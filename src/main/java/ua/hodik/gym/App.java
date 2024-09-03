package ua.hodik.gym;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import ua.hodik.gym.config.StorageConfig;
import ua.hodik.gym.model.Trainee;

import java.util.Map;

public class App {
    public static void main(String[] args) {
        ApplicationContext context = new AnnotationConfigApplicationContext(StorageConfig.class);
        Map<Integer, Trainee> traineeDB = context.getBean("traineeDB", Map.class);
        System.out.println(traineeDB);
    }
}
