package ua.hodik.gym.service.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ua.hodik.gym.dto.TrainerDto;
import ua.hodik.gym.model.Trainer;

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
        TrainerDto trainerDto = modelMapper.map(trainer, TrainerDto.class);
        trainerDto.setUserDto(userMapper.convertToUserDto(trainer.getUser()));
        return trainerDto;
    }
}
