package ua.hodik.gym.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.hodik.gym.dto.TrainingTypeDto;
import ua.hodik.gym.service.TrainingService;

import java.util.List;

@RestController
@RequestMapping("/trainings")
public class TrainingController {
    private final TrainingService trainingService;

    public TrainingController(TrainingService trainingService) {
        this.trainingService = trainingService;
    }


    @GetMapping
    public ResponseEntity<List<TrainingTypeDto>> getTrainingType() {
        List<TrainingTypeDto> trainingTypeDtoList = trainingService.getTrainingType();
        return ResponseEntity.ok(trainingTypeDtoList);
    }
}
