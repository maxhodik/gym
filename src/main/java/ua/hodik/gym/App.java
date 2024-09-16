package ua.hodik.gym;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import ua.hodik.gym.config.StorageConfig;
import ua.hodik.gym.dto.TraineeDto;
import ua.hodik.gym.dto.TrainerDto;
import ua.hodik.gym.dto.UserCredentialDto;
import ua.hodik.gym.dto.UserDto;
import ua.hodik.gym.service.TraineeService;
import ua.hodik.gym.service.TrainerService;

import java.time.LocalDate;

public class App {
    public static void main(String[] args) {
        ApplicationContext context = new AnnotationConfigApplicationContext(StorageConfig.class);
        TraineeService traineeService = context.getBean("traineeServiceImpl", TraineeService.class);
        TrainerService trainerService = context.getBean("trainerServiceImpl", TrainerService.class);

        TraineeDto traineeDto = getTraineeDto();
        TrainerDto trainerDto = getTrainerDto();
//      traineeService.createTraineeProfile(traineeDto);

//      trainerService.createTrainerProfile(trainerDto);
        UserCredentialDto credential = new UserCredentialDto("Sam.Obama4", "vFvplkZYeP");
//        traineeService.updateActiveStatus(credential, false);
//       traineeService.update(credential, traineeDto);
//        traineeService.deleteTrainee(credential);
//
        trainerService.updateActiveStatus(credential, true);
        trainerService.update(credential, trainerDto);

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
                .specialization("Dancing")
                .build();
    }
}
