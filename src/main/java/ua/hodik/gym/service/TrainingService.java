package ua.hodik.gym.service;

import ua.hodik.gym.dto.FilterFormDto;
import ua.hodik.gym.dto.TrainingDto;
import ua.hodik.gym.model.Training;

import java.util.List;

public interface TrainingService {
  Training create(Training training);

  Training findById(int id);

  Training createTraining(TrainingDto trainingDto);

  List<Training> findAllWithFilters(FilterFormDto filterFormDto);
}
