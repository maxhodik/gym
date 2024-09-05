package ua.hodik.gym;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import ua.hodik.gym.config.StorageConfig;
import ua.hodik.gym.model.Trainee;
import ua.hodik.gym.service.TraineeService;

import java.time.LocalDate;
import java.util.Map;

public class App {
    public static void main(String[] args) {
        ApplicationContext context = new AnnotationConfigApplicationContext(StorageConfig.class);
        Map<Integer, Trainee> traineeDB = context.getBean("traineeDB", Map.class);
        System.out.println(traineeDB);
        TraineeService traineeService = context.getBean("traineeServiceImpl", TraineeService.class);

        traineeService.create(createTrainee("Semen", "Taran"));
        traineeService.create(createTrainee("Semen", "Taran"));
        traineeService.create(createTrainee("Semen", "Taran"));
        System.out.println(traineeDB);
    }

    private static Trainee createTrainee(String firstName, String lastName) {
        Trainee trainee= new Trainee();
        trainee.setDayOfBirth(LocalDate.of(1974, 10, 18));
        trainee.setAddress("Kyiv");
        trainee.setActive(true);
        trainee.setFirstName(firstName);
        trainee.setLastName(lastName);
        return trainee;
    }
}
