package ua.hodik.gym.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
@Entity
@Table(name = "Trainee")
public class Trainee extends User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private int userId;
    @JsonFormat(pattern = "dd-MM-yyyy")
    @Column(name = "Data_of_Birth")
    private LocalDate dayOfBirth;
    @Column(name = "Address")
    private String address;

    @ManyToMany()
    @JoinTable(name = "trainer_trainee",
            joinColumns = @JoinColumn(name = "trainee_id"),
            inverseJoinColumns = @JoinColumn(name = "trainer_id"))
    private List<Trainer> trainers = new ArrayList<>();
    @OneToMany(mappedBy = "trainee_id")
    private List<Training> trainings = new ArrayList<>();
}
