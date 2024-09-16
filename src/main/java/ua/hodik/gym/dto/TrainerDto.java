package ua.hodik.gym.dto;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@Builder
@ToString
public class TrainerDto {

    private UserDto userDto;
    private int trainerId;
    private String specialization;

}
