package ua.hodik.gym.service.serviceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.hodik.gym.dao.TrainerDao;
import ua.hodik.gym.model.Trainer;
import ua.hodik.gym.service.TrainerService;
import ua.hodik.gym.util.PasswordGenerator;
import ua.hodik.gym.util.UserNameGenerator;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class TrainerServiceImpl implements TrainerService {
    @Autowired
    private TrainerDao trainerDao;
    @Autowired
    private UserNameGenerator userNameGenerator;
    @Autowired
    private PasswordGenerator passwordGenerator;

    @Override
    public Optional<Trainer> create(Trainer trainer) {
        Objects.requireNonNull(trainer);

        int userId = trainerDao.getMaxId()+1;
        trainer.setUserId(userId);
        String firstName = trainer.getFirstName();
        String lastName = trainer.getLastName();
        String userName = userNameGenerator.generateUserName(firstName, lastName);
        String password = passwordGenerator.generatePassword();
        trainer.setUserName(userName);
        trainer.setPassword(password);
        return trainerDao.add(trainer);
    }

    @Override
    public Optional<Trainer> update(Trainer trainer, int id) {
        Objects.requireNonNull(trainer);
        return trainerDao.update(trainer, id);
    }

    @Override
    public boolean delete(int id) {
        return trainerDao.delete(id);
    }

    @Override
    public Optional<Trainer> findById(int id) {

        return trainerDao.getById(id);
    }

    @Override
    public List<Trainer> getAllTrainers() {

        return trainerDao.getAllTrainers();
    }
}
