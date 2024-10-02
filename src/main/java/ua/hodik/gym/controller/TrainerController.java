package ua.hodik.gym.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.hodik.gym.dto.TrainerDto;
import ua.hodik.gym.dto.UserCredentialDto;
import ua.hodik.gym.service.TrainerService;

@RestController
@RequestMapping("/trainers")
public class TrainerController {
    private final TrainerService trainerService;

    public TrainerController(TrainerService trainerService) {
        this.trainerService = trainerService;
    }

    @PostMapping("/registration")
    public ResponseEntity<UserCredentialDto> registration(@RequestBody @Valid TrainerDto trainerDto) {
        UserCredentialDto credentialDto = trainerService.createTrainerProfile(trainerDto);
        return ResponseEntity.status(201).body(credentialDto);
    }
}
