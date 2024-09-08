package ua.hodik.gym.service.impl;

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
    private final String traineePath = "src/test/resources/trainee.without.user.name.json";
    private final String expectedTraineePath = "src/test/resources/trainee.same.user.name.json";
    private final Trainee trainee = TestUtils.readFromFile(traineePath, Trainee.class);
    private final Trainee expectedTrainee = TestUtils.readFromFile(expectedTraineePath, Trainee.class);
    public final List<Trainee> expectedTraineeList = List.of(expectedTrainee);


    public static final String PASSWORD = "ABCDEFJxyz";
    private static final String FIRST_NAME = "Sam";
    private static final String LAST_NAME = "Jonson";
    @Mock
    private PasswordGenerator passwordGenerator;
    @Mock
    private UserNameGenerator userNameGenerator;
    @Mock
    private TraineeDao traineeDao;
    @InjectMocks
    private TraineeServiceImpl traineeService;

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
        traineeService.create(trainee);
        //then
        verify(userNameGenerator).generateUserName(FIRST_NAME, LAST_NAME);
        verify(passwordGenerator).generatePassword();
        verify(traineeDao).add(expectedTrainee);
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
        traineeService.update(expectedTrainee, expectedTrainee.getUserId());
        verify(traineeDao).update(expectedTrainee, expectedTrainee.getUserId());
    }

    @Test
    void shouldDeleteTrainee() {
        traineeService.delete(expectedTrainee.getUserId());
        verify(traineeDao).delete(expectedTrainee.getUserId());
    }

    @Test
    void findByIdShouldReturnTrainee() {
        //give
        when(traineeDao.getById(ID)).thenReturn(expectedTrainee);
        //when
        Trainee traineeById = traineeService.findById(ID);
        //then
        verify(traineeDao).getById(ID);
        assertEquals(expectedTrainee, traineeById);
    }

    @Test
    void getAllTrainees() {
        //given
        when(traineeDao.getAllTrainees()).thenReturn(List.of(expectedTrainee));
        //when
        List<Trainee> traineeList = traineeService.getAllTrainees();
        //then
        verify(traineeDao).getAllTrainees();
        assertEquals(expectedTraineeList, traineeList);
    }
}