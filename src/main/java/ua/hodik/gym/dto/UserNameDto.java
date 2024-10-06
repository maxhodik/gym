package ua.hodik.gym.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserNameDto {
    @NotBlank(message = "Can't be null or empty")
    private String userName;
}
