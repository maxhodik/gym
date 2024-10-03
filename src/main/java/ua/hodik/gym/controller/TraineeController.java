package ua.hodik.gym.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.hodik.gym.dto.TraineeDto;
import ua.hodik.gym.dto.TraineeUpdateDto;
import ua.hodik.gym.dto.UserCredentialDto;
import ua.hodik.gym.dto.UserNameDto;
import ua.hodik.gym.service.TraineeService;

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

    @PatchMapping()
    public ResponseEntity<String> updateTraineeActivityStatus(@Valid @RequestBody UserNameDto userNameDto,
                                                              @NotBlank(message = "Can't be null or empty") boolean isActive) {
        traineeService.updateActiveStatus(userNameDto.getUserName(), isActive);
        return ResponseEntity.ok(String.format("Trainee %s active status updated", userNameDto.getUserName()));
    }
}


