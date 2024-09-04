package ua.hodik.gym.service;

import ua.hodik.gym.model.Trainer;

import java.util.List;
import java.util.Optional;

public interface TrainerService {
    Optional<Trainer> create(Trainer trainer);

    Optional<Trainer> update(Trainer trainer, int id);

    boolean delete(int id);

    Optional<Trainer> findById(int id);

    List<Trainer> getAllTrainers();
}
