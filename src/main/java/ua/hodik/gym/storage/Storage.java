package ua.hodik.gym.storage;


import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;
import ua.hodik.gym.dto.StorageData;
import ua.hodik.gym.model.Trainee;
import ua.hodik.gym.model.Trainer;
import ua.hodik.gym.model.Training;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;

@Component
public class Storage {
    @Value("${file.path.initialData}")
    private String filePath;

    @Autowired
    private Map<Integer, Trainee> traineeDB;
    @Autowired
    private Map<Integer, Trainer> trainerDB;
    @Autowired
    private Map<Integer, Training> trainingDB;
    @Autowired
    ObjectMapper objectMapper;

    @PostConstruct
    public void initialize() {
        try {
            File file = ResourceUtils.getFile("classpath:" + filePath);
            String data = new String(Files.readAllBytes(file.toPath()));
            StorageData storageData = objectMapper.readValue(data, StorageData.class);
            List<Trainee> traineeList = storageData.getTraineeList();
            List<Trainer> trainerList = storageData.getTrainerList();
            List<Training> trainingList = storageData.getTrainingList();
            trainingList.forEach(t -> trainingDB.put(t.getTrainingId(), t));
            traineeList.forEach(t -> traineeDB.put(t.getUserId(), t));
            trainerList.forEach(t -> trainerDB.put(t.getUserId(), t));

        } catch (IOException e) {
            e.printStackTrace(); // Consider proper logging here
        }
    }
}