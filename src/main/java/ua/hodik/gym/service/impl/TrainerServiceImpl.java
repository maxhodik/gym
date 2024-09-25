package ua.hodik.gym.service.impl;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.hodik.gym.dao.TrainerSpecification;
import ua.hodik.gym.dto.TrainerDto;
import ua.hodik.gym.dto.UserCredentialDto;
import ua.hodik.gym.dto.UserDto;
import ua.hodik.gym.exception.EntityAlreadyExistsException;
import ua.hodik.gym.exception.EntityNotFoundException;
import ua.hodik.gym.exception.ValidationException;
import ua.hodik.gym.exception.WrongCredentialException;
import ua.hodik.gym.model.Trainer;
import ua.hodik.gym.model.User;
import ua.hodik.gym.repository.TrainerRepository;
import ua.hodik.gym.repository.UserRepository;
import ua.hodik.gym.service.TrainerService;
import ua.hodik.gym.service.mapper.TrainerMapper;
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
    private final TrainerMapper trainerMapper;
    private final TrainerSpecification trainerSpecification;
    private final MyValidator validator;

    @Autowired
    public TrainerServiceImpl(UserNameGenerator userNameGenerator, PasswordGenerator passwordGenerator,
                              TrainerRepository trainerRepository, UserRepository userRepository, TrainerMapper trainerMapper,
                              TrainerSpecification trainerSpecification, MyValidator validator) {
        this.userNameGenerator = userNameGenerator;
        this.passwordGenerator = passwordGenerator;
        this.trainerRepository = trainerRepository;
        this.userRepository = userRepository;
        this.trainerMapper = trainerMapper;
        this.trainerSpecification = trainerSpecification;
        this.validator = validator;
    }

    @Override
    @Transactional
    public Trainer createTrainerProfile(TrainerDto trainerDto) {
        Objects.requireNonNull(trainerDto, "Trainer can't be null");
        validator.validate(trainerDto);
        Trainer trainer = trainerMapper.convertToTrainer(trainerDto);
        setGeneratedUserName(trainer);
        setGeneratedPassword(trainer);
        trainer = trainerRepository.save(trainer);
        log.info("Trainer {} saved in DB", trainer.getUser().getUserName());
        return trainer;
    }

    @Transactional()
    @Override
    public Trainer update(UserCredentialDto credential, TrainerDto trainerDto) {
        isMatchCredential(credential);
        validator.validate(trainerDto);
        int trainerId = trainerDto.getTrainerId();
        String userNameFromDto = trainerDto.getUserDto().getUserName();
        Trainer trainerToUpdate = findTrainerToUpdate(trainerId);
        checkIfUserNameAllowedToChange(userNameFromDto, trainerToUpdate.getUser().getUserName());
        updateTrainer(trainerDto, trainerToUpdate);

        log.info("{} trainer updated", userNameFromDto);
        return trainerToUpdate;
    }

    @Override
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
    public List<Trainer> getAllTrainers() {
        List<Trainer> allTrainers = trainerRepository.findAll();
        log.info("Finding all trainers from DB");
        return allTrainers;
    }

    @Transactional()
    @Override
    public Trainer changePassword(UserCredentialDto credential, String newPassword) {
        validatePassword(newPassword);
        isMatchCredential(credential);
        String userName = credential.getUserName();
        Trainer trainerToUpdate = findByUserName(userName);
        trainerToUpdate.getUser().setPassword(newPassword);
        log.info("{} password updated", userName);
        return trainerToUpdate;
    }

    @Transactional()
    @Override
    public Trainer updateActiveStatus(UserCredentialDto credential, boolean isActive) {
        isMatchCredential(credential);
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
        Specification<Trainer> specification = trainerSpecification.getTrainer(traineeName);
        return trainerRepository.findAll(specification);
    }

    private Trainer findTrainerToUpdate(int trainerId) {
        if (trainerId == 0) {
            throw new EntityNotFoundException("Trainer with id = 0 cant be found");
        }
        return findById(trainerId);
    }

    private void checkIfUserNameAllowedToChange(String userNameFromDto, String userNameFromDB) {
        if (!userNameFromDto.equals(userNameFromDB)) {
            if (userRepository.findByUserName(userNameFromDto).isPresent()) {
                throw new EntityAlreadyExistsException(String.format("User %s already exists", userNameFromDto));
            }
        }
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

    public boolean matchCredential(UserCredentialDto credential) {
        Objects.requireNonNull(credential, "Credential can't be null");
        validator.validate(credential);
        Optional<Trainer> trainer = trainerRepository.findByUserUserName(credential.getUserName());
        return trainer.isPresent() && trainer.get().getUser().getPassword().equals(credential.getPassword());
    }

    private void validatePassword(String newPassword) {
        if (newPassword == null || newPassword.isEmpty()) {
            throw new ValidationException("Password can't be null or empty");
        }
    }

    private void isMatchCredential(UserCredentialDto credential) {
        if (!matchCredential(credential)) {
            throw new WrongCredentialException("Incorrect credentials, this operation is prohibited");
        }
    }

    private void updateTrainer(TrainerDto trainerDto, Trainer trainerToUpdate) {
        Optional.ofNullable(trainerDto.getSpecialization()).ifPresent(trainerToUpdate::setSpecialization);
        User user = trainerToUpdate.getUser();
        Optional.ofNullable(trainerDto.getUserDto()).map(UserDto::getFirstName).ifPresent(user::setFirstName);
        Optional.ofNullable(trainerDto.getUserDto()).map(UserDto::getLastName).ifPresent(user::setLastName);
        Optional.ofNullable(trainerDto.getUserDto()).map(UserDto::getUserName).ifPresent(user::setUserName);
        Optional.ofNullable(trainerDto.getUserDto()).map(UserDto::isActive).ifPresent(user::setActive);
        Optional.ofNullable(trainerDto.getUserDto()).map(UserDto::getPassword).ifPresent(user::setPassword);
    }
}
