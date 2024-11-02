package ua.hodik.gym.service.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ua.hodik.gym.dto.TraineeDto;
import ua.hodik.gym.dto.UserDto;
import ua.hodik.gym.model.Trainee;
import ua.hodik.gym.model.User;

@Component
public class TraineeMapper {
    private final ModelMapper modelMapper;
    private final TrainerMapper trainerMapper;

    @Autowired
    public TraineeMapper(ModelMapper modelMapper, TrainerMapper trainerMapper) {
        this.modelMapper = modelMapper;
        this.trainerMapper = trainerMapper;
    }

    public Trainee convertToTrainee(TraineeDto traineeDto) {
        return Trainee.builder()
                .user(User.builder()
                        .firstName(traineeDto.getFirstName())
                        .lastName(traineeDto.getLastName())
                        .userName(traineeDto.getUserName())
                        .isActive(traineeDto.isActive())
                        .build())
                .address(traineeDto.getAddress())
                .dayOfBirth(traineeDto.getDayOfBirth())
                .build();
    }

    public TraineeDto convertToTraineeDto(Trainee trainee) {
        User user = trainee.getUser();
        return TraineeDto.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .userName(user.getUserName())
                .dayOfBirth(trainee.getDayOfBirth())
                .address(trainee.getAddress())
                .trainerDtoList(trainee.getTrainers().stream()
                        .map(trainerMapper::convertToTrainerDto).toList())
                .build();
    }

    public UserDto convertToUserDto(TraineeDto traineeDto) {
        return UserDto.builder()
                .userName(traineeDto.getUserName())
                .firstName(traineeDto.getFirstName())
                .lastName(traineeDto.getLastName())
                .isActive(traineeDto.isActive())
                .build();
    }
}
