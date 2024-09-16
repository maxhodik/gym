package ua.hodik.gym.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ua.hodik.gym.model.Trainee;

import java.util.Optional;

@Repository
public interface TraineeRepository extends JpaRepository<Trainee, Integer> {
    Optional<Trainee> findByUserUserName(String userName);
}

