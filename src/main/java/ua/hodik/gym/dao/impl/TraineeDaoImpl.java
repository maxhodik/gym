package ua.hodik.gym.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ua.hodik.gym.dao.TraineeDao;
import ua.hodik.gym.model.Trainee;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class TraineeDaoImpl implements TraineeDao {
    @Autowired
    private Map<Integer, Trainee> traineeDb;


    @Override
    public Optional<Trainee> add(Trainee trainee) {
        return Optional.ofNullable(traineeDb.put(trainee.getUserId(), trainee));

    }

    @Override
    public Optional<Trainee> update(Trainee trainee, int id) {
        return Optional.ofNullable(traineeDb.put(id, trainee));
    }

    @Override
    public boolean delete(int id) {
        return traineeDb.remove(id) != null;
    }

    @Override
    public Optional<Trainee> getById(int id) {

        return Optional.of(traineeDb.get(id));
    }

    @Override
    public List<Trainee> getAllTrainees() {
        return traineeDb.values().stream().toList();
    }

    @Override
    public List<Trainee> getAllTraineesByUserName(String userName) {

        return traineeDb.values().stream()
                .filter(t -> t.getUserName().equals(userName))
                .collect(Collectors.toList());
    }

    @Override
    public int getMaxId() {
        return traineeDb.size() - 1;
    }
}
