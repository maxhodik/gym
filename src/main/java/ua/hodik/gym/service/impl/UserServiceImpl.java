package ua.hodik.gym.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.hodik.gym.model.User;
import ua.hodik.gym.repository.UserRepository;
import ua.hodik.gym.service.UserService;

import java.util.List;
import java.util.Optional;

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
    public User findByUserName(String userNameDto) {
        Optional<User> optionalUser = userRepository.findByUserName(userNameDto);
        log.info("Finding user by userName", userNameDto);
        return optionalUser.orElseThrow(() -> new EntityNotFoundException(String.format("User  %s not found", userNameDto)));
    }
}
