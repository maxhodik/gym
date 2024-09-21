package ua.hodik.gym.dto;

import jakarta.validation.constraints.NotBlank;
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
    @NotBlank(message = "Can't be null or empty")

    private TrainingType specialization;

}
