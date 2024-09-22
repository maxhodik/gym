package ua.hodik.gym.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.hodik.gym.dto.TraineeDto;
import ua.hodik.gym.dto.UserCredentialDto;
import ua.hodik.gym.dto.mapper.TraineeMapper;
import ua.hodik.gym.exception.EntityAlreadyExistsException;
import ua.hodik.gym.exception.EntityNotFoundException;
import ua.hodik.gym.exception.ValidationException;
import ua.hodik.gym.model.Trainee;
import ua.hodik.gym.model.Trainer;
import ua.hodik.gym.repository.TraineeRepository;
import ua.hodik.gym.repository.TrainerRepository;
import ua.hodik.gym.repository.UserRepository;
import ua.hodik.gym.service.TrainerService;
import ua.hodik.gym.tets.util.TestUtils;
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
    private TrainerRepository trainerRepository;
    @Mock
    private TrainerService trainerService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private MyValidator validator;
    @Mock
    private TraineeMapper traineeMapper;
    @InjectMocks
    private TraineeServiceImpl traineeService;

    @Test
    void createShouldThrowException() {
        //when
        NullPointerException exception = assertThrows(NullPointerException.class, () -> traineeService.createTraineeProfile(null));
        //then
        assertEquals("Trainee can't be null", exception.getMessage());
    }

    @Test
    void create() {
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
    void createShouldTrowValidationException() {
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
    void updateShouldThrowException() {
        //when
        NullPointerException exception = assertThrows(NullPointerException.class,
                () -> traineeService.update(null, traineeDtoWithoutUserName));
        //then
        assertEquals("Credential can't be null", exception.getMessage());
    }

    @Test
    void updateShouldThrowExceptionByTrainerDto() {
        //given
        doNothing().when(validator).validate(any(UserCredentialDto.class));
        when(traineeRepository.findByUserUserName(anyString())).thenReturn(Optional.ofNullable(expectedTrainee));
        doThrow(new ValidationException("Value can't be null")).when(validator).validate(any(TraineeDto.class));
        //when
        assertThrows(ValidationException.class, () -> traineeService.update(userCredentialDto, traineeDtoWithoutUserName));
    }

    @Test
    void updateShouldUpdateWithEqualsUserName() {
        //given
        doNothing().when(validator).validate(any(UserCredentialDto.class));
        when(traineeRepository.findByUserUserName(anyString())).thenReturn(Optional.ofNullable(expectedTrainee));
        doNothing().when(validator).validate(any(TraineeDto.class));
        when(traineeRepository.findById(anyInt())).thenReturn(Optional.ofNullable(expectedTrainee));
        //when
        Trainee updatedTrainee = traineeService.update(userCredentialDto, traineeDtoWithUserName);
        //then
        verify(validator).validate(userCredentialDto);
        verify(traineeRepository).findByUserUserName(userCredentialDto.getUserName());
        verify(validator).validate(traineeDtoWithUserName);
        verify(traineeRepository).findById(ID);

        assertEquals(expectedTrainee, updatedTrainee);
    }

    @Test
    void updateShouldUpdateWithDifferentUserName() {
        //given
        doNothing().when(validator).validate(any(UserCredentialDto.class));
        when(traineeRepository.findByUserUserName(anyString())).thenReturn(Optional.ofNullable(traineeWithId));
        doNothing().when(validator).validate(any(TraineeDto.class));
        when(traineeRepository.findById(anyInt())).thenReturn(Optional.ofNullable(traineeWithId));
        when(userRepository.findByUserName(anyString())).thenReturn(Optional.empty());
        //when
        Trainee updatedTrainee = traineeService.update(userCredentialDto, traineeDtoWithUserName);
        //then
        verify(validator).validate(userCredentialDto);
        verify(traineeRepository).findByUserUserName(userCredentialDto.getUserName());
        verify(validator).validate(traineeDtoWithUserName);
        verify(traineeRepository).findById(ID);
        verify(userRepository).findByUserName(traineeDtoWithUserName.getUserDto().getUserName());
        assertEquals(expectedTrainee, updatedTrainee);
    }

    @Test
    void updateDoubleUserName() {
        //given
        doNothing().when(validator).validate(any(UserCredentialDto.class));
        when(traineeRepository.findByUserUserName(anyString())).thenReturn(Optional.ofNullable(traineeWithId));
        doNothing().when(validator).validate(any(TraineeDto.class));
        when(traineeRepository.findById(anyInt())).thenReturn(Optional.ofNullable(traineeWithId));
        when(userRepository.findByUserName(anyString())).thenReturn(Optional.of(expectedTrainee.getUser()));
        //when
        EntityAlreadyExistsException exception = assertThrows(EntityAlreadyExistsException.class,
                () -> traineeService.update(userCredentialDto, traineeDtoWithUserName));
        //then
        verify(validator).validate(userCredentialDto);
        verify(traineeRepository).findByUserUserName(userCredentialDto.getUserName());
        verify(validator).validate(traineeDtoWithUserName);
        verify(traineeRepository).findById(ID);
        verify(userRepository).findByUserName(traineeDtoWithUserName.getUserDto().getUserName());
        assertEquals("User Sam.Jonson already exists", exception.getMessage());
    }

    @Test
    void deleteShouldThrowException() {
        //when
        NullPointerException exception = assertThrows(NullPointerException.class,
                () -> traineeService.deleteTrainee(null));
        //then
        assertEquals("Credential can't be null", exception.getMessage());
    }

    @Test
    void shouldDeleteTrainee() {
        //given
        doNothing().when(validator).validate(any(UserCredentialDto.class));
        when(traineeRepository.findByUserUserName(anyString())).thenReturn(Optional.of(expectedTrainee));
        //when
        traineeService.deleteTrainee(userCredentialDto);
        //then
        verify(traineeRepository).deleteByUserUserName(userCredentialDto.getUserName());
    }

    @Test
    void updateActiveStatusShouldChange() {
        //given
        doNothing().when(validator).validate(any(UserCredentialDto.class));
        when(traineeRepository.findByUserUserName(anyString())).thenReturn(Optional.of(expectedTrainee));
        //when
        Trainee trainee = traineeService.updateActiveStatus(userCredentialDto, false);
        //then
        verify(traineeRepository, times(2)).findByUserUserName(userCredentialDto.getUserName());
        assertFalse(trainee.getUser().isActive());
    }

    @Test
    void updateActiveStatusShouldNotChange() {
        //given
        doNothing().when(validator).validate(any(UserCredentialDto.class));
        when(traineeRepository.findByUserUserName(anyString())).thenReturn(Optional.of(expectedTrainee));
        //when
        Trainee trainee = traineeService.updateActiveStatus(userCredentialDto, true);
        //then
        verify(traineeRepository, times(2)).findByUserUserName(userCredentialDto.getUserName());
        assertTrue(trainee.getUser().isActive());
    }

    @Test
    void findByIdShouldReturnTrainee() {
        //give
        when(traineeRepository.findById(anyInt())).thenReturn(Optional.ofNullable(expectedTrainee));
        //when
        Trainee traineeById = traineeService.findById(ID);
        //then
        verify(traineeRepository).findById(ID);
        assertEquals(expectedTrainee, traineeById);
    }

    @Test
    void findByIdNotFoundException() {
        //give
        when(traineeRepository.findById(anyInt())).thenReturn(Optional.empty());
        //when
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> traineeService.findById(ID));
        //then
        verify(traineeRepository).findById(ID);
        assertEquals("Trainee id= 1 not found", exception.getMessage());
    }

    @Test
    void findByUsrNameShouldReturnTrainee() {
        //give
        when(traineeRepository.findByUserUserName(anyString())).thenReturn(Optional.ofNullable(expectedTrainee));
        //when
        Trainee trainee = traineeService.findByUserName(USER_NAME);
        //then
        verify(traineeRepository).findByUserUserName(USER_NAME);
        assertEquals(expectedTrainee, trainee);
    }

    @Test
    void findByUserNameNotFoundException() {
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
    void changePassport() {
        //given
        doNothing().when(validator).validate(any(UserCredentialDto.class));
        when(traineeRepository.findByUserUserName(anyString())).thenReturn(Optional.of(expectedTrainee));
        //when
        Trainee trainee = traineeService.changePassword(userCredentialDto, NEW_PASSWORD);
        //then
        assertEquals(NEW_PASSWORD, trainee.getUser().getPassword());
    }

    @Test
    void changeWrongPassportThrowException() {
        //when
        ValidationException exception = assertThrows(ValidationException.class,
                () -> traineeService.changePassword(userCredentialDto, null));
        //then
        assertEquals("Password can't be null or empty", exception.getMessage());
    }

    @Test
    void changeEmptyPassportThrowException() {
        //when
        ValidationException exception = assertThrows(ValidationException.class,
                () -> traineeService.changePassword(userCredentialDto, ""));
        //then
        assertEquals("Password can't be null or empty", exception.getMessage());
    }

    @Test
    void getAllTrainees() {
        //given
        when(traineeRepository.findAll()).thenReturn(expectedTraineeList);
        //when
        List<Trainee> allTrainees = traineeService.getAllTrainees();
        //then
        assertEquals(expectedTraineeList, allTrainees);

    }

    @Test
    void matchCredential() {
        //given
        doNothing().when(validator).validate(any(UserCredentialDto.class));
        when(traineeRepository.findByUserUserName(USER_NAME)).thenReturn(Optional.of(expectedTrainee));
        //when
        boolean b = traineeService.matchCredential(userCredentialDto);
        //then
        verify(traineeRepository).findByUserUserName(USER_NAME);
        assertTrue(b);
    }

    @Test
    void notMatchCredential() {
        //given
        doNothing().when(validator).validate(any(UserCredentialDto.class));
        when(traineeRepository.findByUserUserName(USER_NAME)).thenReturn(Optional.empty());
        //when
        boolean b = traineeService.matchCredential(userCredentialDto);
        //then
        verify(traineeRepository).findByUserUserName(USER_NAME);
        assertFalse(b);
    }

    @Test
    void matchCredentialNull() {
        //when
        NullPointerException exception = assertThrows(NullPointerException.class, () -> traineeService.matchCredential(null));
        //then
        assertEquals("Credential can't be null", exception.getMessage());
    }

    @Test
    void matchCredentialNoValidCredential() {
        //given
        doThrow(new ValidationException()).when(validator).validate(any(UserCredentialDto.class));
        //when
        assertThrows(ValidationException.class, () -> traineeService.matchCredential(userCredentialDto));
    }

    @Test
    void updateTrainersList() {

        //given
        doNothing().when(validator).validate(any(UserCredentialDto.class));
        when(traineeRepository.findByUserUserName(anyString())).thenReturn(Optional.ofNullable(expectedTrainee));
        when(trainerService.findByUserName(anyString())).thenReturn(trainerWithUserName);
        //when
        traineeService.updateTrainersList(userCredentialDto, TRAINER_NAME_LIST);
        //then
        verify(traineeRepository, times(2)).findByUserUserName(userCredentialDto.getUserName());
        verify(trainerService).findByUserName(USER_NAME);
    }

    @Test
    void updateTrainersList_shouldThrowEntityNotFoundException() {

        //given
        doNothing().when(validator).validate(any(UserCredentialDto.class));
        when(traineeRepository.findByUserUserName(anyString()))
                .thenReturn(Optional.ofNullable(expectedTrainee))
                .thenReturn(Optional.empty());

        //when
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            traineeService.updateTrainersList(userCredentialDto, TRAINER_NAME_LIST);
        });
        //then
        verify(traineeRepository, times(2)).findByUserUserName(userCredentialDto.getUserName());
        assertEquals("Trainee Sam.Jonson not found", exception.getMessage());
    }
}