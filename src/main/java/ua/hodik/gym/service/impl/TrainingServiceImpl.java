package ua.hodik.gym.service.impl;

import lombok.extern.log4j.Log4j2;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.hodik.gym.dao.TrainingSpecification;
import ua.hodik.gym.dto.FilterDto;
import ua.hodik.gym.dto.FilterFormDto;
import ua.hodik.gym.dto.TrainingDto;
import ua.hodik.gym.dto.TrainingTypeDto;
import ua.hodik.gym.exception.EntityNotFoundException;
import ua.hodik.gym.model.Trainee;
import ua.hodik.gym.model.Trainer;
import ua.hodik.gym.model.Training;
import ua.hodik.gym.model.TrainingType;
import ua.hodik.gym.repository.TrainingRepository;
import ua.hodik.gym.service.TraineeService;
import ua.hodik.gym.service.TrainerService;
import ua.hodik.gym.service.TrainingService;
import ua.hodik.gym.service.mapper.FilterDtoConverter;
import ua.hodik.gym.service.mapper.TrainingMapper;
import ua.hodik.gym.service.mapper.TrainingTypeMapper;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
@Log4j2
public class TrainingServiceImpl implements TrainingService {

    public static final String TRANSACTION_ID = "transactionId";
    private final TrainingRepository trainingRepository;
    private final TrainingMapper trainingMapper;
    private final TrainingTypeMapper trainingTypeMapper;
    private final TraineeService traineeService;
    private final TrainerService trainerService;
    private final FilterDtoConverter filterDtoConverter;
    private final TrainingSpecification trainingSpecification;

    @Autowired
    public TrainingServiceImpl(TrainingRepository trainingRepository, TrainingMapper trainingMapper, TrainingTypeMapper trainingTypeMapper, TraineeService traineeService, TrainerService trainerService, FilterDtoConverter filterDtoConverter, TrainingSpecification trainingSpecification) {
        this.trainingRepository = trainingRepository;
        this.trainingMapper = trainingMapper;
        this.trainingTypeMapper = trainingTypeMapper;
        this.traineeService = traineeService;
        this.trainerService = trainerService;
        this.filterDtoConverter = filterDtoConverter;
        this.trainingSpecification = trainingSpecification;
    }


    @Override
    public Training findById(int id) {
        return trainingRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Training with id= %s not found", id)));
    }

    @Transactional
    @Override
    public Training createTraining(TrainingDto trainingDto) {
        Training training = trainingMapper.convertToTraining(trainingDto);
        Trainee trainee = traineeService.findByUserName(trainingDto.getTraineeName());
        Trainer trainer = trainerService.findByUserName(trainingDto.getTrainerName());
        trainee.addTrainer(trainer);
        training.setTrainee(trainee);
        training.setTrainer(trainer);
        training = trainingRepository.save(training);
        log.debug("[TrainingController] Training id= {} saved in DB, TransactionId {}", training.getTrainingId(), MDC.get(TRANSACTION_ID));
        return training;
    }

    @Transactional(readOnly = true)
    @Override
    public List<TrainingDto> findAllWithFilters(FilterFormDto filterFormDto) {
        Map<String, FilterDto<?>> filters = filterDtoConverter.convert(filterFormDto);
        Specification<Training> specification = trainingSpecification.getTraining(filters);
        List<Training> trainings = trainingRepository.findAll(specification);
        log.debug("[TrainingService] Finding training list with filters, TransactionId {}", MDC.get(TRANSACTION_ID));
        return trainings.stream()
                .map(trainingMapper::convertToTrainingDto)
                .toList();

    }

    @Override
    public List<TrainingTypeDto> getTrainingType() {
        List<TrainingTypeDto> trainingTypeDtoList = Arrays.stream(TrainingType.values())
                .map(trainingTypeMapper::convertToTrainingTypeDto)
                .toList();
        log.debug("[TrainingService] Get trainingType. TransactionId {}", MDC.get(TRANSACTION_ID));
        return trainingTypeDtoList;
    }
}

