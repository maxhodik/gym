package ua.hodik.gym.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserNameDto {
    @NotBlank(message = "Can't be null or empty")
    private String userName;
}
