package ua.hodik.gym.dao.impl;

import org.springframework.stereotype.Component;
import ua.hodik.gym.dao.TraineeDao;
import ua.hodik.gym.model.Trainee;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component

public class TraineeDaoImpl implements TraineeDao {
    private Map<Integer, Trainee> traineeMap;


    @Override
    public Optional<Trainee> add(Trainee trainee) {
        return Optional.empty();
    }

    @Override
    public Optional<Trainee> update(Trainee trainee, int id) {
        return Optional.empty();
    }

    @Override
    public boolean delete(int id) {
        return false;
    }

    @Override
    public Optional<Trainee> getById(int id) {
        return Optional.empty();
    }

    @Override
    public List<Trainee> getAllTrainees() {
        return null;
    }

    @Override
    public List<Trainee> getAllTraineesByUserName() {
        return null;
    }

    @Override
    public int getMaxId() {
        return 0;
    }
}
