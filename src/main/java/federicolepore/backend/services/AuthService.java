package federicolepore.backend.services;

import federicolepore.backend.DTO.LoginDTO;
import federicolepore.backend.entities.User;
import federicolepore.backend.exceptions.NotFoundException;
import federicolepore.backend.exceptions.UnauthorizedException;
import federicolepore.backend.security.TokenTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final UserService userService;
    private final TokenTools tokenTools;
    private final PasswordEncoder bcrypt;
    private final MailgunService mailgunService;

    public AuthService(UserService utenteService,
                       TokenTools tokenTools,
                       PasswordEncoder bcrypt,
                       MailgunService mailgunService) {
        this.userService = utenteService;
        this.tokenTools = tokenTools;
        this.bcrypt = bcrypt;
        this.mailgunService = mailgunService;
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

    public void sendWelcomeEmail(User user) {
        if (user == null || user.getEmail() == null || user.getEmail().isBlank()) return;

        String fullName = (user.getName() != null && !user.getName().isBlank())
                ? user.getName()
                : user.getUsername();

        String loginUrl = "https://capstone-frontend-six.vercel.app/login"; // se vuoi dopo lo leggiamo da @Value
        String subject = "Benvenuto su SkillSwap 🎉";

        String text = """
                Ciao %s,
                
                la tua registrazione a SkillSwap è andata a buon fine.
                Ora puoi completare il profilo, aggiungere skill e trovare match reciproci.
                
                Accedi qui: %s
                
                A presto,
                Team SkillSwap
                """.formatted(fullName, loginUrl);

        String html = """
                <html>
                  <body style="font-family:Arial,sans-serif;background:#f8fbff;padding:24px;">
                    <div style="max-width:560px;margin:0 auto;background:#ffffff;border:1px solid #e5e7eb;border-radius:12px;padding:20px;">
                      <h2 style="margin:0 0 12px;color:#111;">Benvenuto su SkillSwap 🎉</h2>
                      <p style="margin:0 0 10px;color:#374151;">Ciao <strong>%s</strong>,</p>
                      <p style="margin:0 0 10px;color:#374151;">
                        la tua registrazione è andata a buon fine.
                      </p>
                      <p style="margin:0 0 16px;color:#374151;">
                        Ora puoi completare il profilo, aggiungere skill e trovare match reciproci.
                      </p>
                      <a href="https://capstone-frontend-six.vercel.app/"
                         style="display:inline-block;background:#147BFD;color:#fff;text-decoration:none;padding:10px 14px;border-radius:8px;font-weight:700;">
                         Vai a SkillSwap
                      </a>
                      <p style="margin:18px 0 0;color:#6b7280;font-size:12px;">Team SkillSwap</p>
                    </div>
                  </body>
                </html>
                """.formatted(fullName, loginUrl);

        try {
            mailgunService.sendEmail(user.getEmail(), subject, text, html);
        } catch (Exception ex) {
            log.error("Invio welcome email fallito per userId={}, email={}", user.getId(), user.getEmail(), ex);
        }
    }


}