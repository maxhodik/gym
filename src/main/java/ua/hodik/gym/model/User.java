package ua.hodik.gym.model;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class User {
    private int userId;
    private String firstName;
    private String lastName;
    private String userName;
    private String password;
    private boolean isActive;
}

