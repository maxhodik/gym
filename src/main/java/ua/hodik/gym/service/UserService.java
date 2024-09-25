package ua.hodik.gym.service;

import ua.hodik.gym.dto.UserDto;
import ua.hodik.gym.model.User;

import java.util.List;

public interface UserService {
    List<User> getAllUsers();

    User findByUserName(String userNameDto);

    User update(int id, UserDto userDto);
}
