package ua.hodik.gym.service;

import ua.hodik.gym.model.Trainee;

import java.util.Optional;

public interface TrainingService {
    Optional<Trainee> create(Trainee trainee);

    Optional<Trainee> findById(int id);
}
