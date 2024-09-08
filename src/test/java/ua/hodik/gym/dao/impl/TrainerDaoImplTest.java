package ua.hodik.gym.dao.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.hodik.gym.model.Trainer;
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
class TrainerDaoImplTest {
    public static final int ID = 1;
    private final String trainerPath = "src/test/resources/trainer.json";
    private final String expectedTrainerPath = "src/test/resources/trainer.same.user.name.json";
    private final Trainer trainer = TestUtils.readFromFile(trainerPath, Trainer.class);
    private final Trainer expectedTrainer = TestUtils.readFromFile(expectedTrainerPath, Trainer.class);
    public final List<Trainer> expectedTrainerList = List.of(expectedTrainer);
    public static final String USER_NAME = "Sam.Jonson";
    public static final String WRONG_USER_NAME = "Vasya.Lis";
    @Mock
    private Map<Integer, Trainer> trainerDB;
    @InjectMocks
    private TrainerDaoImpl trainerDao;

    @Test
    void add() {
        //given
        when(trainerDB.put(anyInt(), any())).thenReturn(trainer);
        //when
        Trainer addedTrainer = trainerDao.add(trainer);
        //then
        verify(trainerDB).put(addedTrainer.getUserId(), trainer);
        assertEquals(trainer, addedTrainer);
    }

    @Test
    void update() {
        //given
        when(trainerDB.put(anyInt(), any())).thenReturn(expectedTrainer);
        //when
        Trainer updatedTrainer = trainerDao.update(expectedTrainer, expectedTrainer.getUserId());
        //then
        verify(trainerDB).put(updatedTrainer.getUserId(), expectedTrainer);
        assertEquals(expectedTrainer, updatedTrainer);
    }

    @Test
    void deleteSuccess() {
        //given
        when(trainerDB.remove(anyInt())).thenReturn(expectedTrainer);
        //when
        Boolean deleted = trainerDao.delete(ID);
        //then
        verify(trainerDB).remove(ID);
        assertEquals(true, deleted);
    }

    @Test
    void deleteUnSuccess() {
        //given
        when(trainerDB.remove(anyInt())).thenReturn(null);
        //when
        Boolean deleted = trainerDao.delete(ID);
        //then
        verify(trainerDB).remove(ID);
        assertEquals(false, deleted);
    }

    @Test
    void getById() {
        //given
        when(trainerDB.get(anyInt())).thenReturn(expectedTrainer);
        //when
        Trainer trainerById = trainerDao.getById(expectedTrainer.getUserId());
        //then
        verify(trainerDB).get(trainerById.getUserId());
        assertEquals(expectedTrainer, trainerById);
    }

    @Test
    void getAllTrainers() {
        //given
        when(trainerDB.values()).thenReturn(List.of(expectedTrainer));
        //when
        List<Trainer> allTrainers = trainerDao.getAllTrainers();
        //then
        verify(trainerDB).values();
        assertEquals(expectedTrainerList, allTrainers);
    }

    @Test
    void getAllTrainersByUserName() {
        //given
        when(trainerDB.values()).thenReturn(List.of(expectedTrainer));
        //when
        List<Trainer> allTrainers = trainerDao.getAllTrainersByUserName(USER_NAME);
        //then
        verify(trainerDB).values();
        assertEquals(expectedTrainerList, allTrainers);
    }

    @Test
    void getAllTraineesByUserNameShouldBeEmptyList() {
        //given
        when(trainerDB.values()).thenReturn(List.of());
        //when
        List<Trainer> allTrainers = trainerDao.getAllTrainersByUserName(WRONG_USER_NAME);
        //then
        verify(trainerDB).values();
        assertEquals(List.of(), allTrainers);
    }

    @Test
    void getMaxIdNotEmptyKeySet() {
        //given
        when(trainerDB.keySet()).thenReturn(Set.of(1));
        //when
        int maxId = trainerDao.getMaxId();
        //then
        verify(trainerDB).keySet();
        assertEquals(1, maxId);
    }

    @Test
    void getMaxIdEmptyKeySet() {  //given
        when(trainerDB.keySet()).thenReturn(Set.of(0));
        //when
        int maxId = trainerDao.getMaxId();
        //then
        verify(trainerDB).keySet();
        assertEquals(0, maxId);
    }
}