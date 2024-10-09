package ua.hodik.gym.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ua.hodik.gym.dto.TrainingDto;
import ua.hodik.gym.dto.TrainingTypeDto;
import ua.hodik.gym.model.Training;
import ua.hodik.gym.model.TrainingType;
import ua.hodik.gym.service.TrainingService;
import ua.hodik.gym.tets.util.TestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrainingControllerTest {
    private final String trainingDtoPath = "training.dto.json";
    private final String trainingPath = "training.with.id.json";
    private final TrainingDto trainingDto = TestUtils.readFromFile(trainingDtoPath, TrainingDto.class);
    private final Training training = TestUtils.readFromFile(trainingPath, Training.class);
    @Mock
    private TrainingService trainingService;
    @InjectMocks
    private TrainingController trainingController;

    @Test
    void addTraining_ResponseOK() {
        //given
        when(trainingService.createTraining(any(TrainingDto.class))).thenReturn(training);
        //when
        ResponseEntity<String> response = trainingController.addTraining(trainingDto);
        //then
        verify(trainingService).createTraining(trainingDto);
        assertEquals("Training added successfully", response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void getTrainingType_ReturnTrainingTypeListResponseOK() {
        //given
        TrainingType value = TrainingType.values()[1];
        TrainingTypeDto expectedTrainingTypeDto = new TrainingTypeDto(value.name(), value.getID());

        List<TrainingTypeDto> trainingTypeDtoList = List.of(expectedTrainingTypeDto);
        when(trainingService.getTrainingType()).thenReturn(trainingTypeDtoList);
        //when
        ResponseEntity<List<TrainingTypeDto>> response = trainingController.getTrainingType();
        //then
        verify(trainingService).getTrainingType();
        assertEquals(trainingTypeDtoList, response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}