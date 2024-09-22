package ua.hodik.gym.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.hodik.gym.model.Trainer;
import ua.hodik.gym.repository.TrainerRepository;
import ua.hodik.gym.tets.util.TestUtils;
import ua.hodik.gym.util.PasswordGenerator;
import ua.hodik.gym.util.UserNameGenerator;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrainerServiceImplTest {
    private static final int ID = 1;
    public static final String PASSWORD = "ABCDEFJxyz";
    private static final String FIRST_NAME = "Sam";
    private static final String LAST_NAME = "Jonson";
    private final String trainerPath = "trainer.without.user.name.json";
    private final String expectedTrainerPath = "trainer.same.user.name.json";
    private final Trainer trainer = TestUtils.readFromFile(trainerPath, Trainer.class);
    private final Trainer expectedTrainer = TestUtils.readFromFile(expectedTrainerPath, Trainer.class);
    public final List<Trainer> expectedTrainerList = List.of(expectedTrainer);
    @Mock
    private PasswordGenerator passwordGenerator;
    @Mock
    private UserNameGenerator userNameGenerator;
    @Mock
    private TrainerRepository trainerRepository;
    @InjectMocks
    private TrainerServiceImpl trainerService;

    @Test
    void createShouldThrowException() {
//        //when
//        NullPointerException exception = assertThrows(NullPointerException.class,
//                () -> trainerService.create(null));
//        //then
//        assertEquals("Trainer can't be null", exception.getMessage());
    }

    @Test
    void create() {
//        //given
//        when(trainerRepository.getMaxId()).thenReturn(0);
//        when(userNameGenerator.generateUserName(FIRST_NAME, LAST_NAME)).thenReturn(FIRST_NAME + "." + LAST_NAME);
//        when(passwordGenerator.generatePassword()).thenReturn(PASSWORD);
//        //when
//        trainerService.create(trainer);
//        //then
//        verify(userNameGenerator).generateUserName(FIRST_NAME, LAST_NAME);
//        verify(passwordGenerator).generatePassword();
//        verify(trainerRepository).add(expectedTrainer);

    }

//    @Test
//    void updateShouldThrowException() {
//        //when
//        NullPointerException exception = assertThrows(NullPointerException.class,
//                () -> trainerService.update(null, 1));
//        //then
//        assertEquals("Trainer can't be null", exception.getMessage());
//    }

    @Test
    void update() {
//        //when
//        trainerService.update(expectedTrainer, expectedTrainer.getId());
//        //then
//        verify(trainerRepository).update(expectedTrainer, expectedTrainer.getId());
    }

    @Test
    void delete() {
//        //when
//        trainerService.delete(expectedTrainer.getId());
//        //then
//        verify(trainerRepository).delete(expectedTrainer.getId());
    }

    @Test
    void findById() {
        //give
        when(trainerRepository.getById(ID)).thenReturn(expectedTrainer);
        //when
        Trainer trainerById = trainerService.findById(ID);
        //then
        verify(trainerRepository).getById(ID);
        assertEquals(expectedTrainer, trainerById);
    }

    @Test
    void getAllTrainers() {
//        //given
//        when(trainerRepository.getAllTrainers()).thenReturn(List.of(expectedTrainer));
//        //when
//        List<Trainer> trainerList = trainerService.getAllTrainers();
//        //then
//        verify(trainerRepository).getAllTrainers();
//        assertEquals(expectedTrainerList, trainerList);
    }
}