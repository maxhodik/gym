package ua.hodik.gym.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TrainerDto {

    private UserDto userDto;
    private int trainerId;
    private String specialization;

}
