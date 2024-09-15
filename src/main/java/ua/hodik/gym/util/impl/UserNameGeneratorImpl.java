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

        Stream<String> trainersUsernameStream = trainerService.getAllTrainers().stream()
                .map(Trainer::getUser)
                .map(User::getUserName);
        Stream<String> traineeUsernameStream = traineeService.getAllTrainees().stream()
                .map(Trainee::getUser)
                .map(User::getUserName);
        long count = Stream.concat(traineeUsernameStream, trainersUsernameStream)
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
