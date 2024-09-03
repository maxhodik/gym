package ua.hodik.gym.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class Trainee extends User {
    private int userId;
//    private LocalDate dayOfBirth;
    private String address;
}
