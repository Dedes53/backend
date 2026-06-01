package federicolepore.backend.controller;

import federicolepore.backend.DTO.NewSkillDTO;
import federicolepore.backend.DTO.SkillResponseDTO;
import federicolepore.backend.DTO.UpdateSkillDTO;
import federicolepore.backend.entities.Skill;
import federicolepore.backend.entities.Type;
import federicolepore.backend.entities.User;
import federicolepore.backend.services.SkillService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/skills")
public class SkillController {

    private final SkillService skillService;

    public SkillController(SkillService skillService) {
        this.skillService = skillService;
    }

    private UUID getLoggedUserId(Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();
        return currentUser.getId();
    }

    private SkillResponseDTO toDto(Skill s) {
        return new SkillResponseDTO(
                s.getId(),
                s.getTitle(),
                s.getDescription(),
                s.getCategory(),
                s.getType()
        );
    }


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SkillResponseDTO createSkill(@RequestBody NewSkillDTO body, Authentication authentication) {
        UUID userId = getLoggedUserId(authentication);
        Skill created = skillService.createSkill(body, userId);
        return toDto(created);
    }


    @GetMapping("/{skillId}")
    public SkillResponseDTO getSkillById(@PathVariable UUID skillId) {
        return toDto(skillService.findById(skillId));
    }


    @GetMapping("/me")
    public List<SkillResponseDTO> getMySkillsByType(
            @RequestParam Type type,
            Authentication authentication
    ) {
        UUID userId = getLoggedUserId(authentication);
        return skillService.findMySkillsByType(userId, type)
                .stream()
                .map(this::toDto)
                .toList();
    }


    @GetMapping("/search")
    public Page<SkillResponseDTO> searchOthersSkills(
            @RequestParam(required = false, defaultValue = "") String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "30") int size,
            @RequestParam(defaultValue = "title") String sortBy,
            Authentication authentication
    ) {
        UUID userId = getLoggedUserId(authentication);
        return skillService.searchOthersSkills(query, userId, page, size, sortBy)
                .map(this::toDto);
    }


    @PutMapping("/{skillId}")
    public SkillResponseDTO updateSkill(
            @PathVariable UUID skillId,
            @RequestBody UpdateSkillDTO body,
            Authentication authentication
    ) {
        UUID userId = getLoggedUserId(authentication);
        Skill updated = skillService.updateSkill(skillId, userId, body);
        return toDto(updated);
    }

    @GetMapping("/user/{userId}")
    public List<SkillResponseDTO> getUserSkillsByType(
            @PathVariable UUID userId,
            @RequestParam Type type
    ) {
        return skillService.findUserSkillsByType(userId, type)
                .stream()
                .map(this::toDto)
                .toList();
    }
    
    @DeleteMapping("/{skillId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSkill(
            @PathVariable UUID skillId,
            Authentication authentication
    ) {
        UUID userId = getLoggedUserId(authentication);
        skillService.deleteSkill(skillId, userId);
    }
}