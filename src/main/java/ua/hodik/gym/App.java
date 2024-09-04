package ua.hodik.gym;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import ua.hodik.gym.config.StorageConfig;
import ua.hodik.gym.model.Trainee;
import ua.hodik.gym.util.PasswordGenerator;

import java.util.Map;

public class App {
    public static void main(String[] args) {
        ApplicationContext context = new AnnotationConfigApplicationContext(StorageConfig.class);
        Map<Integer, Trainee> traineeDB = context.getBean("traineeDB", Map.class);
        System.out.println(traineeDB);
        PasswordGenerator passwordGenerator = context.getBean("passwordGenerator", PasswordGenerator.class);
        for (int i = 0; i < 25; i++) {
            System.out.println(passwordGenerator.generatePassword());
        }

    }
}
