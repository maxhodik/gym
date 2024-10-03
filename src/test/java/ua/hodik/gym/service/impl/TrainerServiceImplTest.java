package ua.hodik.gym.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;
import ua.hodik.gym.dto.TrainerDto;
import ua.hodik.gym.dto.TrainerUpdateDto;
import ua.hodik.gym.dto.UserCredentialDto;
import ua.hodik.gym.dto.UserUpdateDto;
import ua.hodik.gym.exception.MyEntityNotFoundException;
import ua.hodik.gym.exception.MyValidationException;
import ua.hodik.gym.model.Trainer;
import ua.hodik.gym.model.User;
import ua.hodik.gym.repository.TrainerRepository;
import ua.hodik.gym.service.UserService;
import ua.hodik.gym.service.mapper.TrainerMapper;
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
class TrainerServiceImplTest {
    private static final int ID = 1;
    public static final String PASSWORD = "ABCDEFJxyz";
    private static final String NEW_PASSWORD = "AAAAAAAA";
    private static final String FIRST_NAME = "Sam";
    private static final String LAST_NAME = "Jonson";
    public static final String VALID_TRAINEE = "validTrainee";
    private final String trainerAnotherName = "trainer.json";
    private final String trainerPath = "trainer.without.user.name.json";
    private final String expectedTrainerPath = "trainer.same.user.name.json";
    private final String trainerDtoPathWithoutUserName = "trainer.dto.without.user.name.json";
    private final String trainerDtoPathWithUserName = "trainer.dto.with.user.name.json";
    private final String userCredentialDtoPath = "user.credential.dto.json";
    private final String trainerUpdateDtoPath = "trainer.update.dto.json";
    private final String userPath = "user.json";
    private final User expectedUser = TestUtils.readFromFile(userPath, User.class);


    private final Trainer trainerWithoutUserName = TestUtils.readFromFile(trainerPath, Trainer.class);
    private final Trainer trainerAnotherUserName = TestUtils.readFromFile(trainerAnotherName, Trainer.class);
    private final Trainer expectedTrainer = TestUtils.readFromFile(expectedTrainerPath, Trainer.class);
    private final TrainerDto trainerDtoWithUserName = TestUtils.readFromFile(trainerDtoPathWithUserName, TrainerDto.class);
    private final TrainerDto trainerDtoWithoutUserName = TestUtils.readFromFile(trainerDtoPathWithoutUserName, TrainerDto.class);
    private final TrainerUpdateDto trainerUpdateDto = TestUtils.readFromFile(trainerUpdateDtoPath, TrainerUpdateDto.class);
    private final UserCredentialDto expectedUserCredentialDto = TestUtils.readFromFile(userCredentialDtoPath, UserCredentialDto.class);
    private static final String USER_NAME = "Sam.Jonson";

    public final List<Trainer> expectedTrainerList = List.of(expectedTrainer);
    @Mock
    private PasswordGenerator passwordGenerator;
    @Mock
    private UserNameGenerator userNameGenerator;
    @Mock
    private TrainerRepository trainerRepository;
    @Mock
    private UserService userService;
    @Mock
    private MyValidator validator;
    @Mock
    private TrainerMapper trainerMapper;

    @Mock
    private CredentialChecker credentialChecker;

    @InjectMocks
    private TrainerServiceImpl trainerService;

    @Test
    void create_TrainerIsNull_ThrowException() {
        //when
        NullPointerException exception = assertThrows(NullPointerException.class, () -> trainerService.createTrainerProfile(null));
        //then
        assertEquals("Trainer can't be null", exception.getMessage());
    }

    @Test
    void create_TrainerValid_CreateTrainer() {
        //given
        doNothing().when(validator).validate(trainerDtoWithoutUserName);
        when(trainerMapper.convertToTrainer(any(TrainerDto.class))).thenReturn(trainerWithoutUserName);
        when(userNameGenerator.generateUserName(FIRST_NAME, LAST_NAME)).thenReturn(FIRST_NAME + "." + LAST_NAME);
        when(passwordGenerator.generatePassword()).thenReturn(PASSWORD);
        when(trainerRepository.save(trainerWithoutUserName)).thenReturn(expectedTrainer);
        //when
        UserCredentialDto credential = trainerService.createTrainerProfile(trainerDtoWithoutUserName);
        //then
        verify(validator).validate(trainerDtoWithoutUserName);
        verify(userNameGenerator).generateUserName(FIRST_NAME, LAST_NAME);
        verify(passwordGenerator).generatePassword();
        verify(trainerRepository).save(expectedTrainer);
        assertEquals(expectedUserCredentialDto, credential);
    }

    @Test
    void create_InvalidTrainer_ThrowValidationException() {
        //given
        doThrow(new MyValidationException("Invalid TrainerDto")).when(validator).validate(any(TrainerDto.class));
        //when
        assertThrows(MyValidationException.class, () -> trainerService.createTrainerProfile(trainerDtoWithoutUserName));
        //then
        verify(validator).validate(trainerDtoWithoutUserName);
        verify(userNameGenerator, times(0)).generateUserName(FIRST_NAME, LAST_NAME);
        verify(passwordGenerator, times(0)).generatePassword();
        verify(trainerRepository, times(0)).save(expectedTrainer);
    }



    @Test
    void update_EqualsUserName_Update() {
        //given
//        when(trainerRepository.findByUserUserName(anyString())).thenReturn(Optional.ofNullable(expectedTrainer));
        when(trainerRepository.findById(anyInt())).thenReturn(Optional.ofNullable(expectedTrainer));
        when(userService.update(anyInt(), any(UserUpdateDto.class))).thenReturn(expectedUser);
        when(trainerMapper.convertToTrainerDto(any(Trainer.class))).thenReturn(trainerDtoWithUserName);
        //when
        TrainerDto updatedTrainer = trainerService.update(ID, trainerUpdateDto);
        //then
        verify(trainerRepository).findById(ID);
        assertEquals(trainerDtoWithUserName, updatedTrainer);
    }

    @Test
    void update_DifferentUserName_ReturnTrainer() {
        //given
        when(trainerRepository.findById(anyInt())).thenReturn(Optional.ofNullable(trainerAnotherUserName));
        when(userService.update(anyInt(), any(UserUpdateDto.class))).thenReturn(expectedUser);
        when(trainerMapper.convertToTrainerDto(any(Trainer.class))).thenReturn(trainerDtoWithUserName);
        //when
        TrainerDto updatedTrainer = trainerService.update(ID, trainerUpdateDto);
        //then
        verify(trainerRepository).findById(ID);
        verify(userService).update(0, trainerUpdateDto.getUserUpdateDto());
        assertEquals(trainerDtoWithUserName, updatedTrainer);
    }

    @Test
    void updateActiveStatus_ValidCredential_ChangeStatus() {
        //given
        when(trainerRepository.findByUserUserName(anyString())).thenReturn(Optional.of(expectedTrainer));
        //when
        trainerService.updateActiveStatus(USER_NAME, false);
        //then
        verify(trainerRepository).findByUserUserName(expectedUserCredentialDto.getUserName());
    }

    @Test
    void updateActiveStatus_ValidCredentialTheSameStatus_StatusNotChange() {
        //given
        when(trainerRepository.findByUserUserName(anyString())).thenReturn(Optional.of(expectedTrainer));
        //when
        trainerService.updateActiveStatus(USER_NAME, true);
        //then
        verify(trainerRepository).findByUserUserName(expectedUserCredentialDto.getUserName());
    }

    @Test
    void findById_ReturnTrainer() {
        //give
        when(trainerRepository.findById(anyInt())).thenReturn(Optional.ofNullable(expectedTrainer));
        //when
        Trainer trainerById = trainerService.findById(ID);
        //then
        verify(trainerRepository).findById(ID);
        assertEquals(expectedTrainer, trainerById);
    }

    @Test
    void findById_ThrowNotFoundException() {
        //give
        when(trainerRepository.findById(anyInt())).thenReturn(Optional.empty());
        //when
        MyEntityNotFoundException exception = assertThrows(MyEntityNotFoundException.class, () -> trainerService.findById(ID));
        //then
        verify(trainerRepository).findById(ID);
        assertEquals("Trainer id= 1 not found", exception.getMessage());
    }

    @Test
    void findByUserName_ValidName_ReturnTrainer() {
        //give
        when(trainerRepository.findByUserUserName(anyString())).thenReturn(Optional.ofNullable(expectedTrainer));
        //when
        Trainer trainer = trainerService.findByUserName(USER_NAME);
        //then
        verify(trainerRepository).findByUserUserName(USER_NAME);
        assertEquals(expectedTrainer, trainer);
    }

    @Test
    void findByUserName_ValidName_ThrowNotFoundException() {
        //give
        when(trainerRepository.findByUserUserName(anyString())).thenReturn(Optional.empty());
        //when
        MyEntityNotFoundException exception = assertThrows(MyEntityNotFoundException.class,
                () -> trainerService.findByUserName(USER_NAME));
        //then
        verify(trainerRepository).findByUserUserName(USER_NAME);
        assertEquals("Trainer Sam.Jonson not found", exception.getMessage());
    }

    @Test
    void changePassword_ValidPassword_Change() {
        //given
        doNothing().when(credentialChecker).checkIfMatchCredentialsOrThrow(any(UserCredentialDto.class));
        when(trainerRepository.findByUserUserName(anyString())).thenReturn(Optional.of(expectedTrainer));
        //when
        Trainer trainer = trainerService.changePassword(expectedUserCredentialDto, NEW_PASSWORD);
        //then
        assertEquals(NEW_PASSWORD, trainer.getUser().getPassword());
    }

    @Test
    void change_WrongPassword_ThrowException() {
        //when
        MyValidationException exception = assertThrows(MyValidationException.class,
                () -> trainerService.changePassword(expectedUserCredentialDto, null));
        //then
        assertEquals("Password can't be null or empty", exception.getMessage());
    }

    @Test
    void change_EmptyPassword_ThrowException() {
        //when
        MyValidationException exception = assertThrows(MyValidationException.class,
                () -> trainerService.changePassword(expectedUserCredentialDto, ""));
        //then
        assertEquals("Password can't be null or empty", exception.getMessage());
    }

    @Test
    void getAllTrainers_Pass() {
        //given
        when(trainerRepository.findAll()).thenReturn(expectedTrainerList);
        //when
        List<Trainer> allTrainers = trainerService.getAllTrainers();
        //then
        assertEquals(expectedTrainerList, allTrainers);

    }


    @Test
    void getNotAssignedTrainers_ValidTraineeName_ReturnTrainersList() {
        //given
        Specification<Trainer> trainerSpecificationMock = mock(Specification.class);
        List<Trainer> expectedTrainers = List.of(expectedTrainer, expectedTrainer);
        when(trainerRepository.findAllNotAssignedTrainers(anyString())).thenReturn(expectedTrainers);
        // when
        List<Trainer> actualTrainers = trainerService.getNotAssignedTrainers(VALID_TRAINEE);
        // then
        assertEquals(expectedTrainers, actualTrainers);
        verify(trainerRepository).findAllNotAssignedTrainers(VALID_TRAINEE);
    }

    @Test
    void getNotAssignedTrainers_NoTrainersFound_ReturnEmptyList() {
        //given
        when(trainerRepository.findAllNotAssignedTrainers(anyString())).thenReturn(List.of());
        //when
        List<Trainer> actualTrainers = trainerService.getNotAssignedTrainers(VALID_TRAINEE);
        //then
        assertTrue(actualTrainers.isEmpty());
        verify(trainerRepository).findAllNotAssignedTrainers(VALID_TRAINEE);
    }
}