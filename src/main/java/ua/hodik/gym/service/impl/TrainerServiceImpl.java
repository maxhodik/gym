package ua.hodik.gym.service.impl;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.hodik.gym.dao.TrainerSpecification;
import ua.hodik.gym.dto.TrainerDto;
import ua.hodik.gym.dto.UserCredentialDto;
import ua.hodik.gym.dto.mapper.TrainerMapper;
import ua.hodik.gym.exception.WrongCredentialException;
import ua.hodik.gym.model.Trainer;
import ua.hodik.gym.model.User;
import ua.hodik.gym.repository.TrainerRepository;
import ua.hodik.gym.service.TrainerService;
import ua.hodik.gym.util.PasswordGenerator;
import ua.hodik.gym.util.UserNameGenerator;

import java.util.List;
import java.util.Optional;

@Service
@Log4j2
public class TrainerServiceImpl implements TrainerService {
    private final UserNameGenerator userNameGenerator;
    private final PasswordGenerator passwordGenerator;
    private final TrainerRepository trainerRepository;
    private final TrainerMapper trainerMapper;
    private final TrainerSpecification trainerSpecification;

    @Autowired
    public TrainerServiceImpl(UserNameGenerator userNameGenerator, PasswordGenerator passwordGenerator,
                              TrainerRepository trainerRepository, TrainerMapper trainerMapper, TrainerSpecification trainerSpecification) {
        this.userNameGenerator = userNameGenerator;
        this.passwordGenerator = passwordGenerator;
        this.trainerRepository = trainerRepository;
        this.trainerMapper = trainerMapper;
        this.trainerSpecification = trainerSpecification;
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
        log.info("Trainer {} saved in DB", trainer.getUser().getUserName());
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
        User user = trainerToUpdate.getUser();
        if (!user.isActive()) {
            user.setActive(isActive);
            log.info("Trainer {} active status updated", userName);
        }
        return trainerToUpdate;
    }

    public List<Trainer> getNotAssignedTrainers(String traineeName) {
        Specification<Trainer> specification = trainerSpecification.getTrainer(traineeName);
        return trainerRepository.findAll(specification);
    }

    @Override
    public Trainer findByUserName(String trainerUserName) {
        return trainerRepository.findByUserUserName(trainerUserName).orElseThrow(() ->
                new EntityNotFoundException(String.format("Trainer %s not found", trainerUserName)));
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
