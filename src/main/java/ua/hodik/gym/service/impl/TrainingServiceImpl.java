package ua.hodik.gym.service.impl;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.hodik.gym.dao.TrainingSpecification;
import ua.hodik.gym.dto.FilterDto;
import ua.hodik.gym.dto.FilterFormDto;
import ua.hodik.gym.dto.TrainingDto;
import ua.hodik.gym.exception.EntityNotFoundException;
import ua.hodik.gym.model.Trainee;
import ua.hodik.gym.model.Trainer;
import ua.hodik.gym.model.Training;
import ua.hodik.gym.repository.TrainingRepository;
import ua.hodik.gym.service.TraineeService;
import ua.hodik.gym.service.TrainerService;
import ua.hodik.gym.service.TrainingService;
import ua.hodik.gym.service.mapper.FilterDtoConverter;
import ua.hodik.gym.service.mapper.TrainingMapper;
import ua.hodik.gym.util.impl.validation.MyValidator;

import java.util.List;
import java.util.Map;

@Service
@Log4j2
public class TrainingServiceImpl implements TrainingService {

    private final TrainingRepository trainingRepository;
    private final TrainingMapper trainingMapper;
    private final TraineeService traineeService;
    private final TrainerService trainerService;
    private final FilterDtoConverter filterDtoConverter;
    private final TrainingSpecification trainingSpecification;
    private final MyValidator validator;

    @Autowired
    public TrainingServiceImpl(TrainingRepository trainingRepository, TrainingMapper trainingMapper, TraineeService traineeService, TrainerService trainerService, FilterDtoConverter filterDtoConverter, TrainingSpecification trainingSpecification, MyValidator validator) {
        this.trainingRepository = trainingRepository;
        this.trainingMapper = trainingMapper;
        this.traineeService = traineeService;
        this.trainerService = trainerService;
        this.filterDtoConverter = filterDtoConverter;
        this.trainingSpecification = trainingSpecification;
        this.validator = validator;
    }


    @Override
    public Training findById(int id) {
        return trainingRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Training with id= %s not found", id)));
    }

    @Transactional
    @Override
    public Training createTraining(TrainingDto trainingDto) {
        validator.validate(trainingDto);
        Training training = trainingMapper.convertToTraining(trainingDto);
        Trainee trainee = traineeService.findByUserName(trainingDto.getTraineeName());
        Trainer trainer = trainerService.findByUserName(trainingDto.getTrainerName());
        trainee.addTrainer(trainer);
        training.setTrainee(trainee);
        training.setTrainer(trainer);
        training = trainingRepository.save(training);
        log.info("Training id= {} saved in DB", training.getTrainingId());
        return training;
    }

    @Transactional(readOnly = true)
    @Override
    public List<Training> findAllWithFilters(FilterFormDto filterFormDto) {
        validator.validate(filterFormDto);
        Map<String, FilterDto<?>> filters = filterDtoConverter.convert(filterFormDto);
        Specification<Training> specification = trainingSpecification.getTraining(filters);
        return trainingRepository.findAll(specification);
    }
}
