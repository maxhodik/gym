package ua.hodik.gym;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import ua.hodik.gym.comfig.StorageConfig;

import java.util.Map;

public class App {
    public static void main(String[] args) {
        ApplicationContext context = new AnnotationConfigApplicationContext(StorageConfig.class);
        Map traineeDB = context.getBean("traineeDB", Map.class);
        System.out.println(traineeDB);
    }
}
