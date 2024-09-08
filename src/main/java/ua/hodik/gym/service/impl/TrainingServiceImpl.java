package ua.hodik.gym.service.impl;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.hodik.gym.dao.TrainingDao;
import ua.hodik.gym.model.Training;
import ua.hodik.gym.service.TrainingService;

import java.util.Objects;

@Service
@Log4j2
public class TrainingServiceImpl implements TrainingService {
    @Autowired
    private TrainingDao trainingDao;

    @Override
    public Training create(Training training) {
        Objects.requireNonNull(training, "Training can't be null");
        Training addTraining = trainingDao.add(training);
        log.info("Training {} added successfully", training.getName());
        return addTraining;
    }

    @Override
    public Training findById(int id) {
        Training trainingById = trainingDao.getById(id);
        log.info("Training with id ={} updated", id);
        return trainingById;
    }
}
