package ua.hodik.gym;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import ua.hodik.gym.config.StorageConfig;
import ua.hodik.gym.dto.*;
import ua.hodik.gym.model.Trainee;
import ua.hodik.gym.model.Trainer;
import ua.hodik.gym.model.TrainingType;
import ua.hodik.gym.service.TraineeService;
import ua.hodik.gym.service.TrainerService;
import ua.hodik.gym.service.TrainingService;

import java.time.LocalDate;

public class App {
    public static void main(String[] args) {
        ApplicationContext context = new AnnotationConfigApplicationContext(StorageConfig.class);
        TraineeService traineeService = context.getBean("traineeServiceImpl", TraineeService.class);
        TrainerService trainerService = context.getBean("trainerServiceImpl", TrainerService.class);
        TrainingService trainingService = context.getBean("trainingServiceImpl", TrainingService.class);

        TraineeDto traineeDto = getTraineeDto();
        TrainerDto trainerDto = getTrainerDto();
        traineeService.createTraineeProfile(traineeDto);

        trainerService.createTrainerProfile(trainerDto);

        Trainer trainer = trainerService.findById(1);
        Trainee trainee = traineeService.findById(1);
        TrainingDto trainingDto = getTrainingDto(trainee, trainer);
        trainingService.createTraining(trainingDto);
        UserCredentialDto credential = new UserCredentialDto("Sam.Obama4", "vFvplkZYeP");
//        traineeService.updateActiveStatus(credential, false);
//       traineeService.update(credential, traineeDto);
//        traineeService.deleteTrainee(credential);
//
//        trainerService.updateActiveStatus(credential, true);
//        trainerService.update(credential, trainerDto);

    }

    private static TrainingDto getTrainingDto(Trainee trainee, Trainer trainer) {
        return TrainingDto.builder()
                .date(LocalDate.now().plusDays(1))
                .name("Boxing")
                .durationMinutes(40)
                .trainer(trainer)
                .trainee(trainee)
                .trainingType(TrainingType.BOXING)
                .build();
    }

    private static TraineeDto getTraineeDto() {
        UserDto userDto = UserDto.builder()
                .firstName("Jon")
                .lastName("Ivanov")
//                .userName("Jon.Ivanov")
                .isActive(true)
                .build();
        return TraineeDto.builder()
                .userDto(userDto)
                .address("NY")
                .dayOfBirth(LocalDate.of(2003, 10, 25))
                .build();
    }

    private static TrainerDto getTrainerDto() {
        UserDto userDto = UserDto.builder()
                .firstName("Yura")
                .lastName("Vasil")
                .userName("Sam.Obama4")
                .isActive(true)
                .build();
        return TrainerDto.builder()
                .userDto(userDto)
                .specialization(TrainingType.BOXING)
                .build();
    }
}
