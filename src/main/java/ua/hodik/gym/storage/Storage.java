package ua.hodik.gym.storage;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ua.hodik.gym.model.Trainee;
import ua.hodik.gym.model.Trainer;
import ua.hodik.gym.model.Training;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

@Component
public class Storage {
    @Value("file.traineeData")
    private String filePath;
    @Value("file.trainerData")
    private String fileTrainerDataPat;
    @Value("file.trainingData")
    private String fileTrainingDataPath;
    @Autowired
    private Map<Integer, Trainee> traineeDB;
    @Autowired
    private Map<Integer, Trainer> trainerDB;
    @Autowired
    private Map<Integer, Training> trainingDB;


    @PostConstruct
    public void initialize() {
        try {
            // Example of loading JSON data from a file
            String data = new String(Files.readAllBytes(Paths.get(filePath)));
            // Parse data and populate the inMemoryStorage
            // Example assumes JSON with simple structure. Adjust parsing accordingly.
            // Here, you'll parse the JSON and populate the traineeStorage, trainerStorage, etc.
            Map<Integer, Object> initialData = parseJson(data);
            inMemoryStorage.getTraineeStorage().putAll(initialData);
        } catch (IOException e) {
            e.printStackTrace(); // Consider proper logging here
        }
    }

    private Map<Integer, Object> parseJson(String data) {
        // Implement JSON parsing logic here
        // This is just a placeholder method
        return Map.of(); // Replace with actual implementation
    }
}
