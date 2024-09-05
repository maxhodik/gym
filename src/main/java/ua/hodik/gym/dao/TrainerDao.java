package ua.hodik.gym.dao;

import ua.hodik.gym.model.Trainer;

import java.util.List;

public interface TrainerDao {
    Trainer add(Trainer trainer);

    Trainer update(Trainer trainer, int id);

    boolean delete(int id);

    Trainer getById(int id);

    List<Trainer> getAllTrainers();

    List<Trainer> getAllTrainersByUserName(String userName);

    int getMaxId();
}
