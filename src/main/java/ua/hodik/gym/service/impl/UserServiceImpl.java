package ua.hodik.gym.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.hodik.gym.exception.ValidationException;
import ua.hodik.gym.model.User;
import ua.hodik.gym.repository.UserRepository;
import ua.hodik.gym.service.UserService;

import java.util.List;

@Service
@Log4j2
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<User> getAllUsers() {
        List<User> users = userRepository.findAll();
        log.info("Finding all users");
        return users;
    }

    @Override
    public User findByUserName(String userName) {
        validateString(userName);
        User foundedUser = userRepository.findByUserName(userName)
                .orElseThrow(() -> new EntityNotFoundException(String.format("User  %s not found", userName)));
        log.info("Finding user by userName {}", userName);
        return foundedUser;
    }

    private void validateString(String value) {
        if (value == null || value.isEmpty()) {
            throw new ValidationException("UserName can't be null or empty");
        }
    }
}
