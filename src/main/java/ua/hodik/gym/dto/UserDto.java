package ua.hodik.gym.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

@Data
@Builder

public class UserDto {

    private int userId;
    @NotEmpty(message = "Can't be empty")
    @Pattern(regexp = "[a-zA-Z]+", message = "There should be only letters")
    private String firstName;
    @NotEmpty(message = "Can't be empty")
    @Pattern(regexp = "[a-zA-Z]+", message = "There should be only letters")
    private String lastName;

    private String userName;

    private String password;
    @NotEmpty(message = "Can't be empty")
    private boolean isActive;
}
