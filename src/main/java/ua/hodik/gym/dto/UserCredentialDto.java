package ua.hodik.gym.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserCredentialDto {

    @NotBlank(message = " Can't be null or empty")
    @Pattern(regexp = "^[A-Za-z]+\\.[A-Za-z]+[0-9]*$")
    private String userName;
    @NotBlank(message = "Can't be null or empty")
    private String password;
}
