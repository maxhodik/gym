package ua.hodik.gym.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.hodik.gym.dao.TrainingDao;
import ua.hodik.gym.model.Training;
import ua.hodik.gym.tets.util.TestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrainingServiceImplTest {


    public static final int ID = 1;
    private final String trainingPath = "src/test/resources/training.without.id.json";
    private final String expectedTrainingPath = "src/test/resources/expected.training.json";
    private final Training training = TestUtils.readFromFile(trainingPath, Training.class);
    private final Training expectedTraining = TestUtils.readFromFile(expectedTrainingPath, Training.class);
    @Mock
    private TrainingDao trainingDao;
    @InjectMocks
    private TrainingServiceImpl trainingService;

    @Test
    void createShouldThrowException() {
        //when
        NullPointerException exception = assertThrows(NullPointerException.class,
                () -> trainingService.create(null));
        //then
        assertEquals("Training can't be null", exception.getMessage());
    }

    @Test
    void create() {
        //when
        trainingService.create(training);
        //then
        verify(trainingDao).add(training);
    }

    @Test
    void findById() {
        //given
        when(trainingDao.getById(ID)).thenReturn(expectedTraining);
        //when
        Training training = trainingService.findById(ID);
        //then
        verify(trainingDao).getById(ID);
        assertEquals(expectedTraining, training);
    }
}