package ua.hodik.gym.service.impl;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.hodik.gym.dao.TrainingDao;
import ua.hodik.gym.dao.TrainingSpecification;
import ua.hodik.gym.dto.FilterDto;
import ua.hodik.gym.dto.FilterFormDto;
import ua.hodik.gym.dto.TrainingDto;
import ua.hodik.gym.dto.mapper.ConvertToFilterDto;
import ua.hodik.gym.dto.mapper.TrainingMapper;
import ua.hodik.gym.model.Trainee;
import ua.hodik.gym.model.Trainer;
import ua.hodik.gym.model.Training;
import ua.hodik.gym.repository.TrainingRepository;
import ua.hodik.gym.service.TraineeService;
import ua.hodik.gym.service.TrainerService;
import ua.hodik.gym.service.TrainingService;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@Log4j2
public class TrainingServiceImpl implements TrainingService {
    @Autowired
    private TrainingDao trainingDao;
    private final TrainingRepository trainingRepository;
    private final TrainingMapper trainingMapper;
    private final TraineeService traineeService;
    private final TrainerService trainerService;
    private final ConvertToFilterDto convertToFilterDto;
    private final TrainingSpecification trainingSpecification;

    @Autowired
    public TrainingServiceImpl(TrainingRepository trainingRepository, TrainingMapper trainingMapper, TraineeService traineeService, TrainerService trainerService, ConvertToFilterDto convertToFilterDto, TrainingSpecification trainingSpecification) {
        this.trainingRepository = trainingRepository;
        this.trainingMapper = trainingMapper;
        this.traineeService = traineeService;
        this.trainerService = trainerService;
        this.convertToFilterDto = convertToFilterDto;
        this.trainingSpecification = trainingSpecification;
    }

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

    @Transactional()
    public Training createTraining(TrainingDto trainingDto) {
        Training training = trainingMapper.convertToTraining(trainingDto);
        Trainee trainee = traineeService.findById(trainingDto.getTrainee().getTraineeId());
        Trainer trainer = trainerService.findById(trainingDto.getTrainer().getId());
        trainee.addTrainer(trainer);
        training = trainingRepository.save(training);
        log.info("Training id= {} saved in DB", training.getTrainingId());
        return training;
    }

    public List<Training> findAllWithFilters(FilterFormDto filterFormDto) {
        Map<String, FilterDto<?>> filters = convertToFilterDto.convert(filterFormDto);
        Specification<Training> specification = trainingSpecification.getTraining(filters);
        return trainingRepository.findAll(specification);
    }
}
