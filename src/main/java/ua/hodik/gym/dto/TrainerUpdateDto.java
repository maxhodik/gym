package ua.hodik.gym.dto;

import jakarta.validation.Valid;
import lombok.*;
import ua.hodik.gym.model.TrainingType;
import ua.hodik.gym.util.impl.validation.ValidTrainingTypeEnum;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class TrainerUpdateDto {
    @Valid
    private UserUpdateDto userUpdateDto;
    @ValidTrainingTypeEnum(enumClass = TrainingType.class)
    private String specialization;
}
