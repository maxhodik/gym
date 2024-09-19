package ua.hodik.gym.dao;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import ua.hodik.gym.dto.FilterDto;
import ua.hodik.gym.model.Trainee;
import ua.hodik.gym.model.Trainer;
import ua.hodik.gym.model.Training;
import ua.hodik.gym.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class TrainingSpecification {


    public Specification<Training> getTraining(Map<String, FilterDto<?>> filters) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            Join<Training, Trainer> trainerJoin = root.join("trainer");
            Join<Trainer, User> trainerUserJoin = trainerJoin.join("user");
            Join<Training, Trainee> traineeJoin = root.join("trainee");
            Join<Trainee, User> traineeUserJoin = traineeJoin.join("user");

            addDatePredicate(filters, root, criteriaBuilder, predicates);
            addTraineeNamePredicate(filters, criteriaBuilder, predicates, traineeUserJoin);
            addTrainerNamePredicate(filters, criteriaBuilder, predicates, trainerUserJoin);
            addTrainingTypePredicate(filters, root, criteriaBuilder, predicates);
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    private void addTraineeNamePredicate(Map<String, FilterDto<?>> filters,
                                         CriteriaBuilder criteriaBuilder, List<Predicate> predicates,
                                         Join<Trainee, User> trainerJoin) {
        FilterDto<?> filterTrainee = filters.get("trainee");
        if (filterTrainee != null) {
            String traineeName = String.valueOf(filterTrainee.getValues().get(0));
            Predicate traineePredicate = criteriaBuilder.equal(trainerJoin.get("userName"), traineeName);
            predicates.add(traineePredicate);
        }
    }

    private static void addTrainingTypePredicate(Map<String, FilterDto<?>> filters,
                                                 Root<Training> root, CriteriaBuilder criteriaBuilder,
                                                 List<Predicate> predicates) {
        FilterDto<?> filterDto = filters.get("trainingType");
        if (filterDto != null) {
            String trainingType = filterDto.getValues().get(0).toString();
            Predicate trainingTypePredicate = criteriaBuilder.equal(root.get("trainingType"), trainingType);
            predicates.add(trainingTypePredicate);
        }
    }

    private static void addTrainerNamePredicate(Map<String, FilterDto<?>> filters,
                                                CriteriaBuilder criteriaBuilder,
                                                List<Predicate> predicates, Join<Trainer, User> trainingRoot) {
        FilterDto<?> filterDto = filters.get("trainer");
        if (filterDto != null) {
            String value = (String) filterDto.getValues().get(0);
            Predicate statusPredicate = criteriaBuilder.equal(trainingRoot.get("userName"),
                    value);
            predicates.add(statusPredicate);

        }
    }

    private static void addDatePredicate(Map<String, FilterDto<?>> filters,
                                         Root<Training> root, CriteriaBuilder criteriaBuilder, List<Predicate> predicates) {
        FilterDto<?> filterDate = filters.get("date");
        if (filterDate != null) {
            List<?> values = filterDate.getValues();

            Predicate datePredicate = criteriaBuilder.between(root.get("date"),
                    (LocalDate) values.get(0), (LocalDate) values.get(1));
            predicates.add(datePredicate);
        }
    }
}
