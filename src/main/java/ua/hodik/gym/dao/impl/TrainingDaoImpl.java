package ua.hodik.gym.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ua.hodik.gym.dao.TrainingDao;
import ua.hodik.gym.model.Training;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class TrainingDaoImpl implements TrainingDao {
    @Autowired
    private Map<Integer, Training> trainingDB;

    @Override
    public Training add(Training training) {
        training.setTrainingId(getMaxId() + 1);
        return trainingDB.put(training.getTrainingId(), training);
    }

    @Override
    public Training getById(int trainingId) {
        return trainingDB.get(trainingId);
    }

    @Override
    public List<Training> getTrainings() {
        return new ArrayList<>(trainingDB.values());
    }

    @Override
    public int getMaxId() {
        return trainingDB.keySet().stream()
                .mapToInt(Integer::intValue)
                .max()
                .orElse(0);
    }
}
