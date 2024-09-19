package ua.hodik.gym.service;

import ua.hodik.gym.dto.TraineeDto;
import ua.hodik.gym.dto.UserCredentialDto;
import ua.hodik.gym.model.Trainee;

import java.util.List;

public interface TraineeService {


    void delete(int id);

    Trainee findById(int id);

    List<Trainee> getAllTrainees();

    Trainee createTraineeProfile(TraineeDto traineeDto);

    boolean matchCredential(UserCredentialDto credential);

    Trainee changePassword(UserCredentialDto credential, String newPassword);

    Trainee update(UserCredentialDto credential, TraineeDto traineeDto);

    void deleteTrainee(UserCredentialDto credentialDto);

    Trainee updateActiveStatus(UserCredentialDto credential, boolean isActive);

    void updateTrainersList(UserCredentialDto credential, String traineeUserName, List<String> trainers);
}
