package ua.hodik.gym.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.hodik.gym.dao.TraineeDao;
import ua.hodik.gym.model.Trainee;
import ua.hodik.gym.tets.util.TestUtils;
import ua.hodik.gym.util.PasswordGenerator;
import ua.hodik.gym.util.UserNameGenerator;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TraineeServiceImplTest {
    private static final int ID = 1;
    private final TestUtils testUtils = new TestUtils();


    private final String TRAINEE_PATH = "src/test/resources/trainee.same.user.name.json";
    private final String EXPECTED_TRAINEE_PATH = "src/test/resources/trainee.same.user.name.json";
    private final Trainee TRAINEE = testUtils.getUser(TRAINEE_PATH, Trainee.class);
    private final Trainee EXPECTED_TRAINEE = testUtils.getUser(EXPECTED_TRAINEE_PATH, Trainee.class);
    public final List<Trainee> EXPECTED_TRAINEE_LIST = List.of(EXPECTED_TRAINEE);


    public static final String PASSWORD = "ABCDEFJxyz";
    private final String FIRST_NAME = "Sam";
    private final String LAST_NAME = "Jonson";
    @Mock
    private PasswordGenerator passwordGenerator;
    @Mock
    private UserNameGenerator userNameGenerator;
    @Mock
    private TraineeDao traineeDao;
    @InjectMocks
    private TraineeServiceImpl traineeService;

    TraineeServiceImplTest() throws JsonProcessingException {
    }


    @Test
    void createShouldThrowException() {
        //when
        NullPointerException exception = assertThrows(NullPointerException.class,
                () -> traineeService.create(null));
        //then
        assertEquals("Trainee can't be null", exception.getMessage());
    }

    @Test
    void create() {
        //given
        when(traineeDao.getMaxId()).thenReturn(0);
        when(userNameGenerator.generateUserName(FIRST_NAME, LAST_NAME)).thenReturn(FIRST_NAME + "." + LAST_NAME);
        when(passwordGenerator.generatePassword()).thenReturn(PASSWORD);
        //when
        traineeService.create(TRAINEE);
        //then
        verify(userNameGenerator).generateUserName(FIRST_NAME, LAST_NAME);
        verify(passwordGenerator).generatePassword();
        verify(traineeDao).add(EXPECTED_TRAINEE);
    }

    @Test
    void updateShouldThrowException() {
        //when
        NullPointerException exception = assertThrows(NullPointerException.class,
                () -> traineeService.update(null, 1));
        //then
        assertEquals("Trainee can't be null", exception.getMessage());
    }

    @Test
    void updateShouldUpdate() {
        traineeService.update(EXPECTED_TRAINEE, EXPECTED_TRAINEE.getUserId());
        verify(traineeDao).update(EXPECTED_TRAINEE, EXPECTED_TRAINEE.getUserId());
    }

    @Test
    void shouldDeleteTrainee() {
        traineeService.delete(EXPECTED_TRAINEE.getUserId());
        verify(traineeDao).delete(EXPECTED_TRAINEE.getUserId());
    }

    @Test
    void findByIdShouldReturnTrainee() {
        //give
        when(traineeDao.getById(ID)).thenReturn(EXPECTED_TRAINEE);
        //when
        Trainee traineeById = traineeService.findById(ID);
        //then
        verify(traineeDao).getById(ID);
        assertEquals(EXPECTED_TRAINEE, traineeById);
    }

    @Test
    void getAllTrainees() {
        //given
        when(traineeDao.getAllTrainees()).thenReturn(List.of(EXPECTED_TRAINEE));
        //when
        List<Trainee> traineeList = traineeService.getAllTrainees();
        //then
        verify(traineeDao).getAllTrainees();
        assertEquals(EXPECTED_TRAINEE_LIST, traineeList);
    }
}