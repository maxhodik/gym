package ua.hodik.gym.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ua.hodik.gym.dto.*;
import ua.hodik.gym.exception.MyEntityNotFoundException;
import ua.hodik.gym.model.Trainee;
import ua.hodik.gym.model.Trainer;
import ua.hodik.gym.model.User;
import ua.hodik.gym.service.TraineeService;
import ua.hodik.gym.service.TrainingService;
import ua.hodik.gym.tets.util.TestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TraineeControllerTest {
    private static final int ID = 1;
    private static final String NEW_PASSWORD = "AAAAAAAA";
    private final String traineePath = "trainee.without.user.name.json";
    private final String expectedTraineePath = "trainee.same.user.name.json";
    private final String traineeDtoPath = "trainee.dto.same.without.user.name.json";
    private final String traineeDtoWithUserNamePath = "trainee.dto.with.user.name.json";
    private final String traineeUpdateDtoPath = "trainee.update.dto.json";
    private final String expectedTrainerPath = "trainer.same.user.name.json";
    private final String invalidTraineeDtoPath = "invalid.trainee.dto.json";


    private final String userCredentialDtoPath = "user.credential.dto.json";
    private final String traineeWithIdPath = "trainee.with.id.json";
    private final String trainerUserName = "trainer.same.user.name.json";
    private final String trainerDtoPathWithUserName = "trainer.dto.with.user.name.json";
    private final String filterFormDtoPath = "filter.form.dto.json";
    private final String trainingDtoPath = "training.dto.json";
    private final String userPath = "user.json";
    private final User expectedUser = TestUtils.readFromFile(userPath, User.class);
    private final Trainee traineeWithoutUserName = TestUtils.readFromFile(traineePath, Trainee.class);
    private final TraineeDto traineeDtoWithoutUserName = TestUtils.readFromFile(traineeDtoPath, TraineeDto.class);
    private final TraineeDto invalidTraineeDto = TestUtils.readFromFile(invalidTraineeDtoPath, TraineeDto.class);
    private final FilterFormDto filterFormDto = TestUtils.readFromFile(filterFormDtoPath, FilterFormDto.class);

    private final TraineeDto traineeDtoWithUserName = TestUtils.readFromFile(traineeDtoWithUserNamePath, TraineeDto.class);
    private final TraineeUpdateDto traineeUpdateDto = TestUtils.readFromFile(traineeUpdateDtoPath, TraineeUpdateDto.class);
    private final TrainerDto trainerDtoWithUserName = TestUtils.readFromFile(trainerDtoPathWithUserName, TrainerDto.class);
    private final Trainee expectedTrainee = TestUtils.readFromFile(expectedTraineePath, Trainee.class);
    private final Trainee traineeWithId = TestUtils.readFromFile(traineeWithIdPath, Trainee.class);
    private final UserCredentialDto expectedUserCredentialDto = TestUtils.readFromFile(userCredentialDtoPath, UserCredentialDto.class);

    private final List<Trainee> expectedTraineeList = List.of(expectedTrainee);
    private final Trainer trainerWithUserName = TestUtils.readFromFile(trainerUserName, Trainer.class);
    private final Trainer expectedTrainer = TestUtils.readFromFile(expectedTrainerPath, Trainer.class);
    public static final String USER_NAME = "Sam.Jonson";
    public static UserNameDto userNameDto = new UserNameDto(USER_NAME);
    private static final List<UserNameDto> userNameDtoList = List.of(userNameDto);
    private final List<Trainer> expectedTrainers = List.of(expectedTrainer);
    private final List<TrainerDto> expectedTrainerDtoList = List.of(trainerDtoWithUserName);
    private final TrainingDto trainingDto = TestUtils.readFromFile(trainingDtoPath, TrainingDto.class);
    private final List<TrainingDto> trainingDtoList = List.of(trainingDto);
    @Mock
    private TraineeService traineeService;
    @Mock
    private TrainingService trainingService;
    @InjectMocks
    private TraineeController traineeController;

    @Test
    void registration_ValidTraineeDto_ReturnUserCredentialDtoResponseOK() {
        //given
        when(traineeService.createTraineeProfile(any(TraineeDto.class))).thenReturn(expectedUserCredentialDto);
        //when
        ResponseEntity<UserCredentialDto> registration = traineeController.registration(traineeDtoWithoutUserName);
        //then
        verify(traineeService).createTraineeProfile(traineeDtoWithoutUserName);
        assertEquals(expectedUserCredentialDto, registration.getBody());
        assertEquals(HttpStatus.CREATED, registration.getStatusCode());
    }

    @Test
    void getTrainee_ValidUserNameDto_TraineeDtoResponseOK() {
        //given
        when(traineeService.findTraineeDtoByUserName(anyString())).thenReturn(traineeDtoWithUserName);
        //when
        ResponseEntity<TraineeDto> trainee = traineeController.getTrainee(userNameDto);
        //then
        verify(traineeService).findTraineeDtoByUserName(userNameDto.getUserName());
        assertEquals(traineeDtoWithUserName, trainee.getBody());
        assertEquals(HttpStatus.OK, trainee.getStatusCode());
    }


    @Test
    void updateTrainee_ValidParams_ResponseOK() {
        //given
        when(traineeService.update(anyInt(), any(TraineeUpdateDto.class))).thenReturn(traineeDtoWithUserName);
        //when
        ResponseEntity<TraineeDto> trainee = traineeController.updateTrainee(ID, traineeUpdateDto);
        //then
        verify(traineeService).update(ID, traineeUpdateDto);
        assertEquals(traineeDtoWithUserName, trainee.getBody());
        assertEquals(HttpStatus.OK, trainee.getStatusCode());
    }

    @Test
    void deleteTrainee_ResponseOK() {
        //given
        doNothing().when(traineeService).deleteTrainee(anyString());
        //when
        ResponseEntity<String> trainee = traineeController.deleteTrainee(userNameDto);
        //then
        verify(traineeService).deleteTrainee(USER_NAME);
        assertEquals("Trainee Sam.Jonson deleted successfully", trainee.getBody());
        assertEquals(HttpStatus.OK, trainee.getStatusCode());
    }

    @Test
    void updateTraineeActivityStatus() {
        //given
        doNothing().when(traineeService).updateActiveStatus(anyString(), anyBoolean());
        //when
        ResponseEntity<String> response = traineeController.updateTraineeActivityStatus(userNameDto, true);
        //then
        verify(traineeService).updateActiveStatus(userNameDto.getUserName(), true);
        assertEquals("Trainee Sam.Jonson active status updated", response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void updateTraineeTrainersList_ResponseOkReturnTrainerDtoList() {
        //given
        when(traineeService.updateTrainersList(anyInt(), any())).thenReturn(expectedTrainerDtoList);
        //when
        ResponseEntity<List<TrainerDto>> response = traineeController.updateTraineeTrainersList(ID, userNameDtoList);
        //then
        verify(traineeService).updateTrainersList(ID, userNameDtoList);
        assertEquals(expectedTrainerDtoList, response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void getTraineeTrainingList_Valid_ResponseOkReturnTrainingDtoList() {
        //given
        when(traineeService.findByUserName(anyString())).thenReturn(expectedTrainee);
        when(trainingService.findAllWithFilters(any(FilterFormDto.class))).thenReturn(trainingDtoList);
        //when
        ResponseEntity<List<TrainingDto>> response = traineeController.getTraineeTrainingList(userNameDto, filterFormDto);
        //then
        verify(traineeService).findByUserName(USER_NAME);
        verify(trainingService).findAllWithFilters(filterFormDto);
        assertEquals(trainingDtoList, response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void getTraineeTrainingList_NotExistUserName_TrowException() {
        //given
        when(traineeService.findByUserName(anyString())).thenThrow(MyEntityNotFoundException.class);
        //when
        MyEntityNotFoundException exception = assertThrows(MyEntityNotFoundException.class,
                () -> traineeController.getTraineeTrainingList(userNameDto, filterFormDto));
        //then
        verify(traineeService).findByUserName(USER_NAME);
        verify(trainingService, times(0)).findAllWithFilters(filterFormDto);
    }
}