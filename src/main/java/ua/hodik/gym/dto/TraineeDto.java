package ua.hodik.gym.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.Past;
import java.time.LocalDate;

@Data
@Builder
@ToString
public class TraineeDto {

    private UserDto userDto;
    private int traineeId;
    @JsonFormat(pattern = "dd-MM-yyyy")
    @Past(message = "You haven't been born yet")
    private LocalDate dayOfBirth;

    private String address;
}
