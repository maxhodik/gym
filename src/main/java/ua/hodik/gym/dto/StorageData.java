package ua.hodik.gym.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ua.hodik.gym.model.Trainee;
import ua.hodik.gym.model.Trainer;
import ua.hodik.gym.model.Training;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class StorageData {
    private List<Trainee> traineeList;
    private List<Trainer> trainerList;
    private List<Training> trainingList;
}
