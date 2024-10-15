package ua.hodik.gym.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
    private final TrainerService trainerService;
    private final TrainingService trainingService;
    private final TraineeService traineeService;

    @Autowired
    public TrainerController(TrainerService trainerService, TrainingService trainingService, TraineeService traineeService) {
        this.trainerService = trainerService;
        this.trainingService = trainingService;
        this.traineeService = traineeService;
    }
    @Operation(summary = "Registration a new trainer")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Registration the trainer",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserCredentialDto.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid username or password",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ValidationErrorResponse.class))})})
    @PostMapping("/registration")
    public ResponseEntity<UserCredentialDto> registration(@RequestBody @Valid TrainerDto trainerDto) {
        UserCredentialDto credentialDto = trainerService.createTrainerProfile(trainerDto);
        return ResponseEntity.status(201).body(credentialDto);
    }
    @Operation(summary = "Update a trainer active status by its username")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Update the trainer",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserCredentialDto.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid username",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ValidationErrorResponse.class))}),
            @ApiResponse(responseCode = "404", description = "Trainer not found",
                    content = @Content)})
    @PatchMapping("/{username}")
    public ResponseEntity<String> updateTrainerActivityStatus(@PathVariable @NotBlank(message = "UserName can't be null or empty") String username,
                                                              @RequestBody boolean isActive) {
        trainerService.updateActiveStatus(username, isActive);
        return ResponseEntity.ok(String.format("Trainer %s active status updated", username));
    }

    @Operation(summary = "Get trainer by its username")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Update the trainer",
                    content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid username",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ValidationErrorResponse.class))}),
            @ApiResponse(responseCode = "404", description = "Trainer not found",
                    content = @Content)})
    @GetMapping
    public ResponseEntity<TrainerDto> getTrainer(@Valid @RequestBody UserNameDto userNameDto) {
        String userName = userNameDto.getUserName();
        TrainerDto trainerDto = trainerService.findTrainerDtoByUserName(userName);
        return ResponseEntity.ok(trainerDto);
    }
    @Operation(summary = "Update a trainer by its id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Updated the trainer",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = TraineeDto.class))}),
            @ApiResponse(responseCode = "404", description = "Trainer not found",
                    content = @Content)})
    @PutMapping("/{id:\\d+}")
    public ResponseEntity<TrainerDto> updateTrainer(@PathVariable int id, @Valid @RequestBody TrainerUpdateDto trainerDto) {
        TrainerDto trainer = trainerService.update(id, trainerDto);
        return ResponseEntity.ok(trainer);

    }

    @Operation(summary = "Get not assigned active trainers by trainee username")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Get trainers list",
                    content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid username",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ValidationErrorResponse.class))}),
            @ApiResponse(responseCode = "404", description = "Entity not found",
                    content = @Content)})
    @GetMapping("/{traineeUsername}")
    public ResponseEntity<List<TrainerDto>> getNotAssignedTrainers(@PathVariable
                                                                   @NotBlank(message = "UserName can't be null or empty")
                                                                   String traineeUsername) {
        traineeService.findByUserName(traineeUsername);
        List<TrainerDto> notAssignedTrainers = trainerService.getNotAssignedTrainers(traineeUsername);
        return ResponseEntity.ok(notAssignedTrainers);
    }
    @Operation(summary = "Get training list by trainer username")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Get training list",
                    content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid username",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ValidationErrorResponse.class))}),
            @ApiResponse(responseCode = "404", description = "Entity not found",
                    content = @Content)})
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