package ua.hodik.gym.dao.impl;

import org.springframework.stereotype.Component;
import ua.hodik.gym.dao.TrainerDao;
import ua.hodik.gym.model.Trainer;

import java.util.List;
import java.util.Optional;

@Component
public class TrainerDaoImpl implements TrainerDao {
    @Override
    public Optional<Trainer> add(Trainer trainer) {
        return Optional.empty();
    }

    @Override
    public Optional<Trainer> update(Trainer trainer, int id) {
        return Optional.empty();
    }

    @Override
    public boolean delete(int id) {
        return false;
    }

    @Override
    public Optional<Trainer> getById(int id) {
        return Optional.empty();
    }

    @Override
    public List<Trainer> getAllTrainers() {
        return null;
    }

    @Override
    public List<Trainer> getAllTrainersByUserName() {
        return null;
    }

    @Override
    public int getMaxId() {
        return 0;
    }
}
