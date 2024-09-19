package ua.hodik.gym.facade;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ua.hodik.gym.dto.FilterFormDto;
import ua.hodik.gym.dto.UserCredentialDto;
import ua.hodik.gym.model.Trainee;
import ua.hodik.gym.model.Trainer;
import ua.hodik.gym.model.Training;
import ua.hodik.gym.service.TraineeService;
import ua.hodik.gym.service.TrainerService;
import ua.hodik.gym.service.TrainingService;

import java.util.List;

@Component
public class Facade {
    private final TraineeService traineeService;
    private final TrainerService trainerService;
    private final TrainingService trainingService;

    @Autowired
    public Facade(TraineeService traineeService, TrainerService trainerService, TrainingService trainingService) {
        this.traineeService = traineeService;
        this.trainerService = trainerService;
        this.trainingService = trainingService;
    }

    public Trainee createTrainee(Trainee trainee) {
        return traineeService.create(trainee);
    }

    public Trainee updateTrainee(Trainee trainee, int id) {
        return traineeService.update(trainee, id);
    }

    public void deleteTrainee(int id) {
        traineeService.delete(id);
    }

    public Trainee findTraineeById(int id) {
        return traineeService.findById(id);
    }


    public Trainer findTrainerById(int id) {
        return trainerService.findById(id);
    }

    public Training createTraining(Training training) {
        return trainingService.create(training);
    }

    public Training findTrainingById(int id) {
        return trainingService.findById(id);
    }

    public List<Training> getTraineeTrainingList(FilterFormDto filterFormDto) {
        return trainingService.findAllWithFilters(filterFormDto);
    }

    public List<Training> getTrainerTrainingList(FilterFormDto filterFormDto) {
        filterFormDto.setTrainingType(null);
        return trainingService.findAllWithFilters(filterFormDto);
    }

    public List<Trainer> getNotAssignedTrainers(String traineeName) {
        return trainerService.getNotAssignedTrainers(traineeName);
    }

    public void updateTrainersList(UserCredentialDto credential, String traineeUserName, List<String> trainers) {
        traineeService.updateTrainersList(credential, traineeUserName, trainers);
    }

    public Trainer getTrainerByUserName(String userName) {
        return trainerService.findByUserName(userName);
    }
}

