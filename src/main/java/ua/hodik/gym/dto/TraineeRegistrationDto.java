package ua.hodik.gym.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
@Builder
@ToString()
@NoArgsConstructor
@AllArgsConstructor
@NotNull
public class TraineeRegistrationDto {

    @NotBlank(message = "Can't be null or empty")
    @Pattern(regexp = "[a-zA-Z]+", message = "There should be only letters")
    private String firstName;
    @NotBlank(message = "Can't be null or empty")
    @Pattern(regexp = "[a-zA-Z]+", message = "There should be only letters")
    private String lastName;

    @JsonProperty("active")
    private boolean isActive;
    @DateTimeFormat(pattern = "dd-MM-yyyy")
    @JsonFormat(pattern = "dd-MM-yyyy")
    @Past(message = "You haven't been born yet")
    private LocalDate dayOfBirth;

    private String address;

}
