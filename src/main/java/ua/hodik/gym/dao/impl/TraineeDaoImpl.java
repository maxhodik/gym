package ua.hodik.gym.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ua.hodik.gym.dao.TraineeDao;
import ua.hodik.gym.model.Trainee;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class TraineeDaoImpl implements TraineeDao {
    @Autowired
    private Map<Integer, Trainee> traineeDB;


    @Override
    public Trainee add(Trainee trainee) {
        return traineeDB.put(trainee.getTraineeId(), trainee);

    }

    @Override
    public Trainee update(Trainee trainee, int id) {
        return traineeDB.put(id, trainee);
    }

    @Override
    public boolean delete(int id) {
        return traineeDB.remove(id) != null;
    }

    @Override
    public Trainee getById(int id) {
        return traineeDB.get(id);
    }

    @Override
    public List<Trainee> getAllTrainees() {
        return new ArrayList<>(traineeDB.values());
    }

    @Override
    public List<Trainee> getAllTraineesByUserName(String userName) {
        return traineeDB.values().stream()
                .filter(t -> t.getUser().getUserName().equals(userName))
                .collect(Collectors.toList());
    }

    @Override
    public int getMaxId() {
        return traineeDB.keySet().stream()
                .mapToInt(Integer::intValue)
                .max()
                .orElse(0);
    }
}
