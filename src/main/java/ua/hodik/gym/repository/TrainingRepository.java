package ua.hodik.gym.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ua.hodik.gym.model.Training;

@Repository
public interface TrainingRepository extends JpaRepository<Training, Integer> {
}
