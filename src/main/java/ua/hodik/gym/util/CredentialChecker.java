package ua.hodik.gym.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ua.hodik.gym.dto.UserCredentialDto;
import ua.hodik.gym.exception.InvalidCredentialException;
import ua.hodik.gym.model.User;
import ua.hodik.gym.repository.UserRepository;
import ua.hodik.gym.util.impl.validation.MyValidator;

import java.util.Objects;
import java.util.Optional;

@Component
public class CredentialChecker {
    private final UserRepository userRepository;
    private final MyValidator validator;

    @Autowired
    public CredentialChecker(UserRepository userRepository, MyValidator validator) {
        this.userRepository = userRepository;
        this.validator = validator;
    }


    public boolean matchCredential(UserCredentialDto credential) {
        Objects.requireNonNull(credential, "Credential can't be null");
        validator.validate(credential);

        String userName = credential.getUserName();
        Optional<User> user = userRepository.findByUserName(userName);
        return user
                .map(u -> u.getPassword().equals(credential.getPassword()))
                .orElse(false);
    }

    public void checkIfMatchCredentialsOrThrow(UserCredentialDto credential) {
        if (!matchCredential(credential)) {
            throw new InvalidCredentialException("Incorrect credentials, this operation is prohibited");
        }
    }
}
