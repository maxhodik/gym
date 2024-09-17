package ua.hodik.gym.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.hodik.gym.dao.TrainerDao;
import ua.hodik.gym.dto.TrainerDto;
import ua.hodik.gym.dto.UserCredentialDto;
import ua.hodik.gym.dto.mapper.TrainerMapper;
import ua.hodik.gym.exception.WrongCredentialException;
import ua.hodik.gym.model.Trainer;
import ua.hodik.gym.repository.TrainerRepository;
import ua.hodik.gym.service.TrainerService;
import ua.hodik.gym.util.PasswordGenerator;
import ua.hodik.gym.util.UserNameGenerator;

import javax.validation.Valid;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@Log4j2
public class TrainerServiceImpl implements TrainerService {
    private TrainerDao trainerDao;
    private UserNameGenerator userNameGenerator;
    private PasswordGenerator passwordGenerator;
    private TrainerRepository trainerRepository;

    private TrainerMapper trainerMapper;

    @Autowired
    public void setTrainerMapper(TrainerMapper trainerMapper) {
        this.trainerMapper = trainerMapper;
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
        trainer.setId(userId);
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
        Optional<Trainer> trainer = trainerRepository.findById(id);
        log.info("Finding trainer by id={}", id);
        return trainer.orElseThrow(() -> new EntityNotFoundException(String.format("Trainer id= %s not found", id)));
    }

    @Override
    public List<Trainer> getAllTrainers() {
        List<Trainer> allTrainers = trainerRepository.findAll();
        log.info("Finding all trainers from DB");
        return allTrainers;
    }

    @Override
    @Transactional
    public Trainer createTrainerProfile(TrainerDto trainerDto) {
        Trainer trainer = trainerMapper.convertToTrainer(trainerDto);
        setGeneratedUserName(trainer);
        setGeneratedPassword(trainer);
        trainer = trainerRepository.save(trainer);
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

    public boolean matchCredential(@Valid UserCredentialDto credential) {
        Optional<Trainer> trainer = trainerRepository.findByUserUserName(credential.getUserName());
        return trainer.isPresent() && trainer.get().getUser().getPassword().equals(credential.getPassword());
    }

    @Transactional()
    public Trainer changePassword(@Valid UserCredentialDto credential, @Valid String newPassword) {
        String userName = credential.getUserName();
        isMatchCredential(credential);
        Optional<Trainer> optionalTrainer = trainerRepository.findByUserUserName(userName);
        optionalTrainer.orElseThrow().getUser().setPassword(newPassword);
        log.info("{} password updated", userName);
        return optionalTrainer.orElseThrow();
    }

    private void isMatchCredential(UserCredentialDto credential) {
        if (!matchCredential(credential)) {
            throw new WrongCredentialException("Incorrect credentials, this operation is prohibited");
        }
    }

    @Transactional()
    public Trainer update(@Valid UserCredentialDto credential, @Valid TrainerDto trainerDto) {
        String userName = credential.getUserName();
        isMatchCredential(credential);
        Optional<Trainer> optionalTrainer = trainerRepository.findByUserUserName(credential.getUserName());
        Trainer trainer = trainerMapper.convertToTrainer(trainerDto);

        Trainer trainerToUpdate = optionalTrainer.orElseThrow(() -> new EntityNotFoundException("Trainer not found"));
        getTrainerToUpdate(trainerDto, trainer, trainerToUpdate);
        log.info("{} trainer updated", userName);
        return trainerToUpdate;
    }

    @Transactional()
    public Trainer updateActiveStatus(@Valid UserCredentialDto credential, @Valid boolean isActive) {
        String userName = credential.getUserName();
        isMatchCredential(credential);
        Optional<Trainer> optionalTrainer = trainerRepository.findByUserUserName(credential.getUserName());
        Trainer trainerToUpdate = optionalTrainer.orElseThrow(() -> new EntityNotFoundException("Trainer not found"));
        trainerToUpdate.getUser().setActive(isActive);
        log.info("{} trainer updated", userName);
        return trainerToUpdate;
    }

    private void getTrainerToUpdate(TrainerDto trainerDto, Trainer trainer, Trainer trainerToUpdate) {
        trainerToUpdate.setSpecialization(trainerDto.getSpecialization());
        trainerToUpdate.getUser().setFirstName(trainer.getUser().getFirstName());
        trainerToUpdate.getUser().setLastName(trainer.getUser().getLastName());
        trainerToUpdate.getUser().setActive(trainer.getUser().isActive());
        trainerToUpdate.getUser().setUserName(trainer.getUser().getUserName());
        trainerToUpdate.getUser().setPassword(trainerToUpdate.getUser().getPassword());
    }


}
