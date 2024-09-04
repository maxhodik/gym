package ua.hodik.gym.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(of = "userId", callSuper = false)
@ToString(callSuper = true)
public class Trainer extends User {
    private String specialization;
    private int userId;
}
