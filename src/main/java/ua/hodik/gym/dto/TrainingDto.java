package ua.hodik.gym.dto;

import lombok.Builder;
import lombok.Data;
import ua.hodik.gym.model.Trainee;
import ua.hodik.gym.model.Trainer;
import ua.hodik.gym.model.TrainingType;

import java.time.LocalDate;

@Data
@Builder
public class TrainingDto {

    private int trainingId;

    private Trainee trainee;

    private Trainer trainer;

    private String name;

    private TrainingType trainingType;

    private LocalDate date;

    private int durationMinutes;
}
