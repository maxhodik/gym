package ua.hodik.gym.model;


import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

@Data
@ToString(exclude = {"trainee", "trainer"})
@Entity
@Table(name = "\"User\"")

@NoArgsConstructor
@Builder
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private int Id;
    @Column(name = "First_name", nullable = false)
    private String firstName;
    @Column(name = "Last_Name", nullable = false)
    private String lastName;
    @Column(name = "Username", nullable = false)
    private String userName;
    @Column(name = "Password", nullable = false)
    private String password;
    @Column(name = "isActive", nullable = false)
    @JsonProperty("active")
    private boolean isActive;
    @OneToOne(mappedBy = "user")
    private Trainee trainee;
    @OneToOne(mappedBy = "user")
    private Trainer trainer;

    public void setTrainee(Trainee trainee) {
        this.trainee = trainee;
        trainee.setUser(this);
    }

    public void setTrainer(Trainer trainer) {
        this.trainer = trainer;
        trainer.setUser(this);
    }
}

