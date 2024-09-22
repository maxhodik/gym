package ua.hodik.gym.repository;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ua.hodik.gym.model.Trainer;

import java.util.List;
import java.util.Optional;

@Repository
public interface TrainerRepository extends JpaRepository<Trainer, Integer> {
    Optional<Trainer> findByUserUserName(String userName);

    List<Trainer> findAll(Specification specification);


}


