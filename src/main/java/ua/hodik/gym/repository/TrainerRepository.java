package ua.hodik.gym.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ua.hodik.gym.model.Trainer;

@Repository
public interface TrainerRepository extends JpaRepository<Trainer, Integer> {
}

