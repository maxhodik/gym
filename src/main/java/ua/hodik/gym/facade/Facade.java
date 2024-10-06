package ua.hodik.gym.facade;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ua.hodik.gym.dto.TrainingDto;
import ua.hodik.gym.model.Trainee;
import ua.hodik.gym.model.Trainer;
import ua.hodik.gym.model.Training;
import ua.hodik.gym.service.TraineeService;
import ua.hodik.gym.service.TrainerService;
import ua.hodik.gym.service.TrainingService;

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




    public Trainee findTraineeById(int id) {
        return traineeService.findById(id);
    }


    public Trainer findTrainerById(int id) {
        return trainerService.findById(id);
    }

    public Training createTraining(TrainingDto training) {
        return trainingService.createTraining(training);
    }

    public Training findTrainingById(int id) {
        return trainingService.findById(id);
    }

//    public List<Training> getTraineeTrainingList(FilterFormDto filterFormDto) {
//        return trainingService.findAllWithFilters(filterFormDto);
//    }
//
//    public List<Training> getTrainerTrainingList(FilterFormDto filterFormDto) {
//        filterFormDto.setTrainingType(null);
//        return trainingService.findAllWithFilters(filterFormDto);
//    }


    public Trainer getTrainerByUserName(String userName) {
        return trainerService.findByUserName(userName);
    }
}

