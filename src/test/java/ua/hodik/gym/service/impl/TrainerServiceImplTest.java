package ua.hodik.gym.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;
import ua.hodik.gym.dto.TrainerDto;
import ua.hodik.gym.dto.UserCredentialDto;
import ua.hodik.gym.dto.UserDto;
import ua.hodik.gym.dto.UserNameDto;
import ua.hodik.gym.exception.EntityNotFoundException;
import ua.hodik.gym.model.Trainee;
import ua.hodik.gym.model.Trainer;
import ua.hodik.gym.model.User;
import ua.hodik.gym.repository.TrainerRepository;
import ua.hodik.gym.service.UserService;
import ua.hodik.gym.service.mapper.TrainerMapper;
import ua.hodik.gym.tets.util.TestUtils;
import ua.hodik.gym.util.PasswordGenerator;
import ua.hodik.gym.util.UserNameGenerator;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainerServiceImplTest {
    private static final int ID = 1;
    public static final String PASSWORD = "ABCDEFJxyz";
    private static final String FIRST_NAME = "Sam";
    private static final String LAST_NAME = "Jonson";
    public static final String VALID_TRAINEE = "validTrainee";
    private final String trainerAnotherName = "trainer.json";
    private final String trainerPath = "trainer.without.user.name.json";
    private final String expectedTrainerPath = "trainer.same.user.name.json";
    private final String trainerDtoPathWithoutUserName = "trainer.dto.without.user.name.json";
    private final String trainerDtoPathWithUserName = "trainer.dto.with.user.name.json";
    private final String userCredentialDtoPath = "user.credential.dto.json";
    private final String userDtoPath = "user.dto.json";
    private final String userPath = "user.json";
    private final String userNameDtoPath = "username.dto.json";
    private final String traineeWithIdPath = "trainee.with.id.json";
    private final Trainee traineeWithId = TestUtils.readFromFile(traineeWithIdPath, Trainee.class);
    private final User expectedUser = TestUtils.readFromFile(userPath, User.class);
    private final UserCredentialDto expectedCredential = TestUtils.readFromFile(userCredentialDtoPath, UserCredentialDto.class);
    private final UserDto userDto = TestUtils.readFromFile(userDtoPath, UserDto.class);

    private final Trainer trainerWithoutUserName = TestUtils.readFromFile(trainerPath, Trainer.class);
    private final Trainer trainerAnotherUserName = TestUtils.readFromFile(trainerAnotherName, Trainer.class);
    private final Trainer expectedTrainer = TestUtils.readFromFile(expectedTrainerPath, Trainer.class);
    private final TrainerDto trainerDtoWithUserName = TestUtils.readFromFile(trainerDtoPathWithUserName, TrainerDto.class);
    private final TrainerDto trainerDtoWithoutUserName = TestUtils.readFromFile(trainerDtoPathWithoutUserName, TrainerDto.class);
    private final UserNameDto userNameDto = TestUtils.readFromFile(userNameDtoPath, UserNameDto.class);
    private static final String USER_NAME = "Sam.Jonson";
    private final List<Trainer> expectedTrainers = List.of(expectedTrainer, expectedTrainer);
    private final List<TrainerDto> expectedTrainerDtoList = List.of(trainerDtoWithUserName, trainerDtoWithUserName);
    public final List<Trainer> expectedTrainerList = List.of(expectedTrainer);
    @Mock
    private PasswordGenerator passwordGenerator;
    @Mock
    private UserNameGenerator userNameGenerator;
    @Mock
    private TrainerRepository trainerRepository;
    @Mock
    private TraineeServiceImpl traineeService;
    @Mock
    private UserService userService;
    @Mock
    private TrainerMapper trainerMapper;
    @InjectMocks
    private TrainerServiceImpl trainerService;


    @Test
    void create_TrainerValid_CreateTrainer() {
        //given
        when(trainerMapper.convertToTrainer(any(TrainerDto.class))).thenReturn(trainerWithoutUserName);
        when(userNameGenerator.generateUserName(FIRST_NAME, LAST_NAME)).thenReturn(FIRST_NAME + "." + LAST_NAME);
        when(passwordGenerator.generatePassword()).thenReturn(PASSWORD);
        when(trainerRepository.save(trainerWithoutUserName)).thenReturn(expectedTrainer);
        //when
        UserCredentialDto credential = trainerService.createTrainerProfile(trainerDtoWithoutUserName);
        //then
        verify(userNameGenerator).generateUserName(FIRST_NAME, LAST_NAME);
        verify(passwordGenerator).generatePassword();
        verify(trainerRepository).save(expectedTrainer);
        assertEquals(expectedCredential, credential);
    }

    @Test
    void update_EqualsUserName_Update() {
        //given
        when(trainerRepository.findById(anyInt())).thenReturn(Optional.ofNullable(expectedTrainer));
        when(userService.update(anyInt(), any(UserDto.class))).thenReturn(expectedUser);
        when(trainerMapper.convertToTrainerDto(any(Trainer.class))).thenReturn(trainerDtoWithUserName);
        when(trainerMapper.convertToUserDto(any(TrainerDto.class))).thenReturn(userDto);
        //when
        TrainerDto updatedTrainer = trainerService.update(ID, trainerDtoWithUserName);
        //then
        verify(trainerRepository).findById(ID);
        assertEquals(trainerDtoWithUserName, updatedTrainer);
    }

    @Test
    void update_DifferentUserName_ReturnTrainer() {
        //given
        when(trainerRepository.findById(anyInt())).thenReturn(Optional.ofNullable(trainerAnotherUserName));
        when(userService.update(anyInt(), any(UserDto.class))).thenReturn(expectedUser);
        when(trainerMapper.convertToTrainerDto(any(Trainer.class))).thenReturn(trainerDtoWithUserName);
        when(trainerMapper.convertToUserDto(any(TrainerDto.class))).thenReturn(userDto);
        //when
        TrainerDto updatedTrainer = trainerService.update(ID, trainerDtoWithUserName);
        //then
        verify(trainerRepository).findById(ID);
        verify(userService).update(0, userDto);
        assertEquals(trainerDtoWithUserName, updatedTrainer);
    }

    @Test
    void updateActiveStatus_ValidCredential_ChangeStatus() {
        //given
        when(trainerRepository.findByUserUserName(anyString())).thenReturn(Optional.of(expectedTrainer));
        //when
        trainerService.updateActiveStatus(USER_NAME, false);
        //then
        verify(trainerRepository).findByUserUserName(userNameDto.getUserName());
    }

    @Test
    void updateActiveStatus_ValidCredentialTheSameStatus_StatusNotChange() {
        //given
        when(trainerRepository.findByUserUserName(anyString())).thenReturn(Optional.of(expectedTrainer));
        //when
        trainerService.updateActiveStatus(USER_NAME, true);
        //then
        verify(trainerRepository).findByUserUserName(userNameDto.getUserName());
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
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> trainerService.findById(ID));
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
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> trainerService.findByUserName(USER_NAME));
        //then
        verify(trainerRepository).findByUserUserName(USER_NAME);
        assertEquals("Trainer Sam.Jonson not found", exception.getMessage());
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
        when(traineeService.findByUserName(anyString())).thenReturn(traineeWithId);
        when(trainerRepository.findAllNotAssignedTrainers(anyInt())).thenReturn(expectedTrainers);
        when(trainerMapper.convertToTrainerDto(any(Trainer.class))).thenReturn(trainerDtoWithUserName);
        // when
        List<TrainerDto> actualTrainers = trainerService.getNotAssignedTrainers(VALID_TRAINEE);
        // then
        assertEquals(expectedTrainerDtoList, actualTrainers);
        verify(trainerRepository).findAllNotAssignedTrainers(traineeWithId.getTraineeId());
    }

    @Test
    void getNotAssignedTrainers_NoTrainersFound_ReturnEmptyList() {
        //given
        when(traineeService.findByUserName(anyString())).thenReturn(traineeWithId);
        when(trainerRepository.findAllNotAssignedTrainers(anyInt())).thenReturn(List.of());
        //when
        List<TrainerDto> actualTrainers = trainerService.getNotAssignedTrainers(VALID_TRAINEE);
        //then
        assertTrue(actualTrainers.isEmpty());
        verify(trainerRepository).findAllNotAssignedTrainers(traineeWithId.getTraineeId());
    }

    @Test
    void findTrainerDtoByUserName_ValidUserName_ReturnTrainerDto() {
        //given
        when(trainerRepository.findByUserUserName(anyString())).thenReturn(Optional.ofNullable(expectedTrainer));
        when(trainerMapper.convertToTrainerDto(any(Trainer.class))).thenReturn(trainerDtoWithUserName);
        //when
        TrainerDto trainerDto = trainerService.findTrainerDtoByUserName(USER_NAME);
        //then
        verify(trainerRepository).findByUserUserName(USER_NAME);
        assertEquals(trainerDtoWithUserName, trainerDto);
    }
}