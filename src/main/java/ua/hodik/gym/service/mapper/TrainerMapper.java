package ua.hodik.gym.service.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ua.hodik.gym.dto.TrainerDto;
import ua.hodik.gym.dto.UserDto;
import ua.hodik.gym.model.Trainer;
import ua.hodik.gym.model.User;

@Component
public class TrainerMapper {
    private final ModelMapper modelMapper;
    private final UserMapper userMapper;

    @Autowired
    public TrainerMapper(ModelMapper modelMapper, UserMapper userMapper) {
        this.modelMapper = modelMapper;
        this.userMapper = userMapper;
    }


    public Trainer convertToTrainer(TrainerDto trainerDto) {
        return modelMapper.map(trainerDto, Trainer.class);
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
