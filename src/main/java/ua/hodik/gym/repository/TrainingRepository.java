package ua.hodik.gym.repository;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ua.hodik.gym.model.Training;

import java.util.List;

@Repository
public interface TrainingRepository extends JpaRepository<Training, Integer> {
//    @EntityGraph(value ="Trainee.withUser")

    List<Training> findAll(Specification specification);
}
