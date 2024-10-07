package ua.hodik.gym.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.hodik.gym.dto.*;
import ua.hodik.gym.service.TraineeService;
import ua.hodik.gym.service.TrainerService;
import ua.hodik.gym.service.TrainingService;

import java.util.List;

@Log4j2
@RestController
@RequestMapping("/trainers")
public class TrainerController {
    public static final String TRANSACTION_ID = "transactionId";
    private final TrainerService trainerService;
    private final TrainingService trainingService;
    private final TraineeService traineeService;

    @Autowired
    public TrainerController(TrainerService trainerService, TrainingService trainingService, TraineeService traineeService) {
        this.trainerService = trainerService;
        this.trainingService = trainingService;
        this.traineeService = traineeService;
    }


    @PostMapping("/registration")
    public ResponseEntity<UserCredentialDto> registration(@RequestBody @Valid TrainerDto trainerDto) {
        UserCredentialDto credentialDto = trainerService.createTrainerProfile(trainerDto);
        return ResponseEntity.status(201).body(credentialDto);
    }

    @PatchMapping("/{username}")
    public ResponseEntity<String> updateTrainerActivityStatus(@PathVariable @NotBlank(message = "UserName can't be null or empty") String username,
                                                              @RequestBody boolean isActive) {
        trainerService.updateActiveStatus(username, isActive);
        return ResponseEntity.ok(String.format("Trainer %s active status updated", username));
    }

    @GetMapping
    public ResponseEntity<TrainerDto> getTrainer(@Valid @RequestBody UserNameDto userNameDto) {
        String userName = userNameDto.getUserName();
        TrainerDto trainerDto = trainerService.findTrainerDtoByUserName(userName);
        return ResponseEntity.ok(trainerDto);
    }

    @PutMapping("/{id:\\d+}")
    public ResponseEntity<TrainerDto> updateTrainer(@PathVariable int id, @Valid @RequestBody TrainerUpdateDto trainerDto) {
        TrainerDto trainer = trainerService.update(id, trainerDto);
        return ResponseEntity.ok(trainer);

    }

    @GetMapping("/{username}")
    public ResponseEntity<List<TrainerDto>> getNotAssignedTrainers(@PathVariable
                                                                   @NotBlank(message = "UserName can't be null or empty")
                                                                   String TraineeUsername) {
        trainerService.findByUserName(TraineeUsername);
        List<TrainerDto> notAssignedTrainers = trainerService.getNotAssignedTrainers(TraineeUsername);
        return ResponseEntity.ok(notAssignedTrainers);
    }

    @GetMapping("/training-list/{usernameDto}")
    public ResponseEntity<List<TrainingDto>> getTrainerTrainingList(@PathVariable UserNameDto usernameDto,
                                                                    @RequestBody @Valid FilterFormDto filterFormDto) {
        String userName = usernameDto.getUserName();
        trainerService.findByUserName(userName);
        filterFormDto.setTrainerName(userName);
        List<TrainingDto> allWithFilters = trainingService.findAllWithFilters(filterFormDto);
        return ResponseEntity.ok(allWithFilters);
    }
}