package ua.hodik.gym.service.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ua.hodik.gym.dto.TrainingDto;
import ua.hodik.gym.model.Training;

@Component
public class TrainingMapper {
    private ModelMapper modelMapper;

    @Autowired
    public TrainingMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public TrainingDto convertToTrainingDto(Training training) {
        return modelMapper.map(training, TrainingDto.class);
    }

    public Training convertToTraining(TrainingDto trainingDto) {
        return modelMapper.map(trainingDto, Training.class);
    }
}
