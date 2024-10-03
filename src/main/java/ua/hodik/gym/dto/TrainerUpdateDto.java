package ua.hodik.gym.dto;

import jakarta.validation.Valid;
import lombok.*;
import ua.hodik.gym.model.TrainingType;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class TrainerUpdateDto {
    @Valid
    private UserUpdateDto userUpdateDto;

    private TrainingType specialization;

}
