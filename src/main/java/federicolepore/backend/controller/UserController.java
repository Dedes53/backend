package federicolepore.backend.controller;

import federicolepore.backend.DTO.UserDTO;
import federicolepore.backend.DTO.UserProfileResponseDTO;
import federicolepore.backend.entities.User;
import federicolepore.backend.services.UserService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    private UserProfileResponseDTO toProfileDTO(User u) {
        return new UserProfileResponseDTO(
                u.getId(),
                u.getUsername(),
                u.getEmail(),
                u.getName(),
                u.getSurname(),
                u.getAvatarUrl()
        );
    }

    @GetMapping("/me")
    public UserProfileResponseDTO getMe(Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();
        return toProfileDTO(currentUser);
    }

    @GetMapping
    public Page<UserProfileResponseDTO> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "username") String sortBy
    ) {
        return userService.findAll(page, size, sortBy).map(this::toProfileDTO);
    }

    @GetMapping("/{userId}")
    public UserProfileResponseDTO getById(@PathVariable UUID userId) {
        return toProfileDTO(userService.findById(userId));
    }

    @PutMapping("/{userId}")
    public UserProfileResponseDTO updateUser(@PathVariable UUID userId, @RequestBody UserDTO body) {
        return toProfileDTO(userService.update(userId, body));
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable UUID userId) {
        userService.delete(userId);
    }
}