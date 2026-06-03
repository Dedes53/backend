package federicolepore.backend.services;

import federicolepore.backend.DTO.NewSkillDTO;
import federicolepore.backend.DTO.SkillResponseDTO;
import federicolepore.backend.DTO.UpdateSkillDTO;
import federicolepore.backend.entities.Skill;
import federicolepore.backend.entities.Type;
import federicolepore.backend.entities.User;
import federicolepore.backend.exceptions.NotFoundException;
import federicolepore.backend.exceptions.UnauthorizedException;
import federicolepore.backend.repositories.SkillRepository;
import federicolepore.backend.repositories.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class SkillService {

    private final SkillRepository skillRepository;
    private final UserRepository userRepository;

    public SkillService(SkillRepository skillRepository, UserRepository userRepository) {
        this.skillRepository = skillRepository;
        this.userRepository = userRepository;
    }


    public SkillResponseDTO toDto(Skill s) {
        return new SkillResponseDTO(
                s.getId(),
                s.getTitle(),
                s.getDescription(),
                s.getCategory(),
                s.getType(),
                s.getUser() != null ? s.getUser().getId() : null,
                s.getUser() != null ? s.getUser().getUsername() : null
        );
    }


    public Skill createSkill(NewSkillDTO body, UUID userId) {
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Utente non trovato"));

        Skill s = new Skill();
        s.setTitle(body.title());
        s.setDescription(body.description());
        s.setCategory(body.category());
        s.setType(body.type());
        s.setUser(owner);

        return skillRepository.save(s);
    }


    public Skill findById(UUID skillId) {
        return skillRepository.findById(skillId)
                .orElseThrow(() -> new NotFoundException("Skill non trovata"));
    }


    public List<Skill> findMySkillsByType(UUID userId, Type type) {
        return skillRepository.findByUserIdAndType(userId, type);
    }


    public List<Skill> findUserSkillsByType(UUID userId, Type type) {
        return skillRepository.findByUserIdAndType(userId, type);
    }


    public Skill updateSkill(UUID skillId, UUID userId, UpdateSkillDTO body) {
        Skill s = findById(skillId);

        if (s.getUser() == null || !s.getUser().getId().equals(userId)) {
            throw new UnauthorizedException("Non puoi modificare questa skill");
        }

        s.setTitle(body.title());
        s.setDescription(body.description());
        s.setCategory(body.category());
        s.setType(body.type());

        return skillRepository.save(s);
    }

    public void deleteSkill(UUID skillId, UUID userId) {
        Skill s = findById(skillId);

        if (s.getUser() == null || !s.getUser().getId().equals(userId)) {
            throw new UnauthorizedException("Non puoi eliminare questa skill");
        }

        skillRepository.delete(s);
    }


    public Page<SkillResponseDTO> searchOthersSkills(User currentUser, String query, int page, int size, String sortBy) {
        if (page < 0) page = 0;
        if (size < 1) size = 10;
        if (size > 100) size = 100;

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        String normalizedQuery = query == null ? "" : query.trim().toLowerCase();

        Page<Skill> skillPage = skillRepository.searchOthersByTitle(
                normalizedQuery,
                currentUser.getId(),
                pageable
        );

        return skillPage.map(this::toDto);
    }
}