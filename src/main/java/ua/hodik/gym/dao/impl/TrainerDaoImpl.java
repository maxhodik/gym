package ua.hodik.gym.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ua.hodik.gym.dao.TrainerDao;
import ua.hodik.gym.model.Trainer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class TrainerDaoImpl implements TrainerDao {
    @Autowired
    private Map<Integer, Trainer> trainerDB;

    @Override
    public Trainer add(Trainer trainer) {
        return trainerDB.put(trainer.getTrainerId(), trainer);
    }

    @Override
    public Trainer update(Trainer trainer, int id) {
        return trainerDB.put(id, trainer);
    }

    @Override
    public boolean delete(int id) {
        return trainerDB.remove(id) != null;
    }

    @Override
    public Trainer getById(int id) {
        return trainerDB.get(id);
    }

    @Override
    public List<Trainer> getAllTrainers() {
        return new ArrayList<>(trainerDB.values());
    }

    @Override
    public List<Trainer> getAllTrainersByUserName(String userName) {
        return trainerDB.values().stream()
                .filter(t -> t.getUser().getUserName().equals(userName))
                .collect(Collectors.toList());
    }

    @Override
    public int getMaxId() {
        return trainerDB.keySet().stream()
                .mapToInt(Integer::intValue)
                .max()
                .orElse(0);
    }
}
