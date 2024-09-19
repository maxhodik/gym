package ua.hodik.gym.dto.mapper;

import org.springframework.stereotype.Component;
import ua.hodik.gym.dto.FilterDto;
import ua.hodik.gym.dto.FilterFormDto;
import ua.hodik.gym.dto.Operation;
import ua.hodik.gym.model.TrainingType;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ConvertToFilterDto {

    public Map<String, FilterDto<?>> convert(FilterFormDto filterFormDto) {
        Map<String, FilterDto<?>> filters = new HashMap<>();
        LocalDate dateFrom = filterFormDto.getDateFrom();
        LocalDate dateTo = filterFormDto.getDateTo();
        String trainerName = filterFormDto.getTrainerName();
        String traineeName = filterFormDto.getTraineeName();

        TrainingType trainingType = filterFormDto.getTrainingType();
        if (dateFrom != null && dateTo != null) {
            filters.put("date", new FilterDto<>("Training_Date", List.of(dateFrom, dateTo), Operation.BETWEEN));
        }
        if (trainerName != null) {
            filters.put("trainer", new FilterDto<>("trainer", List.of(trainerName), Operation.IS));
        }
        if (trainerName != null) {
            filters.put("trainee", new FilterDto<>("trainee", List.of(traineeName), Operation.IS));
        }
        if (trainingType != null) {
            filters.put("trainingType", new FilterDto<>("trainingType", List.of(trainingType), Operation.IS));
        }
        return filters;
    }

}
