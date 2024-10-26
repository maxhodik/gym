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
import ua.hodik.gym.dto.*;
import ua.hodik.gym.service.TraineeService;
import ua.hodik.gym.service.TrainingService;

import java.util.List;

@RestController
@RequestMapping("/trainees")
@Log4j2
public class TraineeController {
    private final TraineeService traineeService;
    private final TrainingService trainingService;

    public TraineeController(TraineeService traineeService, TrainingService trainingService) {
        this.traineeService = traineeService;
        this.trainingService = trainingService;
    }

    @Operation(summary = "Registration a new trainee")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Registration the trainee",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserCredentialDto.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid username or password",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ValidationErrorResponse.class))})})
    @PostMapping("/registration")
    public ResponseEntity<UserCredentialDto> registration(@Valid @RequestBody TraineeDto traineeDto) {
        UserCredentialDto userCredentialDto = traineeService.createTraineeProfile(traineeDto);
        return ResponseEntity.status(201).body(userCredentialDto);
    }


    @Operation(summary = "Get a trainee by its username")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the trainee",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = TraineeDto.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid username",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ValidationErrorResponse.class))}),
            @ApiResponse(responseCode = "404", description = "Trainee not found",
                    content = @Content)})
    @GetMapping("/{userName}")
    public ResponseEntity<TraineeDto> getTrainee(@Valid @PathVariable UserNameDto userName) {
        TraineeDto traineeDto = traineeService.findTraineeDtoByUserName(userName.getUserName());
        return ResponseEntity.ok(traineeDto);
    }

    @Operation(summary = "Update a trainee by its id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Updated the trainee",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = TraineeDto.class))}),
            @ApiResponse(responseCode = "404", description = "Trainee not found",
                    content = @Content)})
    @PutMapping("/{id:\\d+}")
    public ResponseEntity<TraineeDto> updateTrainee(@PathVariable int id,
                                                    @Valid @RequestBody TraineeDto traineeDto) {
        TraineeDto updatedTrainee = traineeService.update(id, traineeDto);
        return ResponseEntity.ok(updatedTrainee);
    }

    @Operation(summary = "Delete a trainee by its username")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Delete the trainee",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Trainee not found",
                    content = @Content)})
    @DeleteMapping
    public ResponseEntity<String> deleteTrainee(@Valid @RequestBody UserNameDto userNameDto) {
        traineeService.deleteTrainee(userNameDto.getUserName());
        return ResponseEntity.ok(String.format("Trainee %s deleted successfully", userNameDto.getUserName()));
    }

    @Operation(summary = "Update a trainee active status by its username")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Update the trainee",
                    content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid username",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ValidationErrorResponse.class))}),
            @ApiResponse(responseCode = "404", description = "Trainee not found",
                    content = @Content)})
    @PatchMapping("/{usernameDto}")
    public ResponseEntity<String> updateTraineeActivityStatus(@PathVariable @Valid UserNameDto usernameDto,
                                                              @RequestBody Boolean isActive) {
        {
            String username = usernameDto.getUserName();
            traineeService.updateActiveStatus(username, isActive);
            return ResponseEntity.ok(String.format("Trainee %s active status updated", username));
        }
    }

    @Operation(summary = "Update a trainee's trainer list by trainee id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Update the trainee",
                    content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid username",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ValidationErrorResponse.class))}),
            @ApiResponse(responseCode = "404", description = "Entity not found",
                    content = @Content)})
    @PutMapping("/update-trainers/{id:\\d+}")
    public ResponseEntity<List<TrainerDto>> updateTraineeTrainersList(@PathVariable int id,
                                                                      @RequestBody @Valid List<UserNameDto> trainerNames) {
        List<TrainerDto> trainerDtoList = traineeService.updateTrainersList(id, trainerNames);
        return ResponseEntity.ok(trainerDtoList);
    }

    @Operation(summary = "Get a trainee's training list by trainee username")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the training list",
                    content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid username",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ValidationErrorResponse.class))}),
            @ApiResponse(responseCode = "404", description = "Entity not found",
                    content = @Content)})
    @GetMapping("/training-list/{usernameDto}")
    public ResponseEntity<List<TrainingDto>> getTraineeTrainingList(@PathVariable @Valid UserNameDto usernameDto,
                                                                    @RequestBody @Valid FilterFormDto filterFormDto) {
        String userName = usernameDto.getUserName();
        traineeService.findByUserName(userName);
        filterFormDto.setTraineeName(userName);
        List<TrainingDto> allWithFilters = trainingService.findAllWithFilters(filterFormDto);
        return ResponseEntity.ok(allWithFilters);
    }
}



