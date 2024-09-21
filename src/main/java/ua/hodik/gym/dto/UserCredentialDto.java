package ua.hodik.gym.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserCredentialDto {

    @NotEmpty(message = " Can't be empty")
    @Pattern(regexp = "^[A-Za-z]+\\.[A-Za-z]+[0-9]*$")
    private String userName;
    @NotEmpty(message = "Can't be empty")
    private String password;
}
