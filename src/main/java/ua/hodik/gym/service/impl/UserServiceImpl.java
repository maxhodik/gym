package ua.hodik.gym.service.impl;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.hodik.gym.dto.PasswordDto;
import ua.hodik.gym.dto.UserDto;
import ua.hodik.gym.dto.UserUpdateDto;
import ua.hodik.gym.exception.MyEntityAlreadyExistsException;
import ua.hodik.gym.exception.MyEntityNotFoundException;
import ua.hodik.gym.exception.MyValidationException;
import ua.hodik.gym.model.User;
import ua.hodik.gym.repository.UserRepository;
import ua.hodik.gym.service.UserService;
import ua.hodik.gym.service.mapper.UserMapper;
import ua.hodik.gym.util.impl.validation.MyValidator;

import java.util.List;

@Service
@Log4j2
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final MyValidator validator;
    private final UserMapper userMapper;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, MyValidator validator, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.validator = validator;
        this.userMapper = userMapper;
    }

    @Override
    public List<User> getAllUsers() {
        List<User> users = userRepository.findAll();
        log.info("Finding all users");
        return users;
    }

    @Transactional
    @Override
    public void changePassword(int id, PasswordDto newPassword) {
        User userForUpdate = userRepository.findById(id)
                .orElseThrow(() -> new MyEntityNotFoundException(String.format("User  with id = %s not found", id)));
        userForUpdate.setPassword(newPassword.getPassword());
        log.info("User's password updated. Id= {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public User findByUserName(String userName) {
        validateUserName(userName);
        User foundedUser = userRepository.findByUserName(userName)
                .orElseThrow(() -> new MyEntityNotFoundException(String.format("User  %s not found", userName)));
        log.info("Finding user by userName {}", userName);
        return foundedUser;
    }

    @Override
    public UserDto findUserDtoByUserName(String userName) {
        return userMapper.convertToUserDto(findByUserName(userName));
    }

    @Override
    @Transactional
    public User update(int id, UserUpdateDto userDto) {
        validator.validate(userDto);
        User userToUpdate = findById(id);
        updateUser(userDto, userToUpdate);
        return userToUpdate;
    }

    private void updateUser(UserUpdateDto userDto, User userToUpdate) {
        userToUpdate.setFirstName(userDto.getFirstName());
        userToUpdate.setLastName(userDto.getLastName());
        userToUpdate.setActive(userDto.getIsActive());
    }

    private void checkIfUserNameAllowedToChange(String userNameFromDto, String userNameFromDB) {
        if (!userNameFromDto.equals(userNameFromDB)) {
            if (userRepository.findByUserName(userNameFromDto).isPresent()) {
                throw new MyEntityAlreadyExistsException(String.format("User %s already exists", userNameFromDto));
            }
        }
    }

    private User findById(int id) {
        return userRepository.findById(id).orElseThrow(
                () -> new MyEntityNotFoundException(String.format("User  with id= %s not found", id)));
    }

    private void validateUserName(String value) {
        if (value == null || value.isEmpty()) {
            throw new MyValidationException("UserName can't be null or empty");
        }
    }
}
