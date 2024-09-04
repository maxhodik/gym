package ua.hodik.gym.service;

import ua.hodik.gym.model.Trainee;

import java.util.List;
import java.util.Optional;

public interface TraineeService {
    Optional<Trainee> create(Trainee trainee);

    Optional<Trainee> update(Trainee trainee, int id);

    boolean delete(int id);

    Optional<Trainee> findById(int id);

    List<Trainee> getAllTrainees();
}
