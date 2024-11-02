package ua.hodik.gym.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@EqualsAndHashCode(exclude = "trainees")
@ToString(exclude = "trainees")
@Entity
@Table(name = "Trainer")
@NamedEntityGraph(name = "Trainer.withUser", attributeNodes = @NamedAttributeNode("user"))
@AllArgsConstructor
@NoArgsConstructor
public class Trainer {
    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Enumerated(EnumType.STRING)
    @Column(name = "Specialization")
    private TrainingType specialization;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    private User user;
    @ManyToMany(mappedBy = "trainers")
    private List<Trainee> trainees = new ArrayList<>();

}
