package ua.hodik.gym.service;

import org.springframework.transaction.annotation.Transactional;
import ua.hodik.gym.dto.TraineeDto;
import ua.hodik.gym.dto.TraineeUpdateDto;
import ua.hodik.gym.dto.UserCredentialDto;
import ua.hodik.gym.model.Trainee;

import java.util.List;

public interface TraineeService {


    Trainee findById(int id);

    Trainee findByUserName(String userName);

    List<Trainee> getAllTrainees();

    UserCredentialDto createTraineeProfile(TraineeDto traineeDto);

    TraineeDto findTraineeDtoByUserName(String username);

    Trainee changePassword(UserCredentialDto credential, String newPassword);

    TraineeDto update(int id, TraineeUpdateDto traineeDto);

    void deleteTrainee(String userName);


    @Transactional()
    void updateActiveStatus(String userName, boolean isActive);

    void updateTrainersList(UserCredentialDto credential, List<String> trainerNameList);
}
