package ua.hodik.gym.service;

import ua.hodik.gym.dto.TrainerDto;
import ua.hodik.gym.dto.UserCredentialDto;
import ua.hodik.gym.model.Trainer;

import java.util.List;

public interface TrainerService {
    Trainer create(Trainer trainer);

    Trainer update(Trainer trainer, int id);

    boolean delete(int id);

    Trainer findById(int id);

    List<Trainer> getAllTrainers();

    Trainer createTrainerProfile(TrainerDto trainerDto);

    boolean matchCredential(UserCredentialDto credential);

    Trainer changePassword(UserCredentialDto credential, String newPassword);

}
