package ua.hodik.gym;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import ua.hodik.gym.config.HibernateConfig;
import ua.hodik.gym.dto.*;
import ua.hodik.gym.facade.Facade;
import ua.hodik.gym.model.Trainee;
import ua.hodik.gym.model.Trainer;
import ua.hodik.gym.model.TrainingType;
import ua.hodik.gym.service.TraineeService;
import ua.hodik.gym.service.TrainerService;
import ua.hodik.gym.service.TrainingService;

import java.time.LocalDate;
import java.util.List;

public class App {
    public static void main(String[] args) {
        ApplicationContext context = new AnnotationConfigApplicationContext(HibernateConfig.class);
        TraineeService traineeService = context.getBean("traineeServiceImpl", TraineeService.class);
        TrainerService trainerService = context.getBean("trainerServiceImpl", TrainerService.class);
        TrainingService trainingService = context.getBean("trainingServiceImpl", TrainingService.class);
        Facade facade = context.getBean("facade", Facade.class);

        TraineeDto traineeDto = getTraineeDto();
        TrainerDto trainerDto = getTrainerDto();
//        traineeService.createTraineeProfile(traineeDto);

//        trainerService.createTrainerProfile(trainerDto);

//        Trainer trainer = trainerService.findById(7);
//        Trainee trainee = traineeService.findById(1);
//        TrainingDto trainingDto = getTrainingDto(trainee, trainer);
//       trainingService.createTraining(trainingDto);
//        UserCredentialDto credential = new UserCredentialDto("Jon.Ivanov1", "MlPyvxOSAw");
//        traineeService.updateActiveStatus(credential, false);
//        traineeService.update(credential, traineeDto);
//        traineeService.deleteTrainee(credential);
//
//        trainerService.updateActiveStatus(credential, true);
//        trainerService.update(credential, trainerDto);
        FilterFormDto filterFormDto = FilterFormDto.builder()
                .traineeName("Jon.Ivanov")
                .trainerName("Yura.Vasil")
                .trainingType("BOXING")
                .dateFrom(LocalDate.of(2024, 9, 6))
                .dateTo(LocalDate.of(2024, 9, 25))
                .build();
//        System.out.println(filterFormDto);
//        List<Training> allWithFilters = facade.getTraineeTrainingList(filterFormDto);
//        List<Training> trainerList=facade.getTrainerTrainingList(filterFormDto);
        List<Trainer> notAssignedTrainers = facade.getNotAssignedTrainers("Jon.Ivanov");
//        notAssignedTrainers.forEach((t) -> System.out.println(t.getUser()));
        System.out.println(notAssignedTrainers);
//        facade.updateTrainersList(credential, List.of("Yura.Vasil4"));
//        System.out.println(allWithFilters);
//        System.out.println(trainerList);
    }

    private static TrainingDto getTrainingDto(Trainee trainee, Trainer trainer) {
        return TrainingDto.builder()
                .date(LocalDate.now().plusDays(1))
                .name("Boxing")
                .durationMinutes(50)
                .trainerName(trainer.getUser().getUserName())
                .traineeName(trainee.getUser().getUserName())
                .trainingType("STRETCHING")
                .build();
    }

    private static TraineeDto getTraineeDto() {
        UserDto userDto = UserDto.builder()
                .firstName("Jon")
                .lastName("Ivanov")
//                .trainerName("Jon.Ivanov")
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
                .firstName("Igor")
                .lastName("Li")
//                .userName("Sam.Obama4")
                .isActive(true)
                .build();
        return TrainerDto.builder()
                .userDto(userDto)
                .specialization(TrainingType.STRETCHING)
                .build();
    }
}
