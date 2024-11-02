package ua.hodik.gym.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import ua.hodik.gym.model.TrainingType;
import ua.hodik.gym.util.impl.validation.ValidTrainingTypeEnum;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class TrainerDto {
    @NotBlank(message = "Can't be null or empty")
    @Pattern(regexp = "[a-zA-Z]+", message = "There should be only letters")
    private String firstName;
    @NotBlank(message = "Can't be null or empty")
    @Pattern(regexp = "[a-zA-Z]+", message = "There should be only letters")
    private String lastName;
    @NotBlank(message = "Can't be null or empty")
    private String userName;
    @JsonProperty("active")
    private boolean isActive;
    @ValidTrainingTypeEnum(enumClass = TrainingType.class)
    private String specialization;
}
