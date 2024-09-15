package ua.hodik.gym.util.impl;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ua.hodik.gym.model.Trainee;
import ua.hodik.gym.model.Trainer;
import ua.hodik.gym.model.User;
import ua.hodik.gym.service.TraineeService;
import ua.hodik.gym.service.TrainerService;
import ua.hodik.gym.util.UserNameGenerator;

import java.util.List;
import java.util.stream.Stream;

@Component
@Log4j2
public class UserNameGeneratorImpl implements UserNameGenerator {
    @Autowired
    private TraineeService traineeService;
    @Autowired
    private TrainerService trainerService;

    @Override
    public String generateUserName(String firstName, String lastName) {
        String userName;
        String baseUsername = generateBaseUserName(firstName, lastName);
        List<Trainee> trainees = traineeService.getAllTrainees();
        List<Trainer> trainers = trainerService.getAllTrainers();
        long count = Stream.concat(trainees.stream(), trainers.stream())
                .map(User::getUserName)
                .map(u -> u.replaceAll("\\d+", ""))
                .filter(u -> u.equals(baseUsername))
                .count();
        if (count >= 1) {
            userName = baseUsername + count;
            log.info("UserName {} created", userName);
            return userName;
        }
        log.info("UserName {} created", baseUsername);
        return baseUsername;
    }

    private String generateBaseUserName(String firstName, String lastName) {
        return firstName + "." + lastName;
    }

}
