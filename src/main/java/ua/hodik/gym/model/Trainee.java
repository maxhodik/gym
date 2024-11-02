package ua.hodik.gym.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@EqualsAndHashCode(exclude = {"trainers", "trainings"})
@ToString(exclude = {"trainers", "trainings"})
@Entity
@Table(name = "Trainee")
@NamedEntityGraph(name = "Trainee.withUser", attributeNodes = @NamedAttributeNode("user"))
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Trainee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private int traineeId;
    @JsonFormat(pattern = "dd-MM-yyyy")
    @Column(name = "Date_of_Birth")
    private LocalDate dayOfBirth;
    @Column(name = "Address")
    private String address;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToMany()
    @JoinTable(name = "trainer_trainee",
            joinColumns = @JoinColumn(name = "trainee_id"),
            inverseJoinColumns = @JoinColumn(name = "trainer_id"))
    private List<Trainer> trainers = new ArrayList<>();
    @OneToMany(mappedBy = "trainee", cascade = CascadeType.REMOVE)
    private List<Training> trainings = new ArrayList<>();


    public void addTrainer(Trainer trainer) {
        trainers.add(trainer);
        trainer.getTrainees().add(this);
    }

    private void removeTrainer(Trainer trainer) {
        trainer.getTrainees().remove(this);
        trainers.remove(trainer);
    }

    public void addTraining(Training training) {
        training.setTrainee(this);
        trainings.add(training);
    }

    public void removeTraining(Training training) {
        trainings.remove(training);
        training.setTrainee(null);

    }

    public void addTrainersList(List<Trainer> trainerList) {
        for (Trainer t : trainerList) {
            t.getTrainees().add(this);
        }
        this.setTrainers(trainerList);
    }

}