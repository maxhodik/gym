package ua.hodik.gym.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Data;


@Data
@Builder

public class UserDto {

    private int userId;
    @NotBlank(message = "Can't be null or empty")
    @Pattern(regexp = "[a-zA-Z]+", message = "There should be only letters")
    private String firstName;
    @NotBlank(message = "Can't be null or empty")
    @Pattern(regexp = "[a-zA-Z]+", message = "There should be only letters")
    private String lastName;

    private String userName;

    private String password;
    @NotBlank(message = "Can't be empty")
    private boolean isActive;
}
