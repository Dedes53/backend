package federicolepore.backend.controller;

import federicolepore.backend.DTO.LoginDTO;
import federicolepore.backend.DTO.LoginRespDTO;
import federicolepore.backend.DTO.NewUtenteRespDTO;
import federicolepore.backend.DTO.UserDTO;
import federicolepore.backend.entities.User;
import federicolepore.backend.exceptions.PayloadValidationException;
import federicolepore.backend.services.AuthService;
import federicolepore.backend.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;
    private final UserService userService;

    public AuthController(AuthService authService, UserService utenteService) {
        this.authService = authService;
        this.userService = utenteService;
    }

    @PostMapping("/login")
    public LoginRespDTO login(@RequestBody LoginDTO body) {
        return new LoginRespDTO(this.authService.checkCredentialsAndGenerateToken(body));
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public NewUtenteRespDTO saveUser(@RequestBody @Validated UserDTO body, BindingResult validationResult) {
        if (validationResult.hasErrors()) {
            List<String> errors = validationResult.getFieldErrors().stream()
                    .map(e -> e.getDefaultMessage())
                    .toList();
            throw new PayloadValidationException(errors);
        }

        User saved = this.userService.saveNewUser(body);

        this.authService.sendWelcomeEmail(saved);

        return new NewUtenteRespDTO(saved.getId());
    }

    
}