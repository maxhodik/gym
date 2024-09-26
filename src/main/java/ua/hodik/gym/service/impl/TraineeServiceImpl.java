package ua.hodik.gym.service.impl;


import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.hodik.gym.dto.TraineeDto;
import ua.hodik.gym.dto.UserCredentialDto;
import ua.hodik.gym.exception.EntityNotFoundException;
import ua.hodik.gym.exception.ValidationException;
import ua.hodik.gym.model.Trainee;
import ua.hodik.gym.model.Trainer;
import ua.hodik.gym.model.User;
import ua.hodik.gym.repository.TraineeRepository;
import ua.hodik.gym.repository.UserRepository;
import ua.hodik.gym.service.TraineeService;
import ua.hodik.gym.service.TrainerService;
import ua.hodik.gym.service.UserService;
import ua.hodik.gym.service.mapper.TraineeMapper;
import ua.hodik.gym.util.CredentialChecker;
import ua.hodik.gym.util.PasswordGenerator;
import ua.hodik.gym.util.UserNameGenerator;
import ua.hodik.gym.util.impl.validation.MyValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@Log4j2
public class TraineeServiceImpl implements TraineeService {

    private final UserNameGenerator userNameGenerator;

    private final PasswordGenerator passwordGenerator;
    private final TraineeMapper traineeMapper;

    private final TraineeRepository traineeRepository;
    private final TrainerService trainerService;
    private final UserRepository userRepository;
    private final UserService userService;

    private final CredentialChecker credentialChecker;

    private final MyValidator validator;


    @Autowired
    public TraineeServiceImpl(UserNameGenerator userNameGenerator, PasswordGenerator passwordGenerator,
                              TraineeMapper traineeMapper,
                              TraineeRepository traineeRepository,
                              TrainerService trainerService, UserRepository userRepository, UserService userService, CredentialChecker credentialChecker, MyValidator validator) {
        this.userNameGenerator = userNameGenerator;
        this.passwordGenerator = passwordGenerator;
        this.traineeMapper = traineeMapper;
        this.traineeRepository = traineeRepository;
        this.trainerService = trainerService;
        this.userRepository = userRepository;
        this.userService = userService;
        this.credentialChecker = credentialChecker;
        this.validator = validator;
    }


    @Override
    @Transactional
    public Trainee createTraineeProfile(TraineeDto traineeDto) {
        Objects.requireNonNull(traineeDto, "Trainee can't be null");
        validator.validate(traineeDto);
        Trainee trainee = traineeMapper.convertToTrainee(traineeDto);
        setGeneratedUserName(trainee);
        setGeneratedPassword(trainee);
        trainee = traineeRepository.save(trainee);
        log.info("Trainee {} saved in DB", trainee.getUser().getUserName());
        return trainee;
    }

    @Transactional
    public Trainee update(UserCredentialDto credential, TraineeDto traineeDto) {
        credentialChecker.checkIfMatchCredentialsOrThrow(credential);
        validator.validate(traineeDto);
        int traineeId = traineeDto.getTraineeId();
        String userNameFromDto = traineeDto.getUserDto().getUserName();
        Trainee traineeToUpdate = findById(traineeId);
        User updatedUser = userService.update(traineeToUpdate.getUser().getId(), traineeDto.getUserDto());
        traineeToUpdate.setUser(updatedUser);
        updateTrainee(traineeDto, traineeToUpdate);

        log.info("{} trainee updated", userNameFromDto);
        return traineeToUpdate;
    }

    @Override
    public Trainee findById(int id) {
        Optional<Trainee> trainee = traineeRepository.findById(id);
        return trainee.orElseThrow(() -> new EntityNotFoundException(String.format("Trainee id= %s not found", id)));
    }

    @Override
    public Trainee findByUserName(String userName) {
        Optional<Trainee> trainee = traineeRepository.findByUserUserName(userName);
        return trainee.orElseThrow(() -> new EntityNotFoundException(String.format("Trainee %s not found", userName)));

    }

    @Transactional
    public Trainee changePassword(UserCredentialDto credential, String newPassword) {
        validatePassword(newPassword);
        credentialChecker.checkIfMatchCredentialsOrThrow(credential);
        String userName = credential.getUserName();
        Trainee traineeForUpdate = findByUserName(userName);
        traineeForUpdate.getUser().setPassword(newPassword);
        log.info("{} password updated", userName);
        return traineeForUpdate;
    }

    @Override
    @Transactional
    public void deleteTrainee(UserCredentialDto credential) {
        credentialChecker.checkIfMatchCredentialsOrThrow(credential);
        String userName = credential.getUserName();
        traineeRepository.deleteByUserUserName(userName);
        log.info("{} trainee  deleted", userName);
    }

    @Override
    public List<Trainee> getAllTrainees() {
        return traineeRepository.findAll();
    }


    private void setGeneratedPassword(Trainee trainee) {
        String password = passwordGenerator.generatePassword();
        trainee.getUser().setPassword(password);
    }

    private void setGeneratedUserName(Trainee trainee) {
        String firstName = trainee.getUser().getFirstName();
        String lastName = trainee.getUser().getLastName();
        String userName = userNameGenerator.generateUserName(firstName, lastName);
        trainee.getUser().setUserName(userName);

    }

    private void validatePassword(String newPassword) {
        if (StringUtils.isBlank(newPassword)) {
            throw new ValidationException("Password can't be null or empty");
        }
    }


    private void updateTrainee(TraineeDto traineeDto, Trainee traineeToUpdate) {
        Optional.ofNullable(traineeDto.getDayOfBirth()).ifPresent(traineeToUpdate::setDayOfBirth);
        traineeToUpdate.setAddress(traineeDto.getAddress());
    }

    @Transactional()
    public Trainee updateActiveStatus(UserCredentialDto credential, boolean isActive) {
        credentialChecker.checkIfMatchCredentialsOrThrow(credential);
        String userName = credential.getUserName();
        Trainee traineeToUpdate = findByUserName(userName);
        User user = traineeToUpdate.getUser();
        user.setActive(isActive);
        log.info("Trainee {} active status updated", userName);
        return traineeToUpdate;
    }

    @Transactional
    public void updateTrainersList(UserCredentialDto credential, List<String> trainerNameList) {
        credentialChecker.checkIfMatchCredentialsOrThrow(credential);
        String traineeUserName = credential.getUserName();
        Trainee trainee = traineeRepository.findByUserUserName(traineeUserName).orElseThrow(() ->
                new EntityNotFoundException(String.format("Trainee %s not found", traineeUserName)));
        List<Trainer> trainerList = new ArrayList<>();
        trainerNameList.stream()
                .map(trainerService::findByUserName)
                .forEach(trainerList::add);
        trainee.addTrainersList(trainerList);
        log.info("Updating {} trainersList", traineeUserName);
    }

}
