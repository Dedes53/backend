package federicolepore.backend.DTO;

import federicolepore.backend.entities.Category;
import federicolepore.backend.entities.Type;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record NewSkillDTO(

        @NotNull(message = "La categoria è obbligatoria")
        Category category,

        @NotNull(message = "Il tipo è obbligatorio")
        Type type, // OWNED / WANTED

        @NotBlank(message = "Il titolo è obbligatorio")
        @Size(min = 2, max = 120, message = "Il titolo deve avere tra 2 e 120 caratteri")
        String title,

        @NotBlank(message = "La descrizione è obbligatoria")
        @Size(min = 10, max = 1000, message = "La descrizione deve avere tra 10 e 1000 caratteri")
        String description
) {
}