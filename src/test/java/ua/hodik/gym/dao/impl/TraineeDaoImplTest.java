package ua.hodik.gym.dao.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.hodik.gym.model.Trainee;
import ua.hodik.gym.tets.util.TestUtils;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TraineeDaoImplTest {
    public static final int ID = 1;
    private final TestUtils testUtils = new TestUtils();
    private final String traineePath = "src/test/resources/trainee.with.id.json";
    private final String expectedTraineePath = "src/test/resources/trainee.same.user.name.json";
    private final Trainee trainee = testUtils.getUser(traineePath, Trainee.class);
    private final Trainee expectedTrainee = testUtils.getUser(expectedTraineePath, Trainee.class);
    public final List<Trainee> expectedTraineeList = List.of(expectedTrainee);
    public static final String USER_NAME = "Sam.Jonson";
    public static final String WRONG_USER_NAME = "Vasya.Lis";
    @InjectMocks
    private TraineeDaoImpl traineeDao;
    @Mock
    private Map<Integer, Trainee> traineeDB;

    @Test
    void add() {
        //given
        when(traineeDB.put(anyInt(), any())).thenReturn(trainee);
        //when
        Trainee addedTrainee = traineeDao.add(trainee);
        //then
        verify(traineeDB).put(addedTrainee.getUserId(), trainee);
        assertEquals(trainee, addedTrainee);
    }

    @Test
    void update() {
        //given
        when(traineeDB.put(anyInt(), any())).thenReturn(expectedTrainee);
        //when
        Trainee updatedTrainee = traineeDao.update(expectedTrainee, expectedTrainee.getUserId());
        //then
        verify(traineeDB).put(updatedTrainee.getUserId(), expectedTrainee);
        assertEquals(expectedTrainee, updatedTrainee);
    }

    @Test
    void getById() {
        //given
        when(traineeDB.get(anyInt())).thenReturn(expectedTrainee);
        //when
        Trainee traineeById = traineeDao.getById(expectedTrainee.getUserId());
        //then
        verify(traineeDB).get(traineeById.getUserId());
        assertEquals(expectedTrainee, traineeById);
    }

    @Test
    void getAllTrainees() {
        //given
        when(traineeDB.values()).thenReturn(List.of(expectedTrainee));
        //when
        List<Trainee> allTrainees = traineeDao.getAllTrainees();
        //then
        verify(traineeDB).values();
        assertEquals(expectedTraineeList, allTrainees);
    }

    @Test
    void getAllTraineesByUserName() {
        //given
        when(traineeDB.values()).thenReturn(List.of(expectedTrainee));
        //when
        List<Trainee> allTrainees = traineeDao.getAllTraineesByUserName(USER_NAME);
        //then
        verify(traineeDB).values();
        assertEquals(expectedTraineeList, allTrainees);
    }

    @Test
    void getAllTraineesByUserNameShouldBeEmptyList() {
        //given
        when(traineeDB.values()).thenReturn(List.of());
        //when
        List<Trainee> allTrainees = traineeDao.getAllTraineesByUserName(WRONG_USER_NAME);
        //then
        verify(traineeDB).values();
        assertEquals(List.of(), allTrainees);
    }

    @Test
    void getMaxIdNotEmptyKeySet() {  //given
        when(traineeDB.keySet()).thenReturn(Set.of(1));
        //when
        int maxId = traineeDao.getMaxId();
        //then
        verify(traineeDB).keySet();
        assertEquals(1, maxId);

    }

    @Test
    void getMaxIdEmptyKeySet() {  //given
        when(traineeDB.keySet()).thenReturn(Set.of(0));
        //when
        int maxId = traineeDao.getMaxId();
        //then
        verify(traineeDB).keySet();
        assertEquals(0, maxId);

    }

    @Test
    void deleteSuccess() {
        //given
        when(traineeDB.remove(anyInt())).thenReturn(expectedTrainee);
        //when
        Boolean deleted = traineeDao.delete(ID);
        //then
        verify(traineeDB).remove(ID);
        assertEquals(true, deleted);
    }

    @Test
    void deleteUnSuccess() {
        //given
        when(traineeDB.remove(anyInt())).thenReturn(null);
        //when
        Boolean deleted = traineeDao.delete(ID);
        //then
        verify(traineeDB).remove(ID);
        assertEquals(false, deleted);
    }
}