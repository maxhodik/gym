package ua.hodik.gym.util.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.hodik.gym.model.User;
import ua.hodik.gym.service.UserService;
import ua.hodik.gym.tets.util.TestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserNameGeneratorImplTest {
    private final String FIRST_NAME = "Sam";
    private final String LAST_NAME = "Jonson";
    public static final String SHORT_LAST_NAME = "Jon";
    public static final String EXPECTED_SHORT_USER_NAME = "Sam.Jon";
    public static final String EXPECTED_SHORT_USER_NAME_1 = "Sam.Jon1";

    private final String EXPECTED_BASE_NAME = "Sam.Jonson";
    private final String EXPECTED_USER_NAME_1 = "Sam.Jonson1";
    private final String EXPECTED_USER_NAME_2 = "Sam.Jonson2";

    private final String userSameNamePath = "user.same.user.name.json";
    public static final String shortUserNamePath = "user.short.user.name.json";
    private final User sameName = TestUtils.readFromFile(userSameNamePath, User.class);
    private final User shortName = TestUtils.readFromFile(shortUserNamePath, User.class);

    @Mock
    private UserService userService;
    @InjectMocks
    private UserNameGeneratorImpl userNameGenerator;


    @Test
    void generateUserNameTwoEqualName() {
        //given
        when(userService.getAllUsers()).thenReturn(List.of(sameName, sameName));
        //when
        String userName = userNameGenerator.generateUserName(FIRST_NAME, LAST_NAME);
        //then
        assertEquals(EXPECTED_USER_NAME_2, userName);
    }

    @Test
    void generateUserNameOneEqualNameTrainer() {
        //given
        when(userService.getAllUsers()).thenReturn(List.of(sameName));
        //when
        String userName = userNameGenerator.generateUserName(FIRST_NAME, LAST_NAME);
        //then
        assertEquals(EXPECTED_USER_NAME_1, userName);
    }


    @Test
    void generateUserNameShouldReturnBAseName() {
        //given
        when(userService.getAllUsers()).thenReturn(List.of());
        //when
        String userName = userNameGenerator.generateUserName(FIRST_NAME, LAST_NAME);
        //then
        assertEquals(EXPECTED_BASE_NAME, userName);
    }

    @Test
    void generateUserNameShortName() {
        //given
        when(userService.getAllUsers()).thenReturn(List.of(sameName, sameName));
        //when
        String userName = userNameGenerator.generateUserName(FIRST_NAME, SHORT_LAST_NAME);
        //then
        assertEquals(EXPECTED_SHORT_USER_NAME, userName);
    }

    @Test
    void generateUserNameShortName1() {
        //given
        when(userService.getAllUsers()).thenReturn(List.of(shortName, sameName));
        //when
        String userName = userNameGenerator.generateUserName(FIRST_NAME, SHORT_LAST_NAME);
        //then
        assertEquals(EXPECTED_SHORT_USER_NAME_1, userName);
    }
}