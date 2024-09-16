package ua.hodik.gym.storage;


import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ResourceUtils;
import ua.hodik.gym.config.StorageConfig;
import ua.hodik.gym.dto.StorageData;
import ua.hodik.gym.exception.StorageInitializeException;
import ua.hodik.gym.model.Trainee;
import ua.hodik.gym.model.Trainer;
import ua.hodik.gym.model.Training;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;

//@Component
@Log4j2
public class Storage {

    private StorageConfig storageConfig;


    private Map<Integer, Trainee> traineeDB;

    private Map<Integer, Trainer> trainerDB;
    private Map<Integer, Training> trainingDB;

    @Autowired
    public void setStorageConfig(StorageConfig storageConfig) {
        this.storageConfig = storageConfig;
    }

    @Autowired
    public void setTraineeDB(Map<Integer, Trainee> traineeDB) {
        this.traineeDB = traineeDB;
    }

    @Autowired
    public void setTrainerDB(Map<Integer, Trainer> trainerDB) {
        this.trainerDB = trainerDB;
    }

    @Autowired
    public void setTrainingDB(Map<Integer, Training> trainingDB) {
        this.trainingDB = trainingDB;
    }

    @Autowired
    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Autowired
    private ObjectMapper objectMapper;

    @PostConstruct
    public void initialize() {
        String filePath = storageConfig.getFilePath();
        try {
            File file = ResourceUtils.getFile("classpath:" + filePath);
            String data = new String(Files.readAllBytes(file.toPath()));
            StorageData storageData = objectMapper.readValue(data, StorageData.class);
            List<Trainee> traineeList = storageData.getTraineeList();
            List<Trainer> trainerList = storageData.getTrainerList();
            List<Training> trainingList = storageData.getTrainingList();
            trainingList.forEach(t -> trainingDB.put(t.getTrainingId(), t));
            traineeList.forEach(t -> traineeDB.put(t.getTraineeId(), t));
            trainerList.forEach(t -> trainerDB.put(t.getId(), t));
            log.info("Post construct storage initialization completed");
        } catch (IOException e) {
            log.error("Post construct storage initialization failed. Can't read the file {}", filePath);
            throw new StorageInitializeException(String.format("Can't read the file %s", filePath), e);
        }
    }
}
