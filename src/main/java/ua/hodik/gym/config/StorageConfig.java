package ua.hodik.gym.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import ua.hodik.gym.model.Trainee;

import java.util.HashMap;
import java.util.Map;

@Configuration
@ComponentScan(basePackages = "ua.hodik.gym")
public class StorageConfig {
//    @Bean
//    @DependsOn("storageTrainerInitializer")
//    public Map<Integer, Trainer> trainerDB() {
//        return new HashMap<>();
//    }

    @Bean
    @DependsOn("storageTraineeInitializer")
    public Map<Integer, Trainee> traineeDB() {
        return new HashMap<>();
    }

//    @Bean
//    @DependsOn("storageTrainingInitializer")
//    public Map<Integer, Training> trainingDB() {
//        return new HashMap<>();
//    }

}
