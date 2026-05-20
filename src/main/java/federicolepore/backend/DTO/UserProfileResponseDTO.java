package federicolepore.backend.DTO;

import java.util.UUID;

public record UserProfileResponseDTO(
        UUID id,
        String username,
        String email,
        String name,
        String surname
) {
}
