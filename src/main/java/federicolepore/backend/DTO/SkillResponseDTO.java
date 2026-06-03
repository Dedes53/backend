package federicolepore.backend.DTO;

import federicolepore.backend.entities.Category;
import federicolepore.backend.entities.Type;

import java.util.UUID;

public record SkillResponseDTO(
        UUID id,
        String title,
        String description,
        Category category,
        Type type,
        UUID userId,
        String ownerUsername
) {
}