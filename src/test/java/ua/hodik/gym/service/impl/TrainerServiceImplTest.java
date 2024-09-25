package ua.hodik.gym.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;
import ua.hodik.gym.dao.TrainerSpecification;
import ua.hodik.gym.dto.TrainerDto;
import ua.hodik.gym.dto.UserCredentialDto;
import ua.hodik.gym.exception.EntityAlreadyExistsException;
import ua.hodik.gym.exception.EntityNotFoundException;
import ua.hodik.gym.exception.ValidationException;
import ua.hodik.gym.model.Trainer;
import ua.hodik.gym.repository.TrainerRepository;
import ua.hodik.gym.repository.UserRepository;
import ua.hodik.gym.service.mapper.TrainerMapper;
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
class TrainerServiceImplTest {
    private static final int ID = 1;
    public static final String PASSWORD = "ABCDEFJxyz";
    private static final String NEW_PASSWORD = "AAAAAAAA";
    private static final String FIRST_NAME = "Sam";
    private static final String LAST_NAME = "Jonson";
    private final String trainerAnotherName = "trainer.json";
    private final String trainerPath = "trainer.without.user.name.json";
    private final String expectedTrainerPath = "trainer.same.user.name.json";
    private final String trainerDtoPathWithoutUserName = "trainer.dto.without.user.name.json";
    private final String trainerDtoPathWithUserName = "trainer.dto.with.user.name.json";
    private final String userCredentialDtoPath = "user.credential.dto.json";


    private final Trainer trainerWithoutUserName = TestUtils.readFromFile(trainerPath, Trainer.class);
    private final Trainer trainerAnotherUserName = TestUtils.readFromFile(trainerAnotherName, Trainer.class);
    private final Trainer expectedTrainer = TestUtils.readFromFile(expectedTrainerPath, Trainer.class);
    private final TrainerDto trainerDtoWithUserName = TestUtils.readFromFile(trainerDtoPathWithUserName, TrainerDto.class);
    private final TrainerDto trainerDtoWithoutUserName = TestUtils.readFromFile(trainerDtoPathWithoutUserName, TrainerDto.class);
    private final UserCredentialDto userCredentialDto = TestUtils.readFromFile(userCredentialDtoPath, UserCredentialDto.class);
    private static final String USER_NAME = "Sam.Jonson";

    public final List<Trainer> expectedTrainerList = List.of(expectedTrainer);
    @Mock
    private PasswordGenerator passwordGenerator;
    @Mock
    private UserNameGenerator userNameGenerator;
    @Mock
    private TrainerRepository trainerRepository;
    @Mock
    private MyValidator validator;
    @Mock
    private TrainerMapper trainerMapper;
    @Mock
    private UserRepository userRepository;
    @Mock
    private TrainerSpecification trainerSpecification;
    @InjectMocks
    private TrainerServiceImpl trainerService;

    @Test
    void createShouldThrowException() {
        //when
        NullPointerException exception = assertThrows(NullPointerException.class, () -> trainerService.createTrainerProfile(null));
        //then
        assertEquals("Trainer can't be null", exception.getMessage());
    }

    @Test
    void create() {
        //given
        doNothing().when(validator).validate(trainerDtoWithoutUserName);
        when(trainerMapper.convertToTrainer(any(TrainerDto.class))).thenReturn(trainerWithoutUserName);
        when(userNameGenerator.generateUserName(FIRST_NAME, LAST_NAME)).thenReturn(FIRST_NAME + "." + LAST_NAME);
        when(passwordGenerator.generatePassword()).thenReturn(PASSWORD);
        when(trainerRepository.save(trainerWithoutUserName)).thenReturn(expectedTrainer);
        //when
        Trainer savedTrainer = trainerService.createTrainerProfile(trainerDtoWithoutUserName);
        //then
        verify(validator).validate(trainerDtoWithoutUserName);
        verify(userNameGenerator).generateUserName(FIRST_NAME, LAST_NAME);
        verify(passwordGenerator).generatePassword();
        verify(trainerRepository).save(expectedTrainer);
        assertEquals(expectedTrainer, savedTrainer);
    }

    @Test
    void createShouldTrowValidationException() {
        //given
        doThrow(new ValidationException("Invalid TrainerDto")).when(validator).validate(any(TrainerDto.class));
        //when
        assertThrows(ValidationException.class, () -> trainerService.createTrainerProfile(trainerDtoWithoutUserName));
        //then
        verify(validator).validate(trainerDtoWithoutUserName);
        verify(userNameGenerator, times(0)).generateUserName(FIRST_NAME, LAST_NAME);
        verify(passwordGenerator, times(0)).generatePassword();
        verify(trainerRepository, times(0)).save(expectedTrainer);
    }

    @Test
    void updateShouldThrowException() {
        //when
        NullPointerException exception = assertThrows(NullPointerException.class,
                () -> trainerService.update(null, trainerDtoWithoutUserName));
        //then
        assertEquals("Credential can't be null", exception.getMessage());
    }

    @Test
    void updateShouldThrowExceptionByTrainerDto() {
        //given
        doNothing().when(validator).validate(any(UserCredentialDto.class));
        when(trainerRepository.findByUserUserName(anyString())).thenReturn(Optional.ofNullable(expectedTrainer));
        doThrow(new ValidationException("Value can't be null")).when(validator).validate(any(TrainerDto.class));
        //when
        assertThrows(ValidationException.class, () -> trainerService.update(userCredentialDto, trainerDtoWithoutUserName));
    }

    @Test
    void updateShouldUpdateWithEqualsUserName() {
        //given
        doNothing().when(validator).validate(any(UserCredentialDto.class));
        when(trainerRepository.findByUserUserName(anyString())).thenReturn(Optional.ofNullable(expectedTrainer));
        doNothing().when(validator).validate(any(TrainerDto.class));
        when(trainerRepository.findById(anyInt())).thenReturn(Optional.ofNullable(expectedTrainer));
        //when
        Trainer updatedTrainer = trainerService.update(userCredentialDto, trainerDtoWithUserName);
        //then
        verify(validator).validate(userCredentialDto);
        verify(trainerRepository).findByUserUserName(userCredentialDto.getUserName());
        verify(validator).validate(trainerDtoWithUserName);
        verify(trainerRepository).findById(ID);

        assertEquals(expectedTrainer, updatedTrainer);
    }

    @Test
    void updateShouldUpdateWithDifferentUserName() {
        //given
        doNothing().when(validator).validate(any(UserCredentialDto.class));
        when(trainerRepository.findByUserUserName(anyString())).thenReturn(Optional.ofNullable(trainerAnotherUserName));
        doNothing().when(validator).validate(any(TrainerDto.class));
        when(trainerRepository.findById(anyInt())).thenReturn(Optional.ofNullable(trainerAnotherUserName));
        when(userRepository.findByUserName(anyString())).thenReturn(Optional.empty());
        //when
        Trainer updatedTrainer = trainerService.update(userCredentialDto, trainerDtoWithUserName);
        //then
        verify(validator).validate(userCredentialDto);
        verify(trainerRepository).findByUserUserName(userCredentialDto.getUserName());
        verify(validator).validate(trainerDtoWithUserName);
        verify(trainerRepository).findById(ID);
        verify(userRepository).findByUserName(trainerDtoWithUserName.getUserDto().getUserName());
        assertEquals(expectedTrainer, updatedTrainer);
    }

    @Test
    void updateDoubleUserName() {
        //given
        doNothing().when(validator).validate(any(UserCredentialDto.class));
        when(trainerRepository.findByUserUserName(anyString())).thenReturn(Optional.ofNullable(trainerAnotherUserName));
        doNothing().when(validator).validate(any(TrainerDto.class));
        when(trainerRepository.findById(anyInt())).thenReturn(Optional.ofNullable(trainerAnotherUserName));
        when(userRepository.findByUserName(anyString())).thenReturn(Optional.of(expectedTrainer.getUser()));
        //when
        EntityAlreadyExistsException exception = assertThrows(EntityAlreadyExistsException.class,
                () -> trainerService.update(userCredentialDto, trainerDtoWithUserName));
        //then
        verify(validator).validate(userCredentialDto);
        verify(trainerRepository).findByUserUserName(userCredentialDto.getUserName());
        verify(validator).validate(trainerDtoWithUserName);
        verify(trainerRepository).findById(ID);
        verify(userRepository).findByUserName(trainerDtoWithUserName.getUserDto().getUserName());
        assertEquals("User Sam.Jonson already exists", exception.getMessage());
    }

    @Test
    void updateActiveStatusShouldChange() {
        //given
        doNothing().when(validator).validate(any(UserCredentialDto.class));
        when(trainerRepository.findByUserUserName(anyString())).thenReturn(Optional.of(expectedTrainer));
        //when
        Trainer trainer = trainerService.updateActiveStatus(userCredentialDto, false);
        //then
        verify(trainerRepository, times(2)).findByUserUserName(userCredentialDto.getUserName());
        assertFalse(trainer.getUser().isActive());
    }

    @Test
    void updateActiveStatusShouldNotChange() {
        //given
        doNothing().when(validator).validate(any(UserCredentialDto.class));
        when(trainerRepository.findByUserUserName(anyString())).thenReturn(Optional.of(expectedTrainer));
        //when
        Trainer trainer = trainerService.updateActiveStatus(userCredentialDto, true);
        //then
        verify(trainerRepository, times(2)).findByUserUserName(userCredentialDto.getUserName());
        assertTrue(trainer.getUser().isActive());
    }

    @Test
    void findByIdShouldReturnTrainer() {
        //give
        when(trainerRepository.findById(anyInt())).thenReturn(Optional.ofNullable(expectedTrainer));
        //when
        Trainer trainerById = trainerService.findById(ID);
        //then
        verify(trainerRepository).findById(ID);
        assertEquals(expectedTrainer, trainerById);
    }

    @Test
    void findByIdNotFoundException() {
        //give
        when(trainerRepository.findById(anyInt())).thenReturn(Optional.empty());
        //when
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> trainerService.findById(ID));
        //then
        verify(trainerRepository).findById(ID);
        assertEquals("Trainer id= 1 not found", exception.getMessage());
    }

    @Test
    void findByUserNameShouldReturnTrainer() {
        //give
        when(trainerRepository.findByUserUserName(anyString())).thenReturn(Optional.ofNullable(expectedTrainer));
        //when
        Trainer trainer = trainerService.findByUserName(USER_NAME);
        //then
        verify(trainerRepository).findByUserUserName(USER_NAME);
        assertEquals(expectedTrainer, trainer);
    }

    @Test
    void findByUserNameNotFoundException() {
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
    void changePassport() {
        //given
        doNothing().when(validator).validate(any(UserCredentialDto.class));
        when(trainerRepository.findByUserUserName(anyString())).thenReturn(Optional.of(expectedTrainer));
        //when
        Trainer trainer = trainerService.changePassword(userCredentialDto, NEW_PASSWORD);
        //then
        assertEquals(NEW_PASSWORD, trainer.getUser().getPassword());
    }

    @Test
    void changeWrongPassportThrowException() {
        //when
        ValidationException exception = assertThrows(ValidationException.class,
                () -> trainerService.changePassword(userCredentialDto, null));
        //then
        assertEquals("Password can't be null or empty", exception.getMessage());
    }

    @Test
    void changeEmptyPassportThrowException() {
        //when
        ValidationException exception = assertThrows(ValidationException.class,
                () -> trainerService.changePassword(userCredentialDto, ""));
        //then
        assertEquals("Password can't be null or empty", exception.getMessage());
    }

    @Test
    void getAllTrainers() {
        //given
        when(trainerRepository.findAll()).thenReturn(expectedTrainerList);
        //when
        List<Trainer> allTrainers = trainerService.getAllTrainers();
        //then
        assertEquals(expectedTrainerList, allTrainers);

    }

//    @Test
//    void matchCredential() {
//        //given
//        doNothing().when(validator).validate(any(UserCredentialDto.class));
//        when(trainerRepository.findByUserUserName(USER_NAME)).thenReturn(Optional.of(expectedTrainer));
//        //when
//        boolean b = trainerService.matchCredential(userCredentialDto);
//        //then
//        verify(trainerRepository).findByUserUserName(USER_NAME);
//        assertTrue(b);
//    }
//
//    @Test
//    void notMatchCredential() {
//        //given
//        doNothing().when(validator).validate(any(UserCredentialDto.class));
//        when(trainerRepository.findByUserUserName(USER_NAME)).thenReturn(Optional.empty());
//        //when
//        boolean b = trainerService.matchCredential(userCredentialDto);
//        //then
//        verify(trainerRepository).findByUserUserName(USER_NAME);
//        assertFalse(b);
//    }
//
//    @Test
//    void matchCredentialNull() {
//        //when
//        NullPointerException exception = assertThrows(NullPointerException.class, () -> trainerService.matchCredential(null));
//        //then
//        assertEquals("Credential can't be null", exception.getMessage());
//    }
//
//    @Test
//    void matchCredentialNoValidCredential() {
//        //given
//        doThrow(new ValidationException()).when(validator).validate(any(UserCredentialDto.class));
//        //when
//        assertThrows(ValidationException.class, () -> trainerService.matchCredential(userCredentialDto));
//    }

    @Test
    void getNotAssignedTrainers_shouldReturnTrainersList() {
        //given
        String traineeName = "validTrainee";
        Specification<Trainer> trainerSpecificationMock = mock(Specification.class);
        List<Trainer> expectedTrainers = List.of(expectedTrainer, expectedTrainer);
        when(trainerSpecification.getTrainer(traineeName)).thenReturn(trainerSpecificationMock);
        when(trainerRepository.findAll(trainerSpecificationMock)).thenReturn(expectedTrainers);
        // when
        List<Trainer> actualTrainers = trainerService.getNotAssignedTrainers(traineeName);
        // then
        assertEquals(expectedTrainers, actualTrainers);
        verify(trainerSpecification).getTrainer(traineeName);
        verify(trainerRepository).findAll(trainerSpecificationMock);
    }

    @Test
    void getNotAssignedTrainers_shouldReturnEmptyList_whenNoTrainersFound() {
        //given
        String traineeName = "validTrainee";
        Specification<Trainer> trainerSpecificationMock = mock(Specification.class);
        when(trainerSpecification.getTrainer(traineeName)).thenReturn(trainerSpecificationMock);
        when(trainerRepository.findAll(trainerSpecificationMock)).thenReturn(List.of());
        //when
        List<Trainer> actualTrainers = trainerService.getNotAssignedTrainers(traineeName);
        //then
        assertTrue(actualTrainers.isEmpty());
        verify(trainerSpecification).getTrainer(traineeName);
        verify(trainerRepository).findAll(trainerSpecificationMock);
    }
}