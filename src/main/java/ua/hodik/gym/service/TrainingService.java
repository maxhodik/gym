package ua.hodik.gym.service;

import ua.hodik.gym.model.Training;

public interface TrainingService {
  Training create(Training training);

  Training findById(int id);
}
