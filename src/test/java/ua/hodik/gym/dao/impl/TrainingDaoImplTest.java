package ua.hodik.gym.dao.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.hodik.gym.model.Training;
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
class TrainingDaoImplTest {
    public static final int ID = 1;
    private final String trainingPath = "src/test/resources/expected.training.json";
    private final Training training = TestUtils.readFromFile(trainingPath, Training.class);

    @Mock
    private Map<Integer, Training> trainingDB;
    @InjectMocks
    private TrainingDaoImpl trainingDao;

    @Test
    void add() {
        //given
        when(trainingDB.put(anyInt(), any())).thenReturn(training);
        //when
        Training addedTraining = trainingDao.add(training);
        //then
        verify(trainingDB).put(addedTraining.getTrainingId(), training);
        assertEquals(training, addedTraining);
    }

    @Test
    void getById() {
        //given
        when(trainingDB.get(anyInt())).thenReturn(training);
        //when
        Training trainingById = trainingDao.getById(training.getTrainingId());
        //then
        verify(trainingDB).get(trainingById.getTrainingId());
        assertEquals(training, trainingById);
    }

    @Test
    void getTrainings() {
        //given
        when(trainingDB.values()).thenReturn(List.of(training));
        //when
        List<Training> trainings = trainingDao.getTrainings();
        //then
        verify(trainingDB).values();
        assertEquals(List.of(training), trainings);
    }

    @Test
    void getMaxIdEmptyKeySet() {
        //given
        when(trainingDB.keySet()).thenReturn(Set.of(0));
        //when
        int maxId = trainingDao.getMaxId();
        //then
        verify(trainingDB).keySet();
        assertEquals(0, maxId);
    }

    @Test
    void getMaxIdNotEmptyKeySet() {
        //given
        when(trainingDB.keySet()).thenReturn(Set.of(1));
        //when
        int maxId = trainingDao.getMaxId();
        //then
        verify(trainingDB).keySet();
        assertEquals(1, maxId);
    }
}