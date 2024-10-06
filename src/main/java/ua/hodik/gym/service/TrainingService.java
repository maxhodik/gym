package ua.hodik.gym.service;

import ua.hodik.gym.dto.FilterFormDto;
import ua.hodik.gym.dto.TrainingDto;
import ua.hodik.gym.dto.TrainingTypeDto;
import ua.hodik.gym.model.Training;

import java.util.List;

public interface TrainingService {


  Training findById(int id);

  Training createTraining(TrainingDto trainingDto);

  List<TrainingDto> findAllWithFilters(FilterFormDto filterFormDto);

  List<TrainingTypeDto> getTrainingType();
}
