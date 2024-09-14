package ua.hodik.gym.storage;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.util.ResourceUtils;
import ua.hodik.gym.config.StorageConfig;
import ua.hodik.gym.dto.StorageData;
import ua.hodik.gym.exception.StorageInitializeException;
import ua.hodik.gym.model.Trainee;
import ua.hodik.gym.model.Trainer;
import ua.hodik.gym.model.Training;
import ua.hodik.gym.tets.util.TestUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StorageTest {
    public static final String TRAINING_NAME = "boxing1";
    public static final String FIRST_TRAINEE_USER_NAME = "Vasya.Lis";
    public static final String SECOND_TRAINER_USER_NAME = "Masha.Semenova";
    public static final String INITIAL_DATA_JSON = "initialData.json";
    public static final String WRONG_FILE_JSON = "wrong.file.json";

    private final String storagePath = "initialData.json";
    private final StorageData storageData = TestUtils.readFromFile(storagePath, StorageData.class);
    @Mock
    private StorageConfig storageConfig;
    private Map<Integer, Trainee> traineeDB;
    private Map<Integer, Trainer> trainerDB;
    private Map<Integer, Training> trainingDB;
    @Mock
    private ObjectMapper objectMapper;
    @InjectMocks
    private Storage storage;

    @BeforeEach
    public void setUp() throws Exception {

        trainingDB = new HashMap<>();
        traineeDB = new HashMap<>();
        trainerDB = new HashMap<>();

        storage.setTrainingDB(trainingDB);
        storage.setTraineeDB(traineeDB);
        storage.setTrainerDB(trainerDB);
    }

    @Test
    void initialize() throws IOException {
        //given
        ResourceUtils.getFile("classpath:" + INITIAL_DATA_JSON);
        when(storageConfig.getFilePath()).thenReturn(INITIAL_DATA_JSON);
        when(objectMapper.readValue(any(String.class), eq(StorageData.class))).thenReturn(storageData);
        //when
        storage.initialize();
        //then
        assertEquals(1, trainingDB.size());
        assertEquals(2, traineeDB.size());
        assertEquals(2, trainerDB.size());
        assertEquals(TRAINING_NAME, trainingDB.get(1).getName());
        assertEquals(FIRST_TRAINEE_USER_NAME, traineeDB.get(1).getUserName());
        assertEquals(SECOND_TRAINER_USER_NAME, trainerDB.get(2).getUserName());
    }

    @Test
    public void initialize_File_Not_found() {
        //given
        when(storageConfig.getFilePath()).thenReturn(WRONG_FILE_JSON);
        //when
        Exception exception = assertThrows(StorageInitializeException.class, () -> storage.initialize());
        //then
        assertEquals("Can't read the file wrong.file.json", exception.getMessage());
    }
}