package ua.hodik.gym.service.serviceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import ua.hodik.gym.dao.TrainingDao;
import ua.hodik.gym.model.Trainee;
import ua.hodik.gym.model.Training;
import ua.hodik.gym.service.TrainingService;

import java.util.Objects;
import java.util.Optional;

public class TrainingServiceImpl implements TrainingService {
    @Autowired
    private TrainingDao trainingDao;

    @Override
    public Optional<Training> create(Training training) {
        Objects.requireNonNull(training);

        return trainingDao.add(training);
    }

    @Override
    public Optional<Training> findById(int id) {
        return trainingDao.getById(id);
    }
}