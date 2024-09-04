package ua.hodik.gym.dao;

import ua.hodik.gym.model.Trainee;

import java.util.List;
import java.util.Optional;

public interface TraineeDao {
    Optional<Trainee> add(Trainee trainee);

    Optional<Trainee> update(Trainee trainee, int id);

    boolean delete(int id);

    Optional<Trainee> getById(int id);

    List<Trainee> getAllTrainees();

    List<Trainee> getAllTraineesByUserName(String userName);

    int getMaxId();
}
