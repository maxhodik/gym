package ua.hodik.gym.dto.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ua.hodik.gym.dto.TrainerDto;
import ua.hodik.gym.model.Trainer;

@Component
public class TrainerMapper {
    private ModelMapper modelMapper;

    @Autowired
    public TrainerMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }


    public Trainer convertToTrainer(TrainerDto trainerDto) {
        return modelMapper.map(trainerDto, Trainer.class);
    }

    public TrainerDto convertToTrainerDto(Trainer trainer) {
        return modelMapper.map(trainer, TrainerDto.class);
    }
}
