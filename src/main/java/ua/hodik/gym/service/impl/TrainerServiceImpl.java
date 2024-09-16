package ua.hodik.gym.service.impl;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.hodik.gym.dao.TrainerDao;
import ua.hodik.gym.dto.TrainerDto;
import ua.hodik.gym.dto.mapper.TrainerMapper;
import ua.hodik.gym.model.Trainer;
import ua.hodik.gym.repository.TrainerRepository;
import ua.hodik.gym.service.TrainerService;
import ua.hodik.gym.util.PasswordGenerator;
import ua.hodik.gym.util.UserNameGenerator;

import java.util.List;
import java.util.Objects;

@Service
@Log4j2
public class TrainerServiceImpl implements TrainerService {
    private TrainerDao trainerDao;
    private UserNameGenerator userNameGenerator;
    private PasswordGenerator passwordGenerator;
    private TrainerRepository trainerRepository;

    private TrainerMapper trainerMapper;

    @Autowired
    public TrainerMapper getTrainerMapper() {
        return trainerMapper;
    }

    @Autowired
    public void setTrainerDao(TrainerDao trainerDao) {
        this.trainerDao = trainerDao;
    }

    @Autowired
    public void setUserNameGenerator(UserNameGenerator userNameGenerator) {
        this.userNameGenerator = userNameGenerator;
    }

    @Autowired
    public void setPasswordGenerator(PasswordGenerator passwordGenerator) {
        this.passwordGenerator = passwordGenerator;
    }

    @Autowired
    public void setTrainerRepository(TrainerRepository trainerRepository) {
        this.trainerRepository = trainerRepository;
    }

    @Override
    public Trainer create(Trainer trainer) {
        Objects.requireNonNull(trainer, "Trainer can't be null");

        int userId = trainerDao.getMaxId() + 1;
        trainer.setTrainerId(userId);
        String firstName = trainer.getUser().getFirstName();
        String lastName = trainer.getUser().getLastName();
        String userName = userNameGenerator.generateUserName(firstName, lastName);
        String password = passwordGenerator.generatePassword();
        trainer.getUser().setUserName(userName);
        trainer.getUser().setPassword(password);
        Trainer addedTrainer = trainerDao.add(trainer);
        log.info("Trainer {} added successfully", userName);
        return addedTrainer;
    }

    @Override
    public Trainer update(Trainer trainer, int id) {
        Objects.requireNonNull(trainer, "Trainer can't be null");
        Trainer updatedTrainer = trainerDao.update(trainer, id);
        log.info("Trainer with id= {} updated successfully", id);
        return updatedTrainer;
    }

    @Override
    public boolean delete(int id) {

        boolean delete = trainerDao.delete(id);
        log.info("Deleting Trainer with id= {}", id);
        return delete;
    }

    @Override
    public Trainer findById(int id) {
        Trainer trainerById = trainerDao.getById(id);
        log.info("Finding trainer by id={}", id);
        return trainerById;
    }

    @Override
    public List<Trainer> getAllTrainers() {
        List<Trainer> allTrainers = trainerDao.getAllTrainers();
        log.info("Finding all trainers");
        return allTrainers;
    }

    @Override
    public Trainer createTrainerProfile(TrainerDto trainerDto) {
        Trainer trainer = trainerMapper.convertToTrainer(trainerDto);
        setGeneratedUserName(trainer);
        setGeneratedPassword(trainer);
        trainer = trainerRepository.saveAndFlush(trainer);
        log.info("Trainee {} saved in DB", trainer.getUser().getUserName());
        return trainer;
    }

    private void setGeneratedPassword(Trainer trainer) {
        String password = passwordGenerator.generatePassword();
        trainer.getUser().setPassword(password);
    }

    private void setGeneratedUserName(Trainer trainer) {
        String firstName = trainer.getUser().getFirstName();
        String lastName = trainer.getUser().getLastName();
        String userName = userNameGenerator.generateUserName(firstName, lastName);
        trainer.getUser().setUserName(userName);
    }
}
