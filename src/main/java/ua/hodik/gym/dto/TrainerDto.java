package ua.hodik.gym.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import ua.hodik.gym.model.TrainingType;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class TrainerDto {

    private UserDto userDto;
    private int trainerId;
    @NotBlank(message = "Can't be null or empty")

    private TrainingType specialization;

}
