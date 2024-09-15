package ua.hodik.gym.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@Entity
@Table(name = "Training")
public class Training {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private int trainingId;

    @ManyToOne
    @JoinColumn(name = "Trainee_id", nullable = false)
    private Trainee trainee;
    @ManyToOne
    @JoinColumn(name = "Trainer_id", nullable = false)
    private Trainer trainer;
    @Column(name = "Training_name", nullable = false)
    private String name;
    @OneToOne
    @JoinColumn(name = "Training_Type_id")
    private TrainingType trainingType;
    @Column(name = "Training_Date", nullable = false)
    private LocalDate date;
    @Column(name = "Training_Duration", nullable = false)
    private int durationMinutes;
}
