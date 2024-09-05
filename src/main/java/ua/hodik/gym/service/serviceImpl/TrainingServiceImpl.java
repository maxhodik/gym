package ua.hodik.gym.service.serviceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.hodik.gym.dao.TrainingDao;
import ua.hodik.gym.model.Training;
import ua.hodik.gym.service.TrainingService;

import java.util.Objects;

@Service
public class TrainingServiceImpl implements TrainingService {
    @Autowired
    private TrainingDao trainingDao;

    @Override
    public Training create(Training training) {
        Objects.requireNonNull(training);

        return trainingDao.add(training);
    }

    @Override
    public Training findById(int id) {
        return trainingDao.getById(id);
    }
}