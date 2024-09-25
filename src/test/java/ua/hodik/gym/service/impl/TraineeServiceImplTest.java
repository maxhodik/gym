package ua.hodik.gym.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.hodik.gym.dto.TraineeDto;
import ua.hodik.gym.dto.UserCredentialDto;
import ua.hodik.gym.dto.UserDto;
import ua.hodik.gym.exception.EntityAlreadyExistsException;
import ua.hodik.gym.exception.EntityNotFoundException;
import ua.hodik.gym.exception.ValidationException;
import ua.hodik.gym.model.Trainee;
import ua.hodik.gym.model.Trainer;
import ua.hodik.gym.model.User;
import ua.hodik.gym.repository.TraineeRepository;
import ua.hodik.gym.repository.UserRepository;
import ua.hodik.gym.service.TrainerService;
import ua.hodik.gym.service.UserService;
import ua.hodik.gym.service.mapper.TraineeMapper;
import ua.hodik.gym.tets.util.TestUtils;
import ua.hodik.gym.util.CredentialChecker;
import ua.hodik.gym.util.PasswordGenerator;
import ua.hodik.gym.util.UserNameGenerator;
import ua.hodik.gym.util.impl.validation.MyValidator;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
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
    private final String userCredentialDtoPath = "user.credential.dto.json";
    private final String traineeWithIdPath = "trainee.with.id.json";
    private final String trainerUserName = "trainer.same.user.name.json";
    private final String userPath = "user.json";
    private final String userShortUserNamePath = "user.short.user.name.json";
    private final User expectedUser = TestUtils.readFromFile(userPath, User.class);
    private final User userShortUserName = TestUtils.readFromFile(userShortUserNamePath, User.class);
    private final Trainee traineeWithoutUserName = TestUtils.readFromFile(traineePath, Trainee.class);
    private final TraineeDto traineeDtoWithoutUserName = TestUtils.readFromFile(traineeDtoPath, TraineeDto.class);
    private final TraineeDto traineeDtoWithUserName = TestUtils.readFromFile(traineeDtoWithUserNamePath, TraineeDto.class);
    private final Trainee expectedTrainee = TestUtils.readFromFile(expectedTraineePath, Trainee.class);
    private final Trainee traineeWithId = TestUtils.readFromFile(traineeWithIdPath, Trainee.class);
    private final UserCredentialDto userCredentialDto = TestUtils.readFromFile(userCredentialDtoPath, UserCredentialDto.class);

    private final List<Trainee> expectedTraineeList = List.of(expectedTrainee);
    private final Trainer trainerWithUserName = TestUtils.readFromFile(trainerUserName, Trainer.class);
    private static final List<String> TRAINER_NAME_LIST = List.of("Sam.Jonson");


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
    private UserRepository userRepository;
    @Mock
    private UserService userService;
    @Mock
    private MyValidator validator;
    @Mock
    private TraineeMapper traineeMapper;
    @Mock
    private CredentialChecker credentialChecker;
    @InjectMocks
    private TraineeServiceImpl traineeService;

    @Test
    void create_TraineeIsNull_ThrowException() {
        //when
        NullPointerException exception = assertThrows(NullPointerException.class, () -> traineeService.createTraineeProfile(null));
        //then
        assertEquals("Trainee can't be null", exception.getMessage());
    }

    @Test
    void create_Pass() {
        //given
        doNothing().when(validator).validate(traineeDtoWithoutUserName);
        when(traineeMapper.convertToTrainee(any(TraineeDto.class))).thenReturn(traineeWithoutUserName);
        when(userNameGenerator.generateUserName(FIRST_NAME, LAST_NAME)).thenReturn(FIRST_NAME + "." + LAST_NAME);
        when(passwordGenerator.generatePassword()).thenReturn(PASSWORD);
        when(traineeRepository.save(traineeWithoutUserName)).thenReturn(expectedTrainee);
        //when
        Trainee savedTrainee = traineeService.createTraineeProfile(traineeDtoWithoutUserName);
        //then
        verify(validator).validate(traineeDtoWithoutUserName);
        verify(userNameGenerator).generateUserName(FIRST_NAME, LAST_NAME);
        verify(passwordGenerator).generatePassword();
        verify(traineeRepository).save(expectedTrainee);
        assertEquals(expectedTrainee, savedTrainee);
    }

    @Test
    void create_InvalidTrainee_ThrowValidationException() {
        //given
        doThrow(new ValidationException("Invalid TraineeDto")).when(validator).validate(any(TraineeDto.class));
        //when
        assertThrows(ValidationException.class, () -> traineeService.createTraineeProfile(traineeDtoWithoutUserName));
        //then
        verify(validator).validate(traineeDtoWithoutUserName);
        verify(userNameGenerator, times(0)).generateUserName(FIRST_NAME, LAST_NAME);
        verify(passwordGenerator, times(0)).generatePassword();
        verify(traineeRepository, times(0)).save(expectedTrainee);
    }

    @Test
    void update_CredentialIsNull_ThrowException() {
        //given
        doThrow(new NullPointerException("Credential can't be null")).when(credentialChecker).checkIfMatchCredentialsOrThrow(null);
        //when
        NullPointerException exception = assertThrows(NullPointerException.class,
                () -> traineeService.update(null, traineeDtoWithoutUserName));
        //then
        assertEquals("Credential can't be null", exception.getMessage());
    }

    @Test
    void update_TrainerDtoIsNull_ThrowException() {
        //given
        doNothing().when(credentialChecker).checkIfMatchCredentialsOrThrow(any(UserCredentialDto.class));
        doThrow(new ValidationException("Value can't be null")).when(validator).validate(any(TraineeDto.class));
        //when
        assertThrows(ValidationException.class, () -> traineeService.update(userCredentialDto, traineeDtoWithoutUserName));
    }

    @Test
    void update_EqualsUserName_Pass() {
        //given
        doNothing().when(credentialChecker).checkIfMatchCredentialsOrThrow(any(UserCredentialDto.class));
        doNothing().when(validator).validate(any(TraineeDto.class));
        when(traineeRepository.findById(anyInt())).thenReturn(Optional.ofNullable(expectedTrainee));
        when(userService.update(anyInt(), any(UserDto.class))).thenReturn(expectedUser);
        //when
        Trainee updatedTrainee = traineeService.update(userCredentialDto, traineeDtoWithUserName);
        //then
        verify(credentialChecker).checkIfMatchCredentialsOrThrow(userCredentialDto);
        verify(validator).validate(traineeDtoWithUserName);
        verify(traineeRepository).findById(ID);
        assertEquals(expectedTrainee, updatedTrainee);
    }

    @Test
    void update_DifferentUserName_ReturnTrainee() {
        //given
        doNothing().when(credentialChecker).checkIfMatchCredentialsOrThrow(any(UserCredentialDto.class));
        doNothing().when(validator).validate(any(TraineeDto.class));
        when(traineeRepository.findById(anyInt())).thenReturn(Optional.ofNullable(traineeWithId));
        when(userService.update(anyInt(), any(UserDto.class))).thenReturn(expectedUser);
        //when
        Trainee updatedTrainee = traineeService.update(userCredentialDto, traineeDtoWithUserName);
        //then
        verify(credentialChecker).checkIfMatchCredentialsOrThrow(userCredentialDto);
        verify(validator).validate(traineeDtoWithUserName);
        verify(traineeRepository).findById(ID);
        verify(userService).update(0, traineeDtoWithUserName.getUserDto());
        assertEquals(expectedTrainee, updatedTrainee);
    }

    @Test
    void update_DoubleUserName_ThrowException() {
        //given
        doNothing().when(credentialChecker).checkIfMatchCredentialsOrThrow(any(UserCredentialDto.class));
        doNothing().when(validator).validate(any(TraineeDto.class));
        when(traineeRepository.findById(anyInt())).thenReturn(Optional.ofNullable(traineeWithId));
        when(userService.update(anyInt(), any(UserDto.class))).thenThrow(
                new EntityAlreadyExistsException("User Sam.Jonson already exists"));
        //when
        EntityAlreadyExistsException exception = assertThrows(EntityAlreadyExistsException.class,
                () -> traineeService.update(userCredentialDto, traineeDtoWithUserName));
        //then
        verify(credentialChecker).checkIfMatchCredentialsOrThrow(userCredentialDto);
        verify(validator).validate(traineeDtoWithUserName);
        verify(traineeRepository).findById(ID);
        verify(userService).update(0, traineeDtoWithUserName.getUserDto());
        assertEquals("User Sam.Jonson already exists", exception.getMessage());
    }

    @Test
    void delete_CredentialIsNull_ThrowException() {
        doThrow(new NullPointerException("Credential can't be null")).when(credentialChecker).checkIfMatchCredentialsOrThrow(null);
        //when
        NullPointerException exception = assertThrows(NullPointerException.class,
                () -> traineeService.deleteTrainee(null));
        //then
        assertEquals("Credential can't be null", exception.getMessage());
    }

    @Test
    void deleteTrainee_ValidCredential_Pass() {
        //given
        doNothing().when(credentialChecker).checkIfMatchCredentialsOrThrow(any(UserCredentialDto.class));
        //when
        traineeService.deleteTrainee(userCredentialDto);
        //then
        verify(traineeRepository).deleteByUserUserName(userCredentialDto.getUserName());
    }

    @Test
    void updateActiveStatus_ValidCredential_Pass() {
        //given
        doNothing().when(credentialChecker).checkIfMatchCredentialsOrThrow(any(UserCredentialDto.class));
        when(traineeRepository.findByUserUserName(anyString())).thenReturn(Optional.of(expectedTrainee));
        //when
        Trainee trainee = traineeService.updateActiveStatus(userCredentialDto, false);
        //then
        verify(traineeRepository).findByUserUserName(userCredentialDto.getUserName());
        assertFalse(trainee.getUser().isActive());
    }

    @Test
    void updateActiveStatus_SameStatus_StatusNotChange() {
        //given
        doNothing().when(credentialChecker).checkIfMatchCredentialsOrThrow(any(UserCredentialDto.class));
        when(traineeRepository.findByUserUserName(anyString())).thenReturn(Optional.of(expectedTrainee));
        //when
        Trainee trainee = traineeService.updateActiveStatus(userCredentialDto, true);
        //then
        verify(traineeRepository).findByUserUserName(userCredentialDto.getUserName());
        assertTrue(trainee.getUser().isActive());
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
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> traineeService.findById(ID));
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
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> traineeService.findByUserName(USER_NAME));
        //then
        verify(traineeRepository).findByUserUserName(USER_NAME);
        assertEquals("Trainee Sam.Jonson not found", exception.getMessage());
    }

    @Test
    void changePassword_ValidCredential_ChangePassword() {
        //given
        doNothing().when(credentialChecker).checkIfMatchCredentialsOrThrow(any(UserCredentialDto.class));
        when(traineeRepository.findByUserUserName(anyString())).thenReturn(Optional.of(expectedTrainee));
        //when
        Trainee trainee = traineeService.changePassword(userCredentialDto, NEW_PASSWORD);
        //then
        assertEquals(NEW_PASSWORD, trainee.getUser().getPassword());
    }

    @Test
    void changePassword_WrongPassword_ThrowException() {
        //when
        ValidationException exception = assertThrows(ValidationException.class,
                () -> traineeService.changePassword(userCredentialDto, null));
        //then
        assertEquals("Password can't be null or empty", exception.getMessage());
    }

    @Test
    void changePassword_EmptyPassword_ThrowException() {
        //when
        ValidationException exception = assertThrows(ValidationException.class,
                () -> traineeService.changePassword(userCredentialDto, ""));
        //then
        assertEquals("Password can't be null or empty", exception.getMessage());
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
        doNothing().when(credentialChecker).checkIfMatchCredentialsOrThrow(any(UserCredentialDto.class));
        when(traineeRepository.findByUserUserName(anyString())).thenReturn(Optional.ofNullable(expectedTrainee));
        when(trainerService.findByUserName(anyString())).thenReturn(trainerWithUserName);
        //when
        traineeService.updateTrainersList(userCredentialDto, TRAINER_NAME_LIST);
        //then
        verify(traineeRepository).findByUserUserName(userCredentialDto.getUserName());
        verify(trainerService).findByUserName(USER_NAME);
    }

    @Test
    void updateTrainersList_TraineeNotExists_ThrowEntityNotFoundException() {
        //given
        doNothing().when(credentialChecker).checkIfMatchCredentialsOrThrow(any(UserCredentialDto.class));
        when(traineeRepository.findByUserUserName(anyString())).thenReturn(Optional.empty());
        //when
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            traineeService.updateTrainersList(userCredentialDto, TRAINER_NAME_LIST);
        });
        //then
        verify(traineeRepository).findByUserUserName(userCredentialDto.getUserName());
        assertEquals("Trainee Sam.Jonson not found", exception.getMessage());
    }
}