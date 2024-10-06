package ua.hodik.gym.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.hodik.gym.dto.*;
import ua.hodik.gym.service.TraineeService;

import java.util.List;

@RestController
@RequestMapping("/trainees")
@Log4j2
public class TraineeController {
    private final TraineeService traineeService;

    public TraineeController(TraineeService traineeService) {
        this.traineeService = traineeService;
    }

    @PostMapping("/registration")
    public ResponseEntity<UserCredentialDto> registration(@Valid @RequestBody TraineeDto traineeDto) {
        UserCredentialDto userCredentialDto = traineeService.createTraineeProfile(traineeDto);
        return ResponseEntity.status(201).body(userCredentialDto);
    }

    @GetMapping
    public ResponseEntity<TraineeDto> getTrainee(@Valid @RequestBody UserNameDto userName) {

        TraineeDto traineeDto = traineeService.findTraineeDtoByUserName(userName.getUserName());
        return ResponseEntity.ok(traineeDto);
    }

    @PutMapping("/{id:\\d+}")
    public ResponseEntity<TraineeDto> updateTrainee(@PathVariable int id,
                                                    @Valid @RequestBody TraineeUpdateDto traineeDto) {
        TraineeDto updatedTrainee = traineeService.update(id, traineeDto);
        return ResponseEntity.ok(updatedTrainee);
    }


    @DeleteMapping
    public ResponseEntity<String> deleteTrainee(@Valid @RequestBody UserNameDto userName) {
        traineeService.deleteTrainee(userName.getUserName());
        log.info("Trainee {} deleted successfully", userName);
        return ResponseEntity.ok(String.format("Trainee %s deleted successfully", userName));
    }

    @PatchMapping("/{username}")
    public ResponseEntity<String> updateTraineeActivityStatus(@PathVariable @NotBlank(message = "UserName can't be null or empty") String username,
                                                              @RequestBody Boolean isActive) {
        {
            traineeService.updateActiveStatus(username, isActive);
            return ResponseEntity.ok(String.format("Trainee %s active status updated", username));
        }
    }

    @PutMapping("/update-trainers/{id:\\d+}")
    public ResponseEntity<List<TrainerDto>> updateTraineeTrainersList(@PathVariable int id,
                                                                      @RequestBody @Valid List<UserNameDto> trainerNames) {
        List<TrainerDto> trainerDtoList = traineeService.updateTrainersList(id, trainerNames);
        return ResponseEntity.ok(trainerDtoList);
    }
}



