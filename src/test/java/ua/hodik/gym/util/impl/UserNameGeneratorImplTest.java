package ua.hodik.gym.util.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.hodik.gym.model.Trainee;
import ua.hodik.gym.model.Trainer;
import ua.hodik.gym.service.TraineeService;
import ua.hodik.gym.service.TrainerService;
import ua.hodik.gym.tets.util.TestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserNameGeneratorImplTest {
    public static final String TRAINEE_SHORT_USER_NAME = "trainee.short.user.name";
    private final String FIRST_NAME = "Sam";
    private final String LAST_NAME = "Jonson";
    public static final String SHORT_LAST_NAME = "Jon";
    public static final String EXPECTED_SHORT_USER_NAME = "Sam.Jon";
    public static final String EXPECTED_SHORT_USER_NAME_1 = "Sam.Jon1";

    private final String EXPECTED_BASE_NAME = "Sam.Jonson";
    private final String EXPECTED_USER_NAME_1 = "Sam.Jonson1";
    private final String EXPECTED_USER_NAME_2 = "Sam.Jonson2";

    private final String TRAINEE_PATH_DIFFERENT_USER_NAME = "trainee.json";
    private final String TRAINEE_PATH_SAME_USER_NAME = "trainee.same.user.name.json";
    private final String TRAINER_PATH_DIFFERENT_USER_NAME = "trainer.json";
    private final String TRAINER_PATH_SAME_USER_NAME = "trainer.same.user.name.json";

    @Mock
    private TraineeService traineeService;
    @Mock
    private TrainerService trainerService;
    @InjectMocks
    private UserNameGeneratorImpl userNameGenerator;


    @Test
    void generateUserNameTwoEqualName() {
        //given
        when(traineeService.getAllTrainees()).thenReturn(List.of(TestUtils.readFromFile(TRAINEE_PATH_SAME_USER_NAME, Trainee.class)));
        when(trainerService.getAllTrainers()).thenReturn(List.of(TestUtils.readFromFile(TRAINER_PATH_SAME_USER_NAME, Trainer.class)));
        //when
        String userName = userNameGenerator.generateUserName(FIRST_NAME, LAST_NAME);
        //then
        assertEquals(EXPECTED_USER_NAME_2, userName);
    }

    @Test
    void generateUserNameOneEqualNameTrainer() {
        //given
        when(traineeService.getAllTrainees()).thenReturn(List.of(TestUtils.readFromFile(TRAINEE_PATH_DIFFERENT_USER_NAME, Trainee.class)));
        when(trainerService.getAllTrainers()).thenReturn(List.of(TestUtils.readFromFile(TRAINER_PATH_SAME_USER_NAME, Trainer.class)));
        //when
        String userName = userNameGenerator.generateUserName(FIRST_NAME, LAST_NAME);
        //then
        assertEquals(EXPECTED_USER_NAME_1, userName);
    }

    @Test
    void generateUserNameOneEqualNameTrainee() {
        //given
        when(traineeService.getAllTrainees()).thenReturn(List.of(TestUtils.readFromFile(TRAINEE_PATH_SAME_USER_NAME, Trainee.class)));
        when(trainerService.getAllTrainers()).thenReturn(List.of(TestUtils.readFromFile(TRAINER_PATH_DIFFERENT_USER_NAME, Trainer.class)));
        //when
        String userName = userNameGenerator.generateUserName(FIRST_NAME, LAST_NAME);
        //then
        assertEquals(EXPECTED_USER_NAME_1, userName);
    }

    @Test
    void generateUserNameShouldReturnBAseName() {
        //given
        when(traineeService.getAllTrainees()).thenReturn(List.of(TestUtils.readFromFile(TRAINEE_PATH_DIFFERENT_USER_NAME, Trainee.class)));
        when(trainerService.getAllTrainers()).thenReturn(List.of(TestUtils.readFromFile(TRAINER_PATH_DIFFERENT_USER_NAME, Trainer.class)));
        //when
        String userName = userNameGenerator.generateUserName(FIRST_NAME, LAST_NAME);
        //then
        assertEquals(EXPECTED_BASE_NAME, userName);
    }

    @Test
    void generateUserNameShortName() {
        //given
        when(traineeService.getAllTrainees()).thenReturn(List.of(TestUtils.readFromFile(TRAINEE_PATH_SAME_USER_NAME, Trainee.class)));
        when(trainerService.getAllTrainers()).thenReturn(List.of(TestUtils.readFromFile(TRAINER_PATH_DIFFERENT_USER_NAME, Trainer.class)));
        //when
        String userName = userNameGenerator.generateUserName(FIRST_NAME, SHORT_LAST_NAME);
        //then
        assertEquals(EXPECTED_SHORT_USER_NAME, userName);
    }

    @Test
    void generateUserNameShortName2() {
        //given
        when(traineeService.getAllTrainees()).thenReturn(List.of(TestUtils.readFromFile(TRAINEE_SHORT_USER_NAME, Trainee.class)));
        when(trainerService.getAllTrainers()).thenReturn(List.of(TestUtils.readFromFile(TRAINER_PATH_SAME_USER_NAME, Trainer.class)));
        //when
        String userName = userNameGenerator.generateUserName(FIRST_NAME, SHORT_LAST_NAME);
        //then
        assertEquals(EXPECTED_SHORT_USER_NAME_1, userName);
    }
}