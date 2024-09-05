package ua.hodik.gym.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ua.hodik.gym.dao.TrainerDao;
import ua.hodik.gym.model.Trainer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class TrainerDaoImpl implements TrainerDao {
    @Autowired
    private Map<Integer, Trainer> trainerDB;

    @Override
    public Optional<Trainer> add(Trainer trainer) {
        return Optional.ofNullable(trainerDB.put(trainer.getUserId(), trainer));
    }

    @Override
    public Optional<Trainer> update(Trainer trainer, int id) {
        return Optional.ofNullable(trainerDB.put(id, trainer));
    }

    @Override
    public boolean delete(int id) {
        return trainerDB.remove(id) != null;
    }

    @Override
    public Optional<Trainer> getById(int id) {
        return Optional.of(trainerDB.get(id));
    }

    @Override
    public List<Trainer> getAllTrainers() {
        return new ArrayList<>(trainerDB.values());
    }

    @Override
    public List<Trainer> getAllTrainersByUserName(String userName) {
        return trainerDB.values().stream()
                .filter(t -> t.getUserName().equals(userName))
                .collect(Collectors.toList());
    }

    @Override
    public int getMaxId() {
        return trainerDB.size() - 1;
    }
}
