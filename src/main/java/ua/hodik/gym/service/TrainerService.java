package ua.hodik.gym.service;

import ua.hodik.gym.dto.TrainerDto;
import ua.hodik.gym.dto.TrainerUpdateDto;
import ua.hodik.gym.dto.UserCredentialDto;
import ua.hodik.gym.model.Trainer;

import java.util.List;

public interface TrainerService {


    Trainer findById(int id);

    TrainerDto findTrainerDtoByUserName(String trainerUserName);

    List<Trainer> getAllTrainers();

    UserCredentialDto createTrainerProfile(TrainerDto trainerDto);


    TrainerDto update(int id, TrainerUpdateDto trainerDto);

    void updateActiveStatus(String userName, boolean isActive);

    List<TrainerDto> getNotAssignedTrainers(String traineeName);

    Trainer findByUserName(String trainerUserName);


}
