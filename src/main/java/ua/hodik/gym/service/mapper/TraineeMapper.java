package ua.hodik.gym.service.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ua.hodik.gym.dto.TraineeDto;
import ua.hodik.gym.model.Trainee;

@Component
public class TraineeMapper {
    private final ModelMapper modelMapper;
    private final UserMapper userMapper;
    private final TrainerMapper trainerMapper;

    @Autowired
    public TraineeMapper(ModelMapper modelMapper, UserMapper userMapper, TrainerMapper trainerMapper) {
        this.modelMapper = modelMapper;
        this.userMapper = userMapper;
        this.trainerMapper = trainerMapper;
    }

    public Trainee convertToTrainee(TraineeDto traineeDto) {
                return modelMapper.map(traineeDto, Trainee.class);
    }

    public TraineeDto convertToTraineeDto(Trainee trainee) {
        TraineeDto traineeDto = modelMapper.map(trainee, TraineeDto.class);
        traineeDto.setUserDto(userMapper.convertToUserDto(trainee.getUser()));
        traineeDto.setTrainerDtoList(trainee.getTrainers().stream()
                .map(trainerMapper::convertToTrainerDto).toList());
        return traineeDto;
    }
}
