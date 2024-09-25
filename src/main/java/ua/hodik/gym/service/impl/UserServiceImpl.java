package ua.hodik.gym.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.hodik.gym.dto.UserDto;
import ua.hodik.gym.exception.EntityAlreadyExistsException;
import ua.hodik.gym.exception.ValidationException;
import ua.hodik.gym.model.User;
import ua.hodik.gym.repository.UserRepository;
import ua.hodik.gym.service.UserService;
import ua.hodik.gym.util.impl.validation.MyValidator;

import java.util.List;
import java.util.Optional;

@Service
@Log4j2
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final MyValidator validator;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, MyValidator validator) {
        this.userRepository = userRepository;
        this.validator = validator;
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

    @Override
    public User update(int id, UserDto userDto) {
        validator.validate(userDto);
        User userToUpdate = findById(id);
        checkIfUserNameAllowedToChange(userDto.getUserName(), userToUpdate.getUserName());
        updateUser(userDto, userToUpdate);
        return userToUpdate;
    }

    private void updateUser(UserDto userDto, User userToUpdate) {
        Optional.ofNullable(userDto).map(UserDto::getFirstName).ifPresent(userToUpdate::setFirstName);
        Optional.ofNullable(userDto).map(UserDto::getLastName).ifPresent(userToUpdate::setLastName);
        Optional.ofNullable(userDto).map(UserDto::getUserName).ifPresent(userToUpdate::setUserName);
        Optional.ofNullable(userDto).map(UserDto::isActive).ifPresent(userToUpdate::setActive);
        Optional.ofNullable(userDto).map(UserDto::getPassword).ifPresent(userToUpdate::setPassword);
    }

    private void checkIfUserNameAllowedToChange(String userNameFromDto, String userNameFromDB) {
        if (!userNameFromDto.equals(userNameFromDB)) {
            if (userRepository.findByUserName(userNameFromDto).isPresent()) {
                throw new EntityAlreadyExistsException(String.format("User %s already exists", userNameFromDto));
            }
        }
    }

    private User findById(int id) {
        return userRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(String.format("User  with id= %s not found", id)));
    }

    private void validateString(String value) {
        if (value == null || value.isEmpty()) {
            throw new ValidationException("UserName can't be null or empty");
        }
    }
}
