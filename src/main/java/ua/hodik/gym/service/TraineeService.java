package ua.hodik.gym.service;

import org.springframework.transaction.annotation.Transactional;
import ua.hodik.gym.dto.TraineeDto;
import ua.hodik.gym.dto.TrainerDto;
import ua.hodik.gym.dto.UserCredentialDto;
import ua.hodik.gym.dto.UserNameDto;
import ua.hodik.gym.model.Trainee;

import java.util.List;

public interface TraineeService {


    Trainee findById(int id);

    Trainee findByUserName(String userName);

    List<Trainee> getAllTrainees();

    UserCredentialDto createTraineeProfile(TraineeDto traineeDto);

    TraineeDto findTraineeDtoByUserName(String username);


    TraineeDto update(int id, TraineeDto traineeDto);

    void deleteTrainee(String userName);


    @Transactional()
    void updateActiveStatus(String userName, boolean isActive);

    List<TrainerDto> updateTrainersList(int traineeId, List<UserNameDto> trainerNameList);
}
