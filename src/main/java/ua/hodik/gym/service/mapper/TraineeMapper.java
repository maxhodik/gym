package ua.hodik.gym.service.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ua.hodik.gym.dto.TraineeDto;
import ua.hodik.gym.model.Trainee;

@Component
public class TraineeMapper {
    private ModelMapper modelMapper;

    @Autowired
    public TraineeMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }


    public Trainee convertToTrainee(TraineeDto traineeDto) {
        return modelMapper.map(traineeDto, Trainee.class);
    }

    public TraineeDto convertToTraineeDto(Trainee trainee) {
        return modelMapper.map(trainee, TraineeDto.class);
    }
}
