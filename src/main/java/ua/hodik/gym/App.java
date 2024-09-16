package ua.hodik.gym;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import ua.hodik.gym.config.StorageConfig;
import ua.hodik.gym.dto.TraineeDto;
import ua.hodik.gym.dto.UserCredentialDto;
import ua.hodik.gym.dto.UserDto;
import ua.hodik.gym.service.TraineeService;

import java.time.LocalDate;

public class App {
    public static void main(String[] args) {
        ApplicationContext context = new AnnotationConfigApplicationContext(StorageConfig.class);
        TraineeService traineeService = context.getBean("traineeServiceImpl", TraineeService.class);
        TraineeDto traineeDto = getTraineeDto();
//        traineeService.createTraineeProfile(traineeDto);
        UserCredentialDto credential = new UserCredentialDto("Sam.Petrov", "yCnQatZkQy");
        traineeService.changePassword(credential, "1111111");
    }

    private static TraineeDto getTraineeDto() {
        UserDto userDto = UserDto.builder()
                .firstName("Sam")
                .lastName("Petrov")
                .isActive(true)
                .build();
        TraineeDto traineeDto = TraineeDto.builder()
                .userDto(userDto)
                .address("Kyiv")
                .dayOfBirth(LocalDate.ofEpochDay(10 - 12 - 1999))
                .build();
        return traineeDto;
    }
}
