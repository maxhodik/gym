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
    private final TestUtils testUtils = new TestUtils();
    private final String TRAINER_PATH = "src/test/resources/trainer.without.user.name.json";
    private final String EXPECTED_TRAINER_PATH = "src/test/resources/trainer.same.user.name.json";
    private final Trainer TRAINER = testUtils.getUser(TRAINER_PATH, Trainer.class);
    private final Trainer EXPECTED_TRAINER = testUtils.getUser(EXPECTED_TRAINER_PATH, Trainer.class);
    public final List<Trainer> EXPECTED_TRAINER_LIST = List.of(EXPECTED_TRAINER);
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
        trainerService.create(TRAINER);
        //then
        verify(userNameGenerator).generateUserName(FIRST_NAME, LAST_NAME);
        verify(passwordGenerator).generatePassword();
        verify(trainerDao).add(EXPECTED_TRAINER);

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
        trainerService.update(EXPECTED_TRAINER, EXPECTED_TRAINER.getUserId());
        verify(trainerDao).update(EXPECTED_TRAINER, EXPECTED_TRAINER.getUserId());
    }

    @Test
    void delete() {
        trainerService.delete(EXPECTED_TRAINER.getUserId());
        verify(trainerDao).delete(EXPECTED_TRAINER.getUserId());
    }

    @Test
    void findById() {
        //give
        when(trainerDao.getById(ID)).thenReturn(EXPECTED_TRAINER);
        //when
        Trainer trainerById = trainerService.findById(ID);
        //then
        verify(trainerDao).getById(ID);
        assertEquals(EXPECTED_TRAINER, trainerById);
    }

    @Test
    void getAllTrainers() {
        //given
        when(trainerDao.getAllTrainers()).thenReturn(List.of(EXPECTED_TRAINER));
        //when
        List<Trainer> trainerList = trainerService.getAllTrainers();
        //then
        verify(trainerDao).getAllTrainers();
        assertEquals(EXPECTED_TRAINER_LIST, trainerList);
    }
}