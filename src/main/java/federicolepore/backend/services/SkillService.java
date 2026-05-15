package federicolepore.backend.services;

import federicolepore.backend.DTO.NewSkillDTO;
import federicolepore.backend.DTO.UpdateSkillDTO;
import federicolepore.backend.entities.Skill;
import federicolepore.backend.entities.Type;
import federicolepore.backend.entities.User;
import federicolepore.backend.exceptions.BadRequestException;
import federicolepore.backend.exceptions.NotFoundException;
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

    public User getUser(UUID userIdFromToken) {
        return userRepository.findById(userIdFromToken).orElseThrow(() -> new NotFoundException("Utente non trovato"));
    }

    public Skill findById(UUID skillId) {
        return this.skillRepository.findById(skillId).orElseThrow(() -> new NotFoundException("Skill non trovata"));
    }

    public Skill createSkill(NewSkillDTO body, UUID userId) {
        User loggedUser = this.getUser(userId);

        Skill newS = new Skill(body.category(), body.type(), body.title(), body.description());

        loggedUser.addSkill(newS);
        userRepository.save(loggedUser);

        return newS;
    }

    //    PER POPOLARE PAGINA PROFILO UTENTE
    //    in questo modo il fe dovrà fare due chiamate distinte per avere le skill OWNED e WANTED
    //    in alternativa fetch unica e poi se le ordina il fe
    public List<Skill> findMySkillsByType(UUID userId, Type type) {
        User loggedUser = this.getUser(userId);
        return this.skillRepository.findByUserIdAndType(loggedUser.getId(), type);
    }


    //    PER LA RICERCA DI SKILL DI ALTRI UTENTI
    //    al momento solo tramite title TODO implementare i filtri per categoria
    public Page<Skill> searchOthersSkills(String query, UUID userId, int page, int size, String sortBy) {
        User loggedUser = getUser(userId);

        if (page < 0) page = 0;
        if (size < 0) size = 1;
        if (size > 100) size = 30;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));

        String q = (query == null) ? "" : query.trim().toLowerCase();
        return skillRepository.searchOthersByTitle(q, loggedUser.getId(), pageable);
    }


    public Skill updateSkill(UUID skillId, UUID userId, UpdateSkillDTO body) {
        Skill skillToUpdate = this.findById(skillId);

        if (!skillToUpdate.getUser().getId().equals(userId)) {
            throw new BadRequestException("Non puoi modificare una Skill che non ti appartiene");
        }

        if (body.category() != null) {
            skillToUpdate.setCategory(body.category());
        }

        if (body.type() != null) {
            skillToUpdate.setType(body.type());
        }

        if (body.title() != null && !body.title().isBlank()) {
            skillToUpdate.setTitle(body.title().trim());
        }

        if (body.description() != null && !body.description().isBlank()) {
            skillToUpdate.setDescription(body.description().trim());
        }

        return skillRepository.save(skillToUpdate);
    }


    public void deleteSkill(UUID skillId, UUID userID) {
        Skill toDelete = this.findById(skillId);
        if (!toDelete.getUser().getId().equals(userID))
            throw new BadRequestException("Non puoi cancellare una Skill che non ti appartiene");

        this.skillRepository.delete(toDelete);
    }


}
