package ua.hodik.gym.model;

import lombok.Data;

import java.time.LocalDate;

@Data
public class Training {
    private int trainingId;
    private int traineeId;
    private int trainerId;
    private String name;
    private TrainingType trainingType;
    private LocalDate date;
    private int durationMinutes;
}
