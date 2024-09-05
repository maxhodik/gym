package ua.hodik.gym.service;

import ua.hodik.gym.model.Trainee;
import ua.hodik.gym.model.Training;

import java.util.Optional;

public interface TrainingService {
    Optional<Training> create(Training training);

    Optional<Training> findById(int id);
}
