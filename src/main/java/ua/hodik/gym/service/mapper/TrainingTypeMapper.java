package ua.hodik.gym.service.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ua.hodik.gym.dto.TrainingTypeDto;
import ua.hodik.gym.model.TrainingType;

@Component
public class TrainingTypeMapper {

    private final ModelMapper modelMapper;

    @Autowired
    public TrainingTypeMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public TrainingTypeDto convertToTrainingTypeDto(TrainingType trainingType) {
        return new TrainingTypeDto(trainingType.name(), trainingType.getID());
    }
}
