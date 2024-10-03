package ua.hodik.gym.service.impl;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.hodik.gym.dto.TrainerDto;
import ua.hodik.gym.dto.TrainerUpdateDto;
import ua.hodik.gym.dto.UserCredentialDto;
import ua.hodik.gym.exception.MyEntityNotFoundException;
import ua.hodik.gym.exception.MyValidationException;
import ua.hodik.gym.model.Trainer;
import ua.hodik.gym.model.User;
import ua.hodik.gym.repository.TrainerRepository;
import ua.hodik.gym.repository.UserRepository;
import ua.hodik.gym.service.TrainerService;
import ua.hodik.gym.service.UserService;
import ua.hodik.gym.service.mapper.TrainerMapper;
import ua.hodik.gym.util.CredentialChecker;
import ua.hodik.gym.util.PasswordGenerator;
import ua.hodik.gym.util.UserNameGenerator;
import ua.hodik.gym.util.impl.validation.MyValidator;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@Log4j2
public class TrainerServiceImpl implements TrainerService {
    private final UserNameGenerator userNameGenerator;
    private final PasswordGenerator passwordGenerator;
    private final TrainerRepository trainerRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final TrainerMapper trainerMapper;
    private final CredentialChecker credentialChecker;
    private final MyValidator validator;


    @Autowired
    public TrainerServiceImpl(UserNameGenerator userNameGenerator, PasswordGenerator passwordGenerator,
                              TrainerRepository trainerRepository, UserRepository userRepository, UserService userService, TrainerMapper trainerMapper,
                              CredentialChecker credentialChecker, MyValidator validator) {
        this.userNameGenerator = userNameGenerator;
        this.passwordGenerator = passwordGenerator;
        this.trainerRepository = trainerRepository;
        this.userRepository = userRepository;
        this.userService = userService;
        this.trainerMapper = trainerMapper;
        this.credentialChecker = credentialChecker;
        this.validator = validator;
    }

    @Override
    @Transactional
    public UserCredentialDto createTrainerProfile(TrainerDto trainerDto) {
        Objects.requireNonNull(trainerDto, "Trainer can't be null");
        validator.validate(trainerDto);
        Trainer trainer = trainerMapper.convertToTrainer(trainerDto);
        setGeneratedUserName(trainer);
        setGeneratedPassword(trainer);
        trainer.getUser().setActive(true);
        trainer = trainerRepository.save(trainer);
        User user = trainer.getUser();
        UserCredentialDto credentialDto = new UserCredentialDto(user.getUserName(), user.getPassword());
        log.info("Trainer {} saved in DB", user.getUserName());
        return credentialDto;
    }

    @Transactional
    @Override
    public TrainerDto update(int trainerId, TrainerUpdateDto trainerDto) {
        Trainer trainerToUpdate = findTrainerToUpdate(trainerId);
        User updatedUser = userService.update(trainerToUpdate.getUser().getId(), trainerDto.getUserUpdateDto());
        trainerToUpdate.setUser(updatedUser);
        updateTrainer(trainerDto, trainerToUpdate);
        log.info("Trainer id={} updated", trainerId);
        return trainerMapper.convertToTrainerDto(trainerToUpdate);
    }

    @Override
    @Transactional(readOnly = true)
    public Trainer findById(int id) {
        Optional<Trainer> trainer = trainerRepository.findById(id);
        log.info("Finding trainer by id={}", id);
        return trainer.orElseThrow(() -> new MyEntityNotFoundException(String.format("Trainer id= %s not found", id)));
    }

    @Override
    public Trainer findByUserName(String trainerUserName) {
        log.info("Finding trainer by userName {}", trainerUserName);
        return trainerRepository.findByUserUserName(trainerUserName).orElseThrow(() ->
                new MyEntityNotFoundException(String.format("Trainer %s not found", trainerUserName)));
    }


    @Override
    @Transactional(readOnly = true)
    public List<Trainer> getAllTrainers() {
        List<Trainer> allTrainers = trainerRepository.findAll();
        log.info("Finding all trainers from DB");
        return allTrainers;
    }

    @Transactional
    @Override
    public Trainer changePassword(UserCredentialDto credential, String newPassword) {
        validatePassword(newPassword);
        credentialChecker.checkIfMatchCredentialsOrThrow(credential);
        String userName = credential.getUserName();
        Trainer trainerToUpdate = findByUserName(userName);
        trainerToUpdate.getUser().setPassword(newPassword);
        log.info("{} password updated", userName);
        return trainerToUpdate;
    }

    @Transactional
    @Override
    public void updateActiveStatus(String userName, boolean isActive) {
        Trainer trainerToUpdate = findByUserName(userName);
        User user = trainerToUpdate.getUser();
        if (!user.isActive() == isActive) {
            user.setActive(isActive);
            log.info("Trainer {} active status updated", userName);
        }
    }

    @Override
    public List<Trainer> getNotAssignedTrainers(String traineeName) {
        return trainerRepository.findAllNotAssignedTrainers(traineeName);
    }


    private Trainer findTrainerToUpdate(int trainerId) {
        if (trainerId == 0) {
            throw new MyEntityNotFoundException("Trainer with id = 0 cant be found");
        }
        return findById(trainerId);
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


    private void validatePassword(String newPassword) {
        if (newPassword == null || newPassword.isEmpty()) {
            throw new MyValidationException("Password can't be null or empty");
        }
    }


    private void updateTrainer(TrainerUpdateDto trainerDto, Trainer trainerToUpdate) {
        Optional.ofNullable(trainerDto.getSpecialization()).ifPresent(trainerToUpdate::setSpecialization);
    }
}
