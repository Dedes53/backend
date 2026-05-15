package federicolepore.backend.services;

import federicolepore.backend.DTO.LoginDTO;
import federicolepore.backend.entities.User;
import federicolepore.backend.exceptions.NotFoundException;
import federicolepore.backend.exceptions.UnauthorizedException;
import federicolepore.backend.security.TokenTools;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserService userService;
    private final TokenTools tokenTools;
    private final PasswordEncoder bcrypt;

    public AuthService(UserService utenteService, TokenTools tokenTools, PasswordEncoder bcrypt) {
        this.userService = utenteService;
        this.tokenTools = tokenTools;
        this.bcrypt = bcrypt;
    }

    public String checkCredentialsAndGenerateToken(LoginDTO body) {
        try {
            User logger = this.userService.findByUsername(body.username());

            if (this.bcrypt.matches(body.password(), logger.getPassword())) {
                return this.tokenTools.generateToken(logger);
            } else {
                throw new UnauthorizedException("Credenziali errate");
            }
        } catch (NotFoundException ex) {
            throw new UnauthorizedException("Credenziali errate");
        }
    }
}
