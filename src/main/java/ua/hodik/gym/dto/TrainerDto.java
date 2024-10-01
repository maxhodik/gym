package ua.hodik.gym.dto;

import jakarta.validation.Valid;
import lombok.*;
import ua.hodik.gym.model.TrainingType;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class TrainerDto {
    @Valid
    private UserDto userDto;
    private int trainerId;

    private TrainingType specialization;

}
