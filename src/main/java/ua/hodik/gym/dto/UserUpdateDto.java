package ua.hodik.gym.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateDto {

    @NotBlank(message = "Can't be null or empty")
    @Pattern(regexp = "[a-zA-Z]+", message = "There should be only letters")
    private String firstName;
    @NotBlank(message = "Can't be null or empty")
    @Pattern(regexp = "[a-zA-Z]+", message = "There should be only letters")
    private String lastName;
    @NotBlank(message = "Can't be null or empty")
    private String userName;
    //    @NotBlank(message = "Can't be null or empty"
    @NotNull
    private Boolean isActive;

}
