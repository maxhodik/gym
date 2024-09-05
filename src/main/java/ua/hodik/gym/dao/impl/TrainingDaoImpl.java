package ua.hodik.gym.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import ua.hodik.gym.dao.TrainingDao;
import ua.hodik.gym.model.Training;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class TrainingDaoImpl implements TrainingDao {
    @Autowired
    private Map<Integer, Training> trainingDB;
    @Override
    public Optional<Training> add(Training training) {
        training.setTrainingId(getMaxId()+1);
        return Optional.ofNullable(trainingDB.put(training.getTrainingId(), training));
    }

    @Override
    public Optional<Training> getById(int trainingId) {
        return Optional.ofNullable(trainingDB.get(trainingId));
    }

    @Override
    public List<Training> getTrainings() {
        return new ArrayList<>(trainingDB.values());
    }

    @Override
    public int getMaxId() {
     return    trainingDB.size();
    }
}
