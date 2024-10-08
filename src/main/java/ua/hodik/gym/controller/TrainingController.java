package ua.hodik.gym.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.hodik.gym.dto.TrainingDto;
import ua.hodik.gym.dto.TrainingTypeDto;
import ua.hodik.gym.dto.ValidationErrorResponse;
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

    @Operation(summary = "Add a new training")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Added a training",
                    content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid parameter",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ValidationErrorResponse.class))}),
            @ApiResponse(responseCode = "404", description = "Entity not found",
                    content = @Content)})
    @PostMapping
    public ResponseEntity<String> addTraining(@RequestBody @Valid TrainingDto trainingDto) {
        trainingService.createTraining(trainingDto);
        return ResponseEntity.ok("Training added successfully");
    }

    @Operation(summary = "Get a training type")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Got a training type",
                    content = @Content)})
    @GetMapping
    public ResponseEntity<List<TrainingTypeDto>> getTrainingType() {
        List<TrainingTypeDto> trainingTypeDtoList = trainingService.getTrainingType();
        return ResponseEntity.ok(trainingTypeDtoList);
    }
}
