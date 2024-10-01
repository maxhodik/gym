package ua.hodik.gym.service.impl;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.hodik.gym.dto.TrainerDto;
import ua.hodik.gym.dto.UserCredentialDto;
import ua.hodik.gym.exception.EntityNotFoundException;
import ua.hodik.gym.exception.ValidationException;
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
        trainer = trainerRepository.save(trainer);
        User user = trainer.getUser();
        UserCredentialDto credentialDto = new UserCredentialDto(user.getUserName(), user.getPassword());
        log.info("Trainer {} saved in DB", user.getUserName());
        return credentialDto;
    }

    @Transactional
    @Override
    public Trainer update(UserCredentialDto credential, TrainerDto trainerDto) {
        credentialChecker.checkIfMatchCredentialsOrThrow(credential);
        validator.validate(trainerDto);
        int trainerId = trainerDto.getTrainerId();
        String userNameFromDto = trainerDto.getUserDto().getUserName();
        Trainer trainerToUpdate = findTrainerToUpdate(trainerId);
        User updatedUser = userService.update(trainerToUpdate.getUser().getId(), trainerDto.getUserDto());
        trainerToUpdate.setUser(updatedUser);
        updateTrainer(trainerDto, trainerToUpdate);
        log.info("{} trainer updated", userNameFromDto);
        return trainerToUpdate;
    }

    @Override
    @Transactional(readOnly = true)
    public Trainer findById(int id) {
        Optional<Trainer> trainer = trainerRepository.findById(id);
        log.info("Finding trainer by id={}", id);
        return trainer.orElseThrow(() -> new EntityNotFoundException(String.format("Trainer id= %s not found", id)));
    }

    @Override
    public Trainer findByUserName(String trainerUserName) {
        log.info("Finding trainer by userName {}", trainerUserName);
        return trainerRepository.findByUserUserName(trainerUserName).orElseThrow(() ->
                new EntityNotFoundException(String.format("Trainer %s not found", trainerUserName)));
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
    public Trainer updateActiveStatus(UserCredentialDto credential, boolean isActive) {
        credentialChecker.checkIfMatchCredentialsOrThrow(credential);
        String userName = credential.getUserName();
        Trainer trainerToUpdate = findByUserName(credential.getUserName());
        User user = trainerToUpdate.getUser();
        if (!user.isActive() == isActive) {
            user.setActive(isActive);
            log.info("Trainer {} active status updated", userName);
        }
        return trainerToUpdate;
    }

    @Override
    public List<Trainer> getNotAssignedTrainers(String traineeName) {
        return trainerRepository.findAllNotAssignedTrainers(traineeName);
    }


    private Trainer findTrainerToUpdate(int trainerId) {
        if (trainerId == 0) {
            throw new EntityNotFoundException("Trainer with id = 0 cant be found");
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
            throw new ValidationException("Password can't be null or empty");
        }
    }


    private void updateTrainer(TrainerDto trainerDto, Trainer trainerToUpdate) {
        Optional.ofNullable(trainerDto.getSpecialization()).ifPresent(trainerToUpdate::setSpecialization);
    }
}
