package ua.hodik.gym.service;

import ua.hodik.gym.dto.TraineeDto;
import ua.hodik.gym.dto.UserCredentialDto;
import ua.hodik.gym.model.Trainee;

import java.util.List;

public interface TraineeService {




    Trainee findById(int id);

    Trainee findByUserName(String userName);

    List<Trainee> getAllTrainees();

    UserCredentialDto createTraineeProfile(TraineeDto traineeDto);

    Trainee changePassword(UserCredentialDto credential, String newPassword);

    Trainee update(UserCredentialDto credential, TraineeDto traineeDto);

    void deleteTrainee(UserCredentialDto credentialDto);

    Trainee updateActiveStatus(UserCredentialDto credential, boolean isActive);

    void updateTrainersList(UserCredentialDto credential, List<String> trainerNameList);
}
