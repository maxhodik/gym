package ua.hodik.gym.controller;

import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.hodik.gym.dto.TraineeDto;
import ua.hodik.gym.dto.UserCredentialDto;
import ua.hodik.gym.service.TraineeService;
import ua.hodik.gym.util.CredentialChecker;

@RestController
@RequestMapping("/trainees")
@Log4j2
public class TraineeController {
    private final TraineeService traineeService;
    private final CredentialChecker credentialChecker;

    public TraineeController(TraineeService traineeService, CredentialChecker credentialChecker) {
        this.traineeService = traineeService;
        this.credentialChecker = credentialChecker;
    }

    @PostMapping("/registration")
    public ResponseEntity<UserCredentialDto> registration(@Valid @RequestBody TraineeDto traineeDto) {
        UserCredentialDto userCredentialDto = traineeService.createTraineeProfile(traineeDto);
        return ResponseEntity.status(201).body(userCredentialDto);
    }



}


