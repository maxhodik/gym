package ua.hodik.gym.dao;

import ua.hodik.gym.model.Trainee;

import java.util.List;

public interface TraineeDao {
    Trainee add(Trainee trainee);

    Trainee update(Trainee trainee, int id);

    boolean delete(int id);

    Trainee getById(int id);

    List<Trainee> getAllTrainees();

    List<Trainee> getAllTraineesByUserName(String userName);

    int getMaxId();
}
