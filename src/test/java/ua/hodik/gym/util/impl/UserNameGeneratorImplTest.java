package ua.hodik.gym.util.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.hodik.gym.model.Trainee;
import ua.hodik.gym.model.Trainer;
import ua.hodik.gym.service.TraineeService;
import ua.hodik.gym.service.TrainerService;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserNameGeneratorImplTest {
    private final String FIRST_NAME = "Sam";
    private final String LAST_NAME = "Jonson";
    private final String EXPECTED_BASE_NAME = "Sam.Jonson";
    private final String EXPECTED_USER_NAME_1 = "Sam.Jonson1";
    private final String EXPECTED_USER_NAME_2 = "Sam.Jonson2";

    private final String TRAINEE_PATH_DIFFERENT_USER_NAME = "src/test/resources/trainee.json";
    private final String TRAINEE_PATH_SAME_USER_NAME = "src/test/resources/traineeSameUserName.json";
    private final String TRAINER_PATH_DIFFERENT_USER_NAME = "src/test/resources/trainer.json";
    private final String TRAINER_PATH_SAME_USER_NAME = "src/test/resources/trainerSameUserName.json";


    @Mock
    private TraineeService traineeService;
    @Mock
    private TrainerService trainerService;
    @InjectMocks
    private UserNameGeneratorImpl userNameGenerator;
    ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void setUp() {
        objectMapper.registerModule(new JavaTimeModule());
    }


    @Test
    void generateUserNameTwoEqualName() {

        when(traineeService.getAllTrainees()).thenReturn(List.of(getTrainee(TRAINEE_PATH_SAME_USER_NAME)));
        when(trainerService.getAllTrainers()).thenReturn(List.of(getTrainer(TRAINER_PATH_SAME_USER_NAME)));

        String userName = userNameGenerator.generateUserName(FIRST_NAME, LAST_NAME);

        assertEquals(EXPECTED_USER_NAME_2, userName);
    }

    @Test
    void generateUserNameOneEqualNameTrainer() {

        when(traineeService.getAllTrainees()).thenReturn(List.of(getTrainee(TRAINEE_PATH_DIFFERENT_USER_NAME)));
        when(trainerService.getAllTrainers()).thenReturn(List.of(getTrainer(TRAINER_PATH_SAME_USER_NAME)));

        String userName = userNameGenerator.generateUserName(FIRST_NAME, LAST_NAME);

        assertEquals(EXPECTED_USER_NAME_1, userName);
    }

    @Test
    void generateUserNameOneEqualNameTrainee() {

        when(traineeService.getAllTrainees()).thenReturn(List.of(getTrainee(TRAINEE_PATH_SAME_USER_NAME)));
        when(trainerService.getAllTrainers()).thenReturn(List.of(getTrainer(TRAINER_PATH_DIFFERENT_USER_NAME)));

        String userName = userNameGenerator.generateUserName(FIRST_NAME, LAST_NAME);

        assertEquals(EXPECTED_USER_NAME_1, userName);
    }

    @Test
    void generateUserNameShouldReturnBAseName() {

        when(traineeService.getAllTrainees()).thenReturn(List.of(getTrainee(TRAINEE_PATH_DIFFERENT_USER_NAME)));
        when(trainerService.getAllTrainers()).thenReturn(List.of(getTrainer(TRAINER_PATH_DIFFERENT_USER_NAME)));

        String userName = userNameGenerator.generateUserName(FIRST_NAME, LAST_NAME);

        assertEquals(EXPECTED_BASE_NAME, userName);
    }


    private Trainee getTrainee(String filePath) {
        File file = new File(filePath);
        try {
            return objectMapper.readValue(file, Trainee.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Trainer getTrainer(String filePath) {
        File file = new File(filePath);
        try {
            return objectMapper.readValue(file, Trainer.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}