package ua.hodik.gym.service.mapper;

import org.springframework.stereotype.Component;
import ua.hodik.gym.dto.TrainerDto;
import ua.hodik.gym.dto.TrainerRegistrationDto;
import ua.hodik.gym.dto.UserDto;
import ua.hodik.gym.model.Trainer;
import ua.hodik.gym.model.TrainingType;
import ua.hodik.gym.model.User;

@Component
public class TrainerMapper {


    public Trainer convertToTrainer(TrainerRegistrationDto trainerDto) {

        return Trainer.builder()
                .user(User.builder()
                        .firstName(trainerDto.getFirstName())
                        .lastName(trainerDto.getLastName())
                        .isActive(trainerDto.isActive())
                        .build())
                .specialization(TrainingType.valueOf(trainerDto.getSpecialization()))
                .build();
    }


    public TrainerDto convertToTrainerDto(Trainer trainer) {
        User user = trainer.getUser();
        return TrainerDto.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .userName(user.getUserName())
                .specialization(trainer.getSpecialization().toString())
                .build();
    }

    public UserDto convertToUserDto(TrainerDto trainerDto) {
        return UserDto.builder()
                .userName(trainerDto.getUserName())
                .firstName(trainerDto.getFirstName())
                .lastName(trainerDto.getLastName())
                .isActive(trainerDto.isActive())
                .build();
    }
}
