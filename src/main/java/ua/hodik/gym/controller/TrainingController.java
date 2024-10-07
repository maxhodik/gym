package ua.hodik.gym.controller;

import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.slf4j.MDC;
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
    public static final String TRANSACTION_ID = "transactionId";
    private final TrainingService trainingService;

    public TrainingController(TrainingService trainingService) {
        this.trainingService = trainingService;
    }

    @PostMapping
    public ResponseEntity<String> addTraining(@RequestBody @Valid TrainingDto trainingDto) {
        trainingService.createTraining(trainingDto);
        log.debug("[TrainingController] Adding training. Training name {}, TransactionId {}", trainingDto.getName(), MDC.get(TRANSACTION_ID));
        return ResponseEntity.ok("Training added successfully");
    }

    @GetMapping
    public ResponseEntity<List<TrainingTypeDto>> getTrainingType() {
        List<TrainingTypeDto> trainingTypeDtoList = trainingService.getTrainingType();
        log.debug("[TrainingController] Getting trainingType. TransactionId {}", MDC.get(TRANSACTION_ID));
        return ResponseEntity.ok(trainingTypeDtoList);
    }
}
