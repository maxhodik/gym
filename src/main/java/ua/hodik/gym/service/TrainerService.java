package ua.hodik.gym.service;

import ua.hodik.gym.dto.TrainerDto;
import ua.hodik.gym.dto.TrainerUpdateDto;
import ua.hodik.gym.dto.UserCredentialDto;
import ua.hodik.gym.model.Trainer;

import java.util.List;

public interface TrainerService {


    Trainer findById(int id);

    List<Trainer> getAllTrainers();

    UserCredentialDto createTrainerProfile(TrainerDto trainerDto);

    Trainer changePassword(UserCredentialDto credential, String newPassword);

    TrainerDto update(int id, TrainerUpdateDto trainerDto);

    void updateActiveStatus(String userName, boolean isActive);

    List<Trainer> getNotAssignedTrainers(String traineeName);

    Trainer findByUserName(String trainerUserName);
}
