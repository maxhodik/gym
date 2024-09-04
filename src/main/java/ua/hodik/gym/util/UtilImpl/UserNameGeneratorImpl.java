package ua.hodik.gym.util.UtilImpl;

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
public class UserNameGeneratorImpl implements UserNameGenerator {
    @Autowired
    private TraineeService traineeService;
    @Autowired
    private TrainerService trainerService;

    @Override
    public String generateUserName(String firstName, String lastName) {
        String baseUsername = generateBaseUserName(firstName, lastName);
        List<Trainee> trainees = traineeService.getAllTrainees();
        List<Trainer> trainers = trainerService.getAllTrainers();
        long count = Stream.concat(trainees.stream(), trainers.stream())
                .map(User::getUserName)
                .filter(u -> u.contains(baseUsername))
                .count();
        if (count > 1) {
            return baseUsername + count;
        }
        return baseUsername;
    }

    private String generateBaseUserName(String firstName, String lastName) {
        return firstName + "." + lastName;
    }

}
