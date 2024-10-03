package ua.hodik.gym.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.hodik.gym.dto.TrainerDto;
import ua.hodik.gym.dto.TrainerUpdateDto;
import ua.hodik.gym.dto.UserCredentialDto;
import ua.hodik.gym.dto.UserNameDto;
import ua.hodik.gym.service.TrainerService;

@RestController
@RequestMapping("/trainers")
public class TrainerController {
    private final TrainerService trainerService;

    @Autowired
    public TrainerController(TrainerService trainerService) {
        this.trainerService = trainerService;
    }


    @PostMapping("/registration")
    public ResponseEntity<UserCredentialDto> registration(@RequestBody @Valid TrainerDto trainerDto) {
        UserCredentialDto credentialDto = trainerService.createTrainerProfile(trainerDto);
        return ResponseEntity.status(201).body(credentialDto);
    }

    @PatchMapping()
    public ResponseEntity<String> updateTrainerActivityStatus(@Valid @RequestBody UserNameDto userNameDto,
                                                              @NotBlank(message = "Can't be null or empty") boolean isActive,
                                                              HttpServletRequest request) {
        trainerService.updateActiveStatus(userNameDto.getUserName(), isActive);
        return ResponseEntity.ok(String.format("Trainer %s active status updated", userNameDto.getUserName()));
    }

    @GetMapping
    public ResponseEntity<TrainerDto> getTrainer(@Valid @RequestBody UserNameDto userNameDto, HttpServletRequest request) {
        //todo
        return ResponseEntity.ok(new TrainerDto());
    }

    @PutMapping("/{id:\\d+}")
    public ResponseEntity<TrainerDto> updateTrainer(@PathVariable int id, @Valid @RequestBody TrainerUpdateDto trainerDto) {
        TrainerDto trainer = trainerService.update(id, trainerDto);
        return ResponseEntity.ok(trainer);

    }
}
