package ua.hodik.gym.service.impl;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.hodik.gym.dto.PasswordDto;
import ua.hodik.gym.dto.UserDto;
import ua.hodik.gym.exception.MyEntityNotFoundException;
import ua.hodik.gym.model.User;
import ua.hodik.gym.repository.UserRepository;
import ua.hodik.gym.service.mapper.UserMapper;
import ua.hodik.gym.tets.util.TestUtils;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    public static final String USER_NAME = "Sam.Jonson";
    public static final String WRONG_USER_NAME = "WrongUserName";
    public static final int ID = 1;
    public static final String PASSWORD = "ABCDEFJxyz";
    private static final PasswordDto NEW_PASSWORD = new PasswordDto("AAAAAAAA");
    public static final int WWRONG_ID = 0;
    private final String userPath = "user.json";
    private final String userDtoPath = "user.dto.json";
    private final User expectedUser = TestUtils.readFromFile(userPath, User.class);
    private final List<User> expectedUserList = List.of(expectedUser);
    private final UserDto expectedUserDto = TestUtils.readFromFile(userDtoPath, UserDto.class);
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapper userMapper;
    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void getAllUsers_ReturnUsersList() {
        //given
        when(userRepository.findAll()).thenReturn(expectedUserList);
        //when
        List<User> userList = userService.getAllUsers();
        //then
        assertEquals(expectedUserList, userList);
        verify(userRepository).findAll();
    }

    @Test
    void findByUserName_ReturnUser() {
        //given
        when(userRepository.findByUserName(anyString())).thenReturn(Optional.ofNullable(expectedUser));
        //when
        User byUserName = userService.findByUserName(USER_NAME);
        //then
        assertEquals(expectedUser, byUserName);
        verify(userRepository).findByUserName(USER_NAME);
    }


    @Test
    void findByUserName_WrongUserName_ThrowException() {
        //given
        when(userRepository.findByUserName(anyString())).thenThrow(new EntityNotFoundException(String.format("User  %s not found", WRONG_USER_NAME)));
        //when
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> userService.findByUserName(WRONG_USER_NAME));
        //then
        assertEquals("User  WrongUserName not found", exception.getMessage());
    }

    @Test
    void changePassword_Valid_ChangePassword() {
        //given
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(expectedUser));
        //when
        userService.changePassword(ID, NEW_PASSWORD);
        //then
        verify(userRepository).findById(ID);
        assertEquals(NEW_PASSWORD.getPassword(), expectedUser.getPassword());

    }

    @Test
    void changePassword_UserNotFound_ThrowException() {
        //given
        when(userRepository.findById(anyInt())).thenReturn(Optional.empty());
        //when
        MyEntityNotFoundException exception = assertThrows(MyEntityNotFoundException.class,
                () -> userService.changePassword(WWRONG_ID, NEW_PASSWORD));
        //then
        verify(userRepository).findById(WWRONG_ID);
        assertEquals("User  with id = 0 not found", exception.getMessage());
    }

}