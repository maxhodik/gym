package ua.hodik.gym.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ua.hodik.gym.model.Trainer;

import java.util.List;
import java.util.Optional;

@Repository
public interface TrainerRepository extends JpaRepository<Trainer, Integer> {
    Optional<Trainer> findByUserUserName(String userName);

    @Query(value = "SELECT t.* " +
            "FROM trainer t " +
            "JOIN `user` u1 ON t.user_id = u1.id " +
            "LEFT JOIN trainer_trainee tt ON t.id = tt.trainer_id AND tt.trainee_id = :traineeId " +
            "WHERE tt.trainee_id IS NULL " +
            "AND u1.isActive = 1",
            nativeQuery = true)
    List<Trainer> findAllNotAssignedTrainers(@Param("traineeId") int username);

    long count();
}
