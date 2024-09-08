package ua.hodik.gym.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.hodik.gym.dao.TrainerDao;
import ua.hodik.gym.model.Trainer;
import ua.hodik.gym.tets.util.TestUtils;
import ua.hodik.gym.util.PasswordGenerator;
import ua.hodik.gym.util.UserNameGenerator;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrainerServiceImplTest {
    private static final int ID = 1;
    public static final String PASSWORD = "ABCDEFJxyz";
    private static final String FIRST_NAME = "Sam";
    private static final String LAST_NAME = "Jonson";
    private final String trainerPath = "src/test/resources/trainer.without.user.name.json";
    private final String expectedTrainerPath = "src/test/resources/trainer.same.user.name.json";
    private final Trainer trainer = TestUtils.readFromFile(trainerPath, Trainer.class);
    private final Trainer expectedTrainer = TestUtils.readFromFile(expectedTrainerPath, Trainer.class);
    public final List<Trainer> expectedTrainerList = List.of(expectedTrainer);
    @Mock
    private PasswordGenerator passwordGenerator;
    @Mock
    private UserNameGenerator userNameGenerator;
    @Mock
    private TrainerDao trainerDao;
    @InjectMocks
    private TrainerServiceImpl trainerService;

    @Test
    void createShouldThrowException() {
        //when
        NullPointerException exception = assertThrows(NullPointerException.class,
                () -> trainerService.create(null));
        //then
        assertEquals("Trainer can't be null", exception.getMessage());
    }

    @Test
    void create() {
        //given
        when(trainerDao.getMaxId()).thenReturn(0);
        when(userNameGenerator.generateUserName(FIRST_NAME, LAST_NAME)).thenReturn(FIRST_NAME + "." + LAST_NAME);
        when(passwordGenerator.generatePassword()).thenReturn(PASSWORD);
        //when
        trainerService.create(trainer);
        //then
        verify(userNameGenerator).generateUserName(FIRST_NAME, LAST_NAME);
        verify(passwordGenerator).generatePassword();
        verify(trainerDao).add(expectedTrainer);

    }

    @Test
    void updateShouldThrowException() {
        //when
        NullPointerException exception = assertThrows(NullPointerException.class,
                () -> trainerService.update(null, 1));
        //then
        assertEquals("Trainer can't be null", exception.getMessage());
    }

    @Test
    void update() {
        trainerService.update(expectedTrainer, expectedTrainer.getUserId());
        verify(trainerDao).update(expectedTrainer, expectedTrainer.getUserId());
    }

    @Test
    void delete() {
        trainerService.delete(expectedTrainer.getUserId());
        verify(trainerDao).delete(expectedTrainer.getUserId());
    }

    @Test
    void findById() {
        //give
        when(trainerDao.getById(ID)).thenReturn(expectedTrainer);
        //when
        Trainer trainerById = trainerService.findById(ID);
        //then
        verify(trainerDao).getById(ID);
        assertEquals(expectedTrainer, trainerById);
    }

    @Test
    void getAllTrainers() {
        //given
        when(trainerDao.getAllTrainers()).thenReturn(List.of(expectedTrainer));
        //when
        List<Trainer> trainerList = trainerService.getAllTrainers();
        //then
        verify(trainerDao).getAllTrainers();
        assertEquals(expectedTrainerList, trainerList);
    }
}