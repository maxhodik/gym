package ua.hodik.gym.dao;

import ua.hodik.gym.model.Training;

import java.util.List;
import java.util.Optional;

public interface TrainingDao {
    Optional<Training> add(Training training);

    Optional<Training> getById(int trainingId);

    List<Training> getTrainings();

    int getMaxId();
}
