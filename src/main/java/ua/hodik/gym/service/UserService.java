package ua.hodik.gym.service;

import org.springframework.transaction.annotation.Transactional;
import ua.hodik.gym.dto.PasswordDto;
import ua.hodik.gym.dto.UserDto;
import ua.hodik.gym.dto.UserUpdateDto;
import ua.hodik.gym.model.User;

import java.util.List;

public interface UserService {
    List<User> getAllUsers();

    @Transactional
    void changePassword(int id, PasswordDto newPassword);

    User findByUserName(String userName);

    UserDto findUserDtoByUserName(String userName);

    User update(int id, UserUpdateDto userDto);
}
