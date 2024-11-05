package ua.hodik.gym.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ua.hodik.gym.dto.*;
import ua.hodik.gym.exception.EntityNotFoundException;
import ua.hodik.gym.model.Trainee;
import ua.hodik.gym.model.Trainer;
import ua.hodik.gym.monitor.UnassignedTrainerCounterService;
import ua.hodik.gym.service.TraineeService;
import ua.hodik.gym.service.TrainerService;
import ua.hodik.gym.service.TrainingService;
import ua.hodik.gym.tets.util.TestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainerControllerTest {
    private static final int ID = 1;
    public static final String TRAINEE_USERNAME = "Vasya.Lis";
    private final String expectedTrainerPath = "trainer.same.user.name.json";
    private final String trainerDtoPathWithoutUserName = "trainer.dto.without.user.name.json";
    private final String trainerDtoPathWithUserName = "trainer.dto.with.user.name.json";
    private final String filterFormDtoPath = "filter.form.dto.json";
//    private final String trainerUpdateDtoPath = "trainer.update.dto.json";
    private final String userNameDtoPath = "username.dto.json";
    private final String userCredentialDtoPath = "user.credential.dto.json";
    private final String trainingDtoPath = "training.dto.json";
    private final String traineeWithIdPath = "trainee.with.id.json";
    private final Trainee traineeWithId = TestUtils.readFromFile(traineeWithIdPath, Trainee.class);

    private final UserCredentialDto expectedCredential = TestUtils.readFromFile(userCredentialDtoPath, UserCredentialDto.class);
    private final FilterFormDto filterFormDto = TestUtils.readFromFile(filterFormDtoPath, FilterFormDto.class);
    private final Trainer expectedTrainer = TestUtils.readFromFile(expectedTrainerPath, Trainer.class);
    private final TrainerDto trainerDtoWithUserName = TestUtils.readFromFile(trainerDtoPathWithUserName, TrainerDto.class);
    private final TrainerRegistrationDto trainerDtoWithoutUserName = TestUtils.readFromFile(trainerDtoPathWithoutUserName, TrainerRegistrationDto.class);
    private final UserNameDto userNameDto = TestUtils.readFromFile(userNameDtoPath, UserNameDto.class);
    private final TrainingDto trainingDto = TestUtils.readFromFile(trainingDtoPath, TrainingDto.class);
    private final List<TrainingDto> trainingDtoList = List.of(trainingDto);
    private static final String USER_NAME = "Sam.Jonson";


    @Mock
    private TrainerService trainerService;
    @Mock
    private TraineeService traineeService;
    @Mock
    private TrainingService trainingService;
    @Mock
    private UnassignedTrainerCounterService customMetricService;

    @InjectMocks
    private TrainerController trainerController;

    @Test
    void registration_ValidTrainerDto_ReturnUserCredentialDtoResponseOK() {
        //given
        when(trainerService.createTrainerProfile(any(TrainerRegistrationDto.class))).thenReturn(expectedCredential);
        //when
        ResponseEntity<UserCredentialDto> response = trainerController.registration(trainerDtoWithoutUserName);
        //then
        verify(trainerService).createTrainerProfile(trainerDtoWithoutUserName);
        assertEquals(expectedCredential, response.getBody());
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    void updateTrainerActivityStatus_ResponseOk() {
        //given
        doNothing().when(trainerService).updateActiveStatus(anyString(), anyBoolean());
        //when
        ResponseEntity<String> response = trainerController.updateTrainerActivityStatus(USER_NAME, true);
        //then
        verify(trainerService).updateActiveStatus(userNameDto.getUserName(), true);
        assertEquals("Trainer Sam.Jonson active status updated", response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void getTrainer_ReturnTrainerDtoResponseOk() {
        //given
        when(trainerService.findTrainerDtoByUserName(anyString())).thenReturn(trainerDtoWithUserName);
        //when
        ResponseEntity<TrainerDto> response = trainerController.getTrainer(userNameDto);
        //then
        verify(trainerService).findTrainerDtoByUserName(userNameDto.getUserName());
        assertEquals(trainerDtoWithUserName, response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void updateTrainer_ReturnTrainerDtoResponseOk() {
        //given
        when(trainerService.update(anyInt(), any(TrainerDto.class))).thenReturn(trainerDtoWithUserName);
        //when
        ResponseEntity<TrainerDto> trainer = trainerController.updateTrainer(ID, trainerDtoWithUserName);
        //then
        verify(trainerService).update(ID, trainerDtoWithUserName);
        assertEquals(trainerDtoWithUserName, trainer.getBody());
        assertEquals(HttpStatus.OK, trainer.getStatusCode());
    }

    @Test
    void getNotAssignedTrainers_ValidUserName_ResponseOkReturnTrainerDtoList() {
        doNothing().when(customMetricService).incrementCustomMetric();
        when(traineeService.findByUserName(anyString())).thenReturn(traineeWithId);
        when(trainerService.getNotAssignedTrainers(anyString())).thenReturn(List.of(trainerDtoWithUserName));
        //when
        ResponseEntity<List<TrainerDto>> response = trainerController.getNotAssignedTrainers(TRAINEE_USERNAME);
        //then
        verify(traineeService).findByUserName(TRAINEE_USERNAME);
        verify(trainerService).getNotAssignedTrainers(TRAINEE_USERNAME);
        assertEquals(List.of(trainerDtoWithUserName), response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void getNotAssignedTrainers_UserNameNotExists_ThrowException() {
        doNothing().when(customMetricService).incrementCustomMetric();
        when(traineeService.findByUserName(anyString())).thenThrow(EntityNotFoundException.class);
        //when
        assertThrows(EntityNotFoundException.class,
                () -> trainerController.getNotAssignedTrainers(USER_NAME));
        //then
        verify(traineeService).findByUserName(USER_NAME);
        verify(trainingService, times(0)).findAllWithFilters(filterFormDto);
    }

    @Test
    void getTrainerTrainingLis_UserExists_ReturnTrainingDtoResponseOk() {
        //given
        when(trainerService.findByUserName(anyString())).thenReturn(expectedTrainer);
        when(trainingService.findAllWithFilters(any(FilterFormDto.class))).thenReturn(trainingDtoList);
        //when
        ResponseEntity<List<TrainingDto>> response = trainerController.getTrainerTrainingList(userNameDto, filterFormDto);
        //then
        verify(trainerService).findByUserName(USER_NAME);
        verify(trainingService).findAllWithFilters(filterFormDto);
        assertEquals(trainingDtoList, response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void getTrainerTrainingList_NotExistUserName_TrowException() {
        //given
        when(trainerService.findByUserName(anyString())).thenThrow(EntityNotFoundException.class);
        //when
        assertThrows(EntityNotFoundException.class,
                () -> trainerController.getTrainerTrainingList(userNameDto, filterFormDto));
        //then
        verify(trainerService).findByUserName(USER_NAME);
        verify(trainingService, times(0)).findAllWithFilters(filterFormDto);
    }
}