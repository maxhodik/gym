package ua.hodik.gym.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

@Data
@AllArgsConstructor
public class UserCredentialDto {
    @NotEmpty(message = " Can't be empty")
    @Pattern(regexp = "[a-zA-Z]+\\.\\[a-zA-Z]+")
    private String userName;
    @NotEmpty(message = "Can't be empty")
    private String password;
}
