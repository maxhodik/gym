package ua.hodik.gym.service.impl;

import lombok.extern.log4j.Log4j2;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.hodik.gym.dto.PasswordDto;
import ua.hodik.gym.dto.UserDto;
import ua.hodik.gym.dto.UserUpdateDto;
import ua.hodik.gym.exception.MyEntityNotFoundException;
import ua.hodik.gym.model.User;
import ua.hodik.gym.repository.UserRepository;
import ua.hodik.gym.service.UserService;
import ua.hodik.gym.service.mapper.UserMapper;

import java.util.List;

@Service
@Log4j2
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Override
    public List<User> getAllUsers() {
        List<User> users = userRepository.findAll();
        log.info("Found all users");
        return users;
    }

    @Transactional
    @Override
    public void changePassword(int id, PasswordDto newPassword) {
        User userForUpdate = userRepository.findById(id)
                .orElseThrow(() -> new MyEntityNotFoundException(String.format("User  with id = %s not found", id)));
        userForUpdate.setPassword(newPassword.getPassword());
        log.debug("[LoginService] User's password updated. Id= {}. TransactionId {}", id, MDC.get("transactionId"));
    }

    @Override
    @Transactional(readOnly = true)
    public User findByUserName(String userName) {
        User foundedUser = userRepository.findByUserName(userName)
                .orElseThrow(() -> new MyEntityNotFoundException(String.format("User  %s not found", userName)));
        log.debug("[LoginService] Found usr by userName {}. TransactionId {}", userName, MDC.get("transactionId"));
        return foundedUser;
    }

    @Override
    public UserDto findUserDtoByUserName(String userName) {
        return userMapper.convertToUserDto(findByUserName(userName));
    }

    @Override
    @Transactional
    public User update(int id, UserUpdateDto userDto) {
        User userToUpdate = findById(id);
        updateUser(userDto, userToUpdate);
        return userToUpdate;
    }

    private void updateUser(UserUpdateDto userDto, User userToUpdate) {
        userToUpdate.setFirstName(userDto.getFirstName());
        userToUpdate.setLastName(userDto.getLastName());
        userToUpdate.setActive(userDto.getIsActive());
    }


    private User findById(int id) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new MyEntityNotFoundException(String.format("User  with id= %s not found", id)));
        log.debug("[LoginService] Found user by id={}. TransactionId {}", id, MDC.get("transactionId"));
        return user;
    }
}
