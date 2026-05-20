package federicolepore.backend.controller;

import federicolepore.backend.DTO.UserProfileResponseDTO;
import federicolepore.backend.entities.User;
import federicolepore.backend.services.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService; // ok anche se non usato ora

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public UserProfileResponseDTO getMe(Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();

        return new UserProfileResponseDTO(
                currentUser.getId(),
                currentUser.getUsername(),
                currentUser.getEmail(),
                currentUser.getName(),
                currentUser.getSurname(),
                currentUser.getAvatarUrl()
        );
    }
}