package ua.hodik.gym.storage;

import org.springframework.beans.factory.annotation.Autowired;
import ua.hodik.gym.model.Trainee;
import ua.hodik.gym.model.Trainer;
import ua.hodik.gym.model.Training;

import java.util.Map;

//@Component
public class StorageImpl {
    // todo create interface
    private Map<Integer, Trainer> trainerDB;
    private Map<Integer, Trainee> traineeDB;
    private Map<Integer, Training> trainingDB;

    @Autowired
    public void setTraineeDB(Map<Integer, Trainee> traineeDB) {
        this.traineeDB = traineeDB;
    }

    @Autowired
    public void setTrainingDB(Map<Integer, Training> trainingDB) {
        this.trainingDB = trainingDB;
    }

    @Autowired
    public void setTrainerDB(Map<Integer, Trainer> trainerDB) {
        this.trainerDB = trainerDB;
    }


}
