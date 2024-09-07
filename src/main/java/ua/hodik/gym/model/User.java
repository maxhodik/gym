package ua.hodik.gym.model;


import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class User {
    private String firstName;
    private String lastName;
    private String userName;
    private String password;
    private boolean isActive;
}

