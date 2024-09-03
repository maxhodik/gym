package ua.hodik.gym.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import ua.hodik.gym.model.Trainee;
import ua.hodik.gym.model.Trainer;
import ua.hodik.gym.model.Training;

import java.util.HashMap;
import java.util.Map;

@Configuration
@ComponentScan("ua.hodik.gym")
@PropertySource("classpath:application.properties")
public class storageConfig {

    @Bean
    public Map<Integer, Trainee> traineeDB (){
        return new HashMap<>();
    }
    @Bean
    public Map<Integer, Trainer> trainerDB (){
        return new HashMap<>();
    }
    @Bean
    public Map<Integer, Training> trainingDB (){
        return new HashMap<>();
    }

}
