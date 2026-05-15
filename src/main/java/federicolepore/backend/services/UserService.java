package federicolepore.backend.services;

import federicolepore.backend.DTO.UserDTO;
import federicolepore.backend.entities.User;
import federicolepore.backend.exceptions.BadRequestException;
import federicolepore.backend.exceptions.NotFoundException;
import federicolepore.backend.repositories.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder bcrypt;


    public UserService(UserRepository userRepository, PasswordEncoder bcrypt) {
        this.userRepository = userRepository;
        this.bcrypt = bcrypt;
    }


    public User saveNewUser(UserDTO body) {

        if (this.userRepository.existsByEmail(body.email()))
            throw new BadRequestException("La mail " + body.email() + " risulta già associata ad un altro account");

        if (this.userRepository.existsByUsername(body.username()))
            throw new BadRequestException("Lo username " + body.username() + " è già utilizzato da un altro utente");

        User newU = new User(body.username(), bcrypt.encode(body.password()), body.email(), body.name(), body.surname());

        // TODO aggiungere email di conferma registrazione

        return this.userRepository.save(newU);
    }

    public User findById(UUID userID) {
        return this.userRepository.findById(userID).orElseThrow(() -> new NotFoundException("Utente non trovato"));
    }

    public User findByUsername(String username) {
        return this.userRepository.findByUsername(username).orElseThrow(() -> new NotFoundException("Utente non trovato"));
    }
//    possibile find by email

    public Page<User> findAll(int page, int size, String sortBy) {
        if (page < 0) page = 0;
        if (size < 0) size = 1;
        if (size > 100) size = 10;

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));

        return this.userRepository.findAll(pageable);
    }

    public User update(UUID userId, UserDTO body) {
        User toUpdate = this.findById(userId);
        if (!toUpdate.getEmail().equals(body.email()))
            if (this.userRepository.existsByEmail(body.email()))
                throw new BadRequestException("La mail " + body.email() + " risulta già associata ad un altro account");
        if (!toUpdate.getUsername().equals(body.username()))
            if (this.userRepository.existsByUsername(body.username()))
                throw new BadRequestException("Lo username " + body.username() + " è già utilizzato da un altro utente");

        toUpdate.setUsername(body.username());
        toUpdate.setPassword(body.password());
        toUpdate.setEmail(body.email());
        toUpdate.setName(body.name());
        toUpdate.setSurname(body.surname());

        return this.userRepository.save(toUpdate);
    }

    public void delete(UUID userId) {
        this.userRepository.delete(this.findById(userId));
    }

//    public SendEmailDTO sendEmail(User currentAuthenticatedUser, EmailDTO body) {
//        return this.emailSender.sendEmail(currentAuthenticatedUser, body);
//    }

}
