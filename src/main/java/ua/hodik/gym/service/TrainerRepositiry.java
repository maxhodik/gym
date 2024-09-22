package ua.hodik.gym.service;

import ua.hodik.gym.dto.TrainerDto;
import ua.hodik.gym.dto.UserCredentialDto;
import ua.hodik.gym.model.Trainer;

import java.util.List;

public interface TrainerRepositiry {

    Trainer findById(int id);

    List<Trainer> getAllTrainers();

    Trainer createTrainerProfile(TrainerDto trainerDto);

    boolean matchCredential(UserCredentialDto credential);

    Trainer changePassword(UserCredentialDto credential, String newPassword);

    Trainer update(UserCredentialDto credential, TrainerDto trainerDto);

    Trainer updateActiveStatus(UserCredentialDto credential, boolean isActive);

    List<Trainer> getNotAssignedTrainers(String traineeName);

    Trainer findByUserName(String trainerUserName);

}