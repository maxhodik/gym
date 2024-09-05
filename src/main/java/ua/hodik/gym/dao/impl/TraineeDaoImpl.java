package ua.hodik.gym.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ua.hodik.gym.dao.TraineeDao;
import ua.hodik.gym.model.Trainee;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class TraineeDaoImpl implements TraineeDao {
    @Autowired
    private Map<Integer, Trainee> traineeDB;


    @Override
    public Optional<Trainee> add(Trainee trainee) {
        return Optional.ofNullable(traineeDB.put(trainee.getUserId(), trainee));

    }

    @Override
    public Optional<Trainee> update(Trainee trainee, int id) {
        return Optional.ofNullable(traineeDB.put(id, trainee));
    }

    @Override
    public boolean delete(int id) {
        return traineeDB.remove(id) != null;
    }

    @Override
    public Optional<Trainee> getById(int id) {

        return Optional.of(traineeDB.get(id));
    }

    @Override
    public List<Trainee> getAllTrainees() {
        return new ArrayList<>(traineeDB.values());
    }

    @Override
    public List<Trainee> getAllTraineesByUserName(String userName) {

        return traineeDB.values().stream()
                .filter(t -> t.getUserName().equals(userName))
                .collect(Collectors.toList());
    }

    @Override
    public int getMaxId() {
        return traineeDB.keySet().size();
    }
}
