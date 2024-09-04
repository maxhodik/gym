package ua.hodik.gym.dao;

import ua.hodik.gym.model.Trainer;

import java.util.List;
import java.util.Optional;

public interface TrainerDao {
    Optional<Trainer> add(Trainer trainer);

    Optional<Trainer> update(Trainer trainer, int id);

    boolean delete(int id);

    Optional<Trainer> getById(int id);

    List<Trainer> getAllTrainers();

    List<Trainer> getAllTrainersByUserName(String userName);

    int getMaxId();
}
