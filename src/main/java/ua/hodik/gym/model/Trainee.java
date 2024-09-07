package ua.hodik.gym.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

@Setter
@Getter
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
public class Trainee extends User {
    private int userId;
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate dayOfBirth;
    private String address;
}
