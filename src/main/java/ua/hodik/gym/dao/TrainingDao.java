package ua.hodik.gym.dao;

import ua.hodik.gym.model.Training;

import java.util.List;

public interface TrainingDao {
    Training add(Training training);

    Training getById(int trainingId);

    List<Training> getTrainings();

    int getMaxId();
}
