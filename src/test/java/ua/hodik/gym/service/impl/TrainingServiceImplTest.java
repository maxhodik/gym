package ua.hodik.gym.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;
import ua.hodik.gym.dao.TrainingSpecification;
import ua.hodik.gym.dto.FilterDto;
import ua.hodik.gym.dto.FilterFormDto;
import ua.hodik.gym.dto.TrainingDto;
import ua.hodik.gym.exception.EntityNotFoundException;
import ua.hodik.gym.exception.ValidationException;
import ua.hodik.gym.model.Trainee;
import ua.hodik.gym.model.Trainer;
import ua.hodik.gym.model.Training;
import ua.hodik.gym.repository.TrainingRepository;
import ua.hodik.gym.service.TraineeService;
import ua.hodik.gym.service.TrainerService;
import ua.hodik.gym.service.mapper.FilterDtoConverter;
import ua.hodik.gym.service.mapper.TrainingMapper;
import ua.hodik.gym.tets.util.TestUtils;
import ua.hodik.gym.util.impl.validation.MyValidator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainingServiceImplTest {


    public static final int ID = 1;
    private final String trainingPath = "training.with.id.json";
    private final String expectedTrainingPath = "expected.training.json";
    private final String expectedTraineePath = "trainee.same.user.name.json";
    private final String expectedTrainerPath = "trainer.json";
    private final String trainingDtoPath = "training.dto.json";
    private final String filterFormDtoPath = "filter.form.dto.json";

    private final Training training = TestUtils.readFromFile(trainingPath, Training.class);
    private final Training expectedTraining = TestUtils.readFromFile(expectedTrainingPath, Training.class);
    private final Trainee expectedTrainee = TestUtils.readFromFile(expectedTraineePath, Trainee.class);
    private final Trainer expectedTrainer = TestUtils.readFromFile(expectedTrainerPath, Trainer.class);
    private final TrainingDto trainingDto = TestUtils.readFromFile(trainingDtoPath, TrainingDto.class);
    private final TrainingDto invalidTrainingDto = new TrainingDto();
    private final FilterFormDto filterFormDto = TestUtils.readFromFile(filterFormDtoPath, FilterFormDto.class);
    private final List<Training> expectedTrainings = List.of(expectedTraining);
    private final Specification<Training> specification = mock(Specification.class);
    private final Map<String, FilterDto<?>> filters = new HashMap<>();

    @Mock
    private TrainingRepository trainingRepository;
    @Mock
    private TraineeService traineeService;
    @Mock
    private TrainerService trainerService;
    @Mock
    private TrainingMapper trainingMapper;
    @Mock
    private MyValidator validator;
    @Mock
    private FilterDtoConverter filterDtoConverter;
    @Mock
    private TrainingSpecification trainingSpecification;
    @InjectMocks
    private TrainingServiceImpl trainingService;

    @Test
    void findByIdShouldReturnTraining() {
        //give
        when(trainingRepository.findById(anyInt())).thenReturn(Optional.ofNullable(expectedTraining));
        //when
        Training trainingById = trainingService.findById(ID);
        //then
        verify(trainingRepository).findById(ID);
        assertEquals(expectedTraining, trainingById);
    }

    @Test
    void createShouldThrowException() {
        //given
        doThrow(new ValidationException()).when(validator).validate(null);
        //when
        ValidationException exception = assertThrows(ValidationException.class,
                () -> trainingService.createTraining(null));

    }

    @Test
    void createShouldThrowException_Invalid_TrainingDto() {
        //given
        doThrow(new ValidationException()).when(validator).validate(any());
        //when
        ValidationException exception = assertThrows(ValidationException.class,
                () -> trainingService.createTraining(invalidTrainingDto));

    }

    @Test
    void create() {
        //given
        doNothing().when(validator).validate(any(TrainingDto.class));
        when(trainingMapper.convertToTraining(any(TrainingDto.class))).thenReturn(expectedTraining);
        when(traineeService.findByUserName(anyString())).thenReturn(expectedTrainee);
        when(trainerService.findByUserName(anyString())).thenReturn(expectedTrainer);
        when(trainingRepository.save(any())).thenReturn(expectedTraining);
//        //when
        Training savedTraining = trainingService.createTraining(trainingDto);
        //then
        verify(trainingRepository).save(training);
        assertEquals(expectedTraining, savedTraining);
    }

    @Test
    void findByIdNotFoundException() {
        //give
        when(trainingRepository.findById(anyInt())).thenReturn(Optional.empty());
        //when
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> trainingService.findById(ID));
        //then
        verify(trainingRepository).findById(ID);
        assertEquals("Training with id= 1 not found", exception.getMessage());
    }

    @Test
    void findAllWithFilters() {
        //given
        doNothing().when(validator).validate(any(FilterFormDto.class));
        when(filterDtoConverter.convert(any(FilterFormDto.class))).thenReturn(filters);
        when(trainingSpecification.getTraining(filters)).thenReturn(specification);
        when(trainingRepository.findAll(specification)).thenReturn(expectedTrainings);

        //when
        List<Training> trainings = trainingService.findAllWithFilters(filterFormDto);
        //then
        assertEquals(expectedTrainings, trainings);
        verify(validator).validate(filterFormDto);
        verify(filterDtoConverter).convert(filterFormDto);
        verify(trainingSpecification).getTraining(filters);
        verify(trainingRepository).findAll(specification);
    }

    @Test
    void findAllWithFilters_shouldReturnEmptyList_whenNoTrainingsFound() {
        //given
        doNothing().when(validator).validate(any(FilterFormDto.class));
        when(filterDtoConverter.convert(any(FilterFormDto.class))).thenReturn(filters);
        when(trainingSpecification.getTraining(filters)).thenReturn(specification);
        when(trainingRepository.findAll(specification)).thenReturn(List.of());
        //when
        List<Training> actualTrainings = trainingService.findAllWithFilters(filterFormDto);
        //then
        assertTrue(actualTrainings.isEmpty());
        verify(validator).validate(filterFormDto);
        verify(filterDtoConverter).convert(filterFormDto);
        verify(trainingSpecification).getTraining(filters);
        verify(trainingRepository).findAll(specification);
    }

    @Test
    void findAllWithFilters_shouldThrowException_whenValidationFails() {

        //given
        doThrow(new ValidationException())
                .when(validator).validate(any(FilterFormDto.class));
        //when
        assertThrows(ValidationException.class, () ->
                trainingService.findAllWithFilters(filterFormDto));
        //then
        verify(validator).validate(filterFormDto);
        verifyNoMoreInteractions(filterDtoConverter, trainingSpecification, trainingRepository);
    }


}