package ua.hodik.gym.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import ua.hodik.gym.model.TrainingType;
import ua.hodik.gym.util.impl.validation.ValidTrainingTypeEnum;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrainingDto {


    @NotBlank(message = "Can't be null or empty")
    private String traineeName;
    @NotBlank(message = "Can't be null or empty")
    private String trainerName;
    @NotBlank(message = "Can't be null or empty")
    private String name;
    @NotBlank(message = "Can't be null or empty")
    @ValidTrainingTypeEnum(enumClass = TrainingType.class)
    private String trainingType;
    @DateTimeFormat(pattern = "dd-MM-yyyy")
    @FutureOrPresent(message = "Should be today or latter")
    private LocalDate date;
    @Min(value = 10, message = "It's too short training")
    @Max(value = 120, message = "It's too long training")
    private int durationMinutes;
}
