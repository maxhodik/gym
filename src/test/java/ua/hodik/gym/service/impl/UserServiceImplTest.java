package ua.hodik.gym.service.impl;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.hodik.gym.exception.ValidationException;
import ua.hodik.gym.model.User;
import ua.hodik.gym.repository.UserRepository;
import ua.hodik.gym.tets.util.TestUtils;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    public static final String USER_NAME = "Sam.Jonson";
    public static final String WRONG_USER_NAME = "WrongUserName";
    private final String userPath = "user.json";
    private final User expectedUser = TestUtils.readFromFile(userPath, User.class);
    private final List<User> expectedUserList = List.of(expectedUser);
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void getAllUsers_ReturnUsersList() {
        //given
        when(userRepository.findAll()).thenReturn(expectedUserList);
        //then
        List<User> userList = userService.getAllUsers();
        //
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
    void findByUserName_UserNameIsNull_ThrowException() {
        //when
        ValidationException exception = assertThrows(ValidationException.class, () -> userService.findByUserName(null));
        //then
        assertEquals("UserName can't be null or empty", exception.getMessage());
    }

    @Test
    void findByUserName_EmptyUserName_ThrowException() {
        //when
        ValidationException exception = assertThrows(ValidationException.class, () -> userService.findByUserName(""));
        //then
        assertEquals("UserName can't be null or empty", exception.getMessage());
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
}