package ua.hodik.gym.service;

import org.springframework.transaction.annotation.Transactional;
import ua.hodik.gym.dto.PasswordDto;
import ua.hodik.gym.dto.UserDto;
import ua.hodik.gym.model.User;

import java.util.List;

public interface UserService {
    List<User> getAllUsers();

    @Transactional
    void changePassword(int id, PasswordDto newPassword);

    UserDto findByUserName(String userNameDto);

    User update(int id, UserDto userDto);
}
