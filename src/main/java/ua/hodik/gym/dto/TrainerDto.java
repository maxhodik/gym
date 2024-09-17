package ua.hodik.gym.dto;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import ua.hodik.gym.model.TrainingType;

@Data
@Builder
@ToString
public class TrainerDto {

    private UserDto userDto;
    private int trainerId;
    private TrainingType specialization;

}
