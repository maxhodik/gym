package ua.hodik.gym.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
@Builder
@ToString()
@NoArgsConstructor
@AllArgsConstructor
@NotNull
public class TraineeUpdateDto {
    @Valid
    private UserUpdateDto userUpdateDto;
    @DateTimeFormat(pattern = "dd-MM-yyyy")
    @JsonFormat(pattern = "dd-MM-yyyy")
    @Past(message = "You haven't been born yet")
    private LocalDate dayOfBirth;

    private String address;
}
