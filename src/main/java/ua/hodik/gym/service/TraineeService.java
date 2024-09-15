package ua.hodik.gym.service;

import ua.hodik.gym.dto.TraineeDto;
import ua.hodik.gym.model.Trainee;

import java.util.List;

public interface TraineeService {
    Trainee create(Trainee trainee);


    Trainee update(Trainee trainee, int id);

    boolean delete(int id);

    Trainee findById(int id);

    List<Trainee> getAllTrainees();

    Trainee createTraineeProfile(TraineeDto traineeDto);
}
