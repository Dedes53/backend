package federicolepore.backend.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UserDTO(

        @NotBlank(message = "Impostare uno username")
        @Size(min = 2, message = "Non puoi impostare uno username di lunghezza inferiore a 2 caratteri")
        String username,

        @NotBlank(message = "Impostare una password")
        @Size(min = 8, message = "La password deve avere almeno 8 caratteri")
        @Pattern(regexp = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{8,}$", message = "La password deve contenere almeno 8 caratteri, una lettera maiuscola, una minuscola e un numero")
        String password,

        @NotBlank(message = "Inserire un indirizzo email")
        @Email(regexp = "^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$", message = "Formato email non valido")
        String email,

        @NotBlank(message = "Inserire il proprio nome ")
        @Size(min = 2, max = 30, message = "Il nome proprio deve essere compreso tra i 2 e i 30 caratteri")
        String name,

        @NotBlank(message = "Inserire il proprio cognome")
        @Size(min = 2, max = 30, message = "Il cognome deve essere compreso tra i 2 e i 30 caratteri")
        String surname
) {
}
