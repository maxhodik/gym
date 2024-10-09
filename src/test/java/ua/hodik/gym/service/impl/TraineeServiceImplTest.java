package ua.hodik.gym.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.hodik.gym.dto.*;
import ua.hodik.gym.exception.MyEntityNotFoundException;
import ua.hodik.gym.model.Trainee;
import ua.hodik.gym.model.Trainer;
import ua.hodik.gym.model.User;
import ua.hodik.gym.repository.TraineeRepository;
import ua.hodik.gym.service.TrainerService;
import ua.hodik.gym.service.UserService;
import ua.hodik.gym.service.mapper.TraineeMapper;
import ua.hodik.gym.service.mapper.TrainerMapper;
import ua.hodik.gym.tets.util.TestUtils;
import ua.hodik.gym.util.PasswordGenerator;
import ua.hodik.gym.util.UserNameGenerator;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TraineeServiceImplTest {
    private static final int ID = 1;
    private static final String NEW_PASSWORD = "AAAAAAAA";
    private final String traineePath = "trainee.without.user.name.json";
    private final String expectedTraineePath = "trainee.same.user.name.json";
    private final String traineeDtoPath = "trainee.dto.same.without.user.name.json";
    private final String traineeDtoWithUserNamePath = "trainee.dto.with.user.name.json";
    private final String traineeUpdateDtoPath = "trainee.update.dto.json";
    private final String expectedTrainerPath = "trainer.same.user.name.json";

    private final String userCredentialDtoPath = "user.credential.dto.json";
    private final String traineeWithIdPath = "trainee.with.id.json";
    private final String trainerUserName = "trainer.same.user.name.json";
    private final String trainerDtoPathWithUserName = "trainer.dto.with.user.name.json";
    private final String userPath = "user.json";
    private final User expectedUser = TestUtils.readFromFile(userPath, User.class);
    private final Trainee traineeWithoutUserName = TestUtils.readFromFile(traineePath, Trainee.class);
    private final TraineeDto traineeDtoWithoutUserName = TestUtils.readFromFile(traineeDtoPath, TraineeDto.class);
    private final TraineeDto traineeDtoWithUserName = TestUtils.readFromFile(traineeDtoWithUserNamePath, TraineeDto.class);
    private final TraineeUpdateDto traineeUpdateDto = TestUtils.readFromFile(traineeUpdateDtoPath, TraineeUpdateDto.class);
    private final TrainerDto trainerDtoWithUserName = TestUtils.readFromFile(trainerDtoPathWithUserName, TrainerDto.class);
    private final Trainee expectedTrainee = TestUtils.readFromFile(expectedTraineePath, Trainee.class);
    private final Trainee traineeWithId = TestUtils.readFromFile(traineeWithIdPath, Trainee.class);
    private final UserCredentialDto expectedUserCredentialDto = TestUtils.readFromFile(userCredentialDtoPath, UserCredentialDto.class);

    private final List<Trainee> expectedTraineeList = List.of(expectedTrainee);
    private final Trainer trainerWithUserName = TestUtils.readFromFile(trainerUserName, Trainer.class);
    private final Trainer expectedTrainer = TestUtils.readFromFile(expectedTrainerPath, Trainer.class);
    private static final List<UserNameDto> TRAINER_NAME_LIST = List.of(new UserNameDto("Sam.Jonson"));
    private List<Trainer> expectedTrainers = List.of(expectedTrainer);
    private List<TrainerDto> expectedTrainerDtoList = List.of(trainerDtoWithUserName);


    public static final String PASSWORD = "ABCDEFJxyz";
    private static final String FIRST_NAME = "Sam";
    private static final String LAST_NAME = "Jonson";
    private static final String USER_NAME = "Sam.Jonson";
    @Mock
    private PasswordGenerator passwordGenerator;
    @Mock
    private UserNameGenerator userNameGenerator;
    @Mock
    private TraineeRepository traineeRepository;
    @Mock
    private TrainerService trainerService;
    @Mock
    private UserService userService;
    @Mock
    private TraineeMapper traineeMapper;
    @Mock
    private TrainerMapper trainerMapper;
    @InjectMocks
    private TraineeServiceImpl traineeService;


    @Test
    void create_ReturnUserCredentialDto() {
        //given
        when(traineeMapper.convertToTrainee(any(TraineeDto.class))).thenReturn(traineeWithoutUserName);
        when(userNameGenerator.generateUserName(FIRST_NAME, LAST_NAME)).thenReturn(FIRST_NAME + "." + LAST_NAME);
        when(passwordGenerator.generatePassword()).thenReturn(PASSWORD);
        when(traineeRepository.save(traineeWithoutUserName)).thenReturn(expectedTrainee);
        //when
        UserCredentialDto credentialDto = traineeService.createTraineeProfile(traineeDtoWithoutUserName);
        //then
        verify(userNameGenerator).generateUserName(FIRST_NAME, LAST_NAME);
        verify(passwordGenerator).generatePassword();
        verify(traineeRepository).save(expectedTrainee);
        assertEquals(expectedUserCredentialDto, credentialDto);
    }

    @Test
    void update_EqualsUserName_Pass() {
        //given
        when(traineeRepository.findById(anyInt())).thenReturn(Optional.ofNullable(expectedTrainee));
        when(userService.update(anyInt(), any(UserUpdateDto.class))).thenReturn(expectedUser);
        when(traineeMapper.convertToTraineeDto(any(Trainee.class))).thenReturn(traineeDtoWithUserName);

        //when
        TraineeDto updatedTrainee = traineeService.update(ID, traineeUpdateDto);
        //then
        verify(traineeRepository).findById(ID);
        assertEquals(traineeDtoWithUserName, updatedTrainee);
    }

    @Test
    void update_DifferentUserName_ReturnTraineeDto() {
        //given
        when(traineeRepository.findById(anyInt())).thenReturn(Optional.ofNullable(traineeWithId));
        when(userService.update(anyInt(), any(UserUpdateDto.class))).thenReturn(expectedUser);
        when(traineeMapper.convertToTraineeDto(any(Trainee.class))).thenReturn(traineeDtoWithUserName);
        //when
        TraineeDto updatedTrainee = traineeService.update(ID, traineeUpdateDto);
        //then
        verify(traineeRepository).findById(ID);
        verify(userService).update(0, traineeUpdateDto.getUserUpdateDto());
        assertEquals(traineeDtoWithUserName, updatedTrainee);
    }


    @Test
    void deleteTrainee_ValidCredential_Pass() {
        //when
        traineeService.deleteTrainee(USER_NAME);
        //then
        verify(traineeRepository).deleteByUserUserName(expectedUserCredentialDto.getUserName());
    }

    @Test
    void updateActiveStatus_ValidCredential_Pass() {
        //given
        when(traineeRepository.findByUserUserName(anyString())).thenReturn(Optional.of(expectedTrainee));
        //when
        traineeService.updateActiveStatus(USER_NAME, false);
        //then
        verify(traineeRepository).findByUserUserName(expectedUserCredentialDto.getUserName());
    }

    @Test
    void updateActiveStatus_SameStatus_StatusNotChange() {
        //given
        when(traineeRepository.findByUserUserName(anyString())).thenReturn(Optional.of(expectedTrainee));
        //when
        traineeService.updateActiveStatus(USER_NAME, true);
        //then
        verify(traineeRepository).findByUserUserName(expectedUserCredentialDto.getUserName());
    }

    @Test
    void findById_ReturnTrainee() {
        //give
        when(traineeRepository.findById(anyInt())).thenReturn(Optional.ofNullable(expectedTrainee));
        //when
        Trainee traineeById = traineeService.findById(ID);
        //then
        verify(traineeRepository).findById(ID);
        assertEquals(expectedTrainee, traineeById);
    }

    @Test
    void findById_ThrowNotFoundException() {
        //give
        when(traineeRepository.findById(anyInt())).thenReturn(Optional.empty());
        //when
        MyEntityNotFoundException exception = assertThrows(MyEntityNotFoundException.class, () -> traineeService.findById(ID));
        //then
        verify(traineeRepository).findById(ID);
        assertEquals("Trainee id= 1 not found", exception.getMessage());
    }

    @Test
    void findByUserName_ValidName_ReturnTrainee() {
        //give
        when(traineeRepository.findByUserUserName(anyString())).thenReturn(Optional.ofNullable(expectedTrainee));
        //when
        Trainee trainee = traineeService.findByUserName(USER_NAME);
        //then
        verify(traineeRepository).findByUserUserName(USER_NAME);
        assertEquals(expectedTrainee, trainee);
    }

    @Test
    void findByUserName_ThrowNotFoundException() {
        //give
        when(traineeRepository.findByUserUserName(anyString())).thenReturn(Optional.empty());
        //when
        MyEntityNotFoundException exception = assertThrows(MyEntityNotFoundException.class,
                () -> traineeService.findByUserName(USER_NAME));
        //then
        verify(traineeRepository).findByUserUserName(USER_NAME);
        assertEquals("Trainee Sam.Jonson not found", exception.getMessage());
    }



    @Test
    void getAllTrainees_Pass() {
        //given
        when(traineeRepository.findAll()).thenReturn(expectedTraineeList);
        //when
        List<Trainee> allTrainees = traineeService.getAllTrainees();
        //then
        assertEquals(expectedTraineeList, allTrainees);

    }


    @Test
    void updateTrainersList_ValidCredential_UpdateList() {

        //given
        when(traineeRepository.findById(anyInt())).thenReturn(Optional.ofNullable(expectedTrainee));
        when(trainerService.findByUserName(anyString())).thenReturn(trainerWithUserName);
        when(trainerMapper.convertToTrainerDto(trainerWithUserName)).thenReturn(trainerDtoWithUserName);
        //when
        List<TrainerDto> trainerDtoList = traineeService.updateTrainersList(ID, TRAINER_NAME_LIST);
        //then
        verify(traineeRepository).findById(ID);
        verify(trainerService).findByUserName(USER_NAME);
        assertEquals(expectedTrainerDtoList, trainerDtoList);
    }

    @Test
    void updateTrainersList_TraineeNotExists_ThrowEntityNotFoundException() {
        //given
        when(traineeRepository.findById(anyInt())).thenReturn(Optional.empty());
        //when
        MyEntityNotFoundException exception = assertThrows(MyEntityNotFoundException.class, () ->
                traineeService.updateTrainersList(ID, TRAINER_NAME_LIST));
        //then
        verify(traineeRepository).findById(ID);
        assertEquals("Trainee id= 1 not found", exception.getMessage());
    }

    @Test
    void findTraineeDtoByUserName_ValidUserName_ReturnTraineeDto() {
        //given
        when(traineeRepository.findByUserUserName(anyString())).thenReturn(Optional.ofNullable(expectedTrainee));
        when(traineeMapper.convertToTraineeDto(any(Trainee.class))).thenReturn(traineeDtoWithUserName);
        //when
        TraineeDto traineeDto = traineeService.findTraineeDtoByUserName(USER_NAME);
        //then
        verify(traineeRepository).findByUserUserName(USER_NAME);
        assertEquals(traineeDtoWithUserName, traineeDto);
    }


}