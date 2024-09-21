package ua.hodik.gym.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import ua.hodik.gym.model.TrainingType;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FilterFormDto {
    private String traineeName;
    @DateTimeFormat(pattern = "dd-MM-yyyy")
    private LocalDate dateFrom;
    @DateTimeFormat(pattern = "dd-MM-yyyy")
    private LocalDate dateTo;
    private String trainerName;

    private TrainingType trainingType;
}
