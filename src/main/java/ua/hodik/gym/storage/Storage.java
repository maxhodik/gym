package ua.hodik.gym.storage;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ua.hodik.gym.model.Trainee;
import ua.hodik.gym.model.Trainer;
import ua.hodik.gym.model.Training;

import java.util.Map;

@Component
public class Storage {
    @Value("file.traineeData")
    private String fileTraineeDataPath;
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
    }

}
