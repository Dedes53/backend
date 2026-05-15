package federicolepore.backend.DTO;

import federicolepore.backend.entities.Category;
import federicolepore.backend.entities.Type;
import jakarta.validation.constraints.Size;

public record UpdateSkillDTO(
        Category category,
        Type type,
        @Size(min = 2, max = 120, message = "Il titolo deve avere tra 2 e 120 caratteri")
        String title,
        @Size(min = 10, max = 1000, message = "La descrizione deve avere tra 10 e 1000 caratteri")
        String description
) {
}
