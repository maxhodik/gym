package ua.hodik.gym.controller;

import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.hodik.gym.dto.TrainingDto;
import ua.hodik.gym.dto.TrainingTypeDto;
import ua.hodik.gym.service.TrainingService;

import java.util.List;

@Log4j2
@RestController
@RequestMapping("/trainings")
public class TrainingController {
    private final TrainingService trainingService;

    public TrainingController(TrainingService trainingService) {
        this.trainingService = trainingService;
    }

    @PostMapping
    public ResponseEntity<String> addTraining(@RequestBody @Valid TrainingDto trainingDto) {
        trainingService.createTraining(trainingDto);
        log.info("Adding training");
        return ResponseEntity.ok("Training added successfully");
    }

    @GetMapping

    public ResponseEntity<List<TrainingTypeDto>> getTrainingType() {
        List<TrainingTypeDto> trainingTypeDtoList = trainingService.getTrainingType();
        return ResponseEntity.ok(trainingTypeDtoList);
    }
}
