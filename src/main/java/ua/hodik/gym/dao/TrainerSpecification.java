package ua.hodik.gym.dao;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import ua.hodik.gym.model.Trainee;
import ua.hodik.gym.model.Trainer;
import ua.hodik.gym.model.User;

import java.util.ArrayList;
import java.util.List;

@Component
public class TrainerSpecification {
    public Specification<Trainer> getTrainer(String traineeName) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            Join<Trainer, Trainee> traineeJoin = root.join("trainees");

            Join<Trainer, User> trainerUserJoin = traineeJoin.join("user");
//
//            Join<Trainer, Trainee> traineeJoin = trainerUserJoin.join("trainees");
            Join<Trainee, User> traineeUserJoin = traineeJoin.join("user");

            addTraineeNamePredicate(traineeName, criteriaBuilder, predicates, traineeUserJoin);

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    private void addTraineeNamePredicate(String traineeName,
                                         CriteriaBuilder criteriaBuilder, List<Predicate> predicates,
                                         Join<Trainee, User> trainerJoin) {
        if (traineeName != null) {
            Predicate traineePredicate = criteriaBuilder.equal(trainerJoin.get("userName"), traineeName);
            traineePredicate.not().in(traineePredicate);
            predicates.add(traineePredicate);
        }
    }
}
