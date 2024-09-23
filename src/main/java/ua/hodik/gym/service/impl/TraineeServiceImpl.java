package ua.hodik.gym.service.impl;


import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.hodik.gym.dto.TraineeDto;
import ua.hodik.gym.dto.UserCredentialDto;
import ua.hodik.gym.dto.UserDto;
import ua.hodik.gym.dto.mapper.TraineeMapper;
import ua.hodik.gym.exception.EntityAlreadyExistsException;
import ua.hodik.gym.exception.EntityNotFoundException;
import ua.hodik.gym.exception.ValidationException;
import ua.hodik.gym.exception.WrongCredentialException;
import ua.hodik.gym.model.Trainee;
import ua.hodik.gym.model.Trainer;
import ua.hodik.gym.model.User;
import ua.hodik.gym.repository.TraineeRepository;
import ua.hodik.gym.repository.TrainerRepository;
import ua.hodik.gym.repository.UserRepository;
import ua.hodik.gym.service.TraineeService;
import ua.hodik.gym.service.TrainerService;
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
    private final TrainerRepository trainerRepository;
    private final TrainerService trainerService;
    private final UserRepository userRepository;

    private final MyValidator validator;


    @Autowired
    public TraineeServiceImpl(UserNameGenerator userNameGenerator, PasswordGenerator passwordGenerator,
                              TraineeMapper traineeMapper,
                              TraineeRepository traineeRepository, TrainerRepository trainerRepository,
                              TrainerService trainerService, UserRepository userRepository, MyValidator validator) {
        this.userNameGenerator = userNameGenerator;
        this.passwordGenerator = passwordGenerator;
        this.traineeMapper = traineeMapper;
        this.traineeRepository = traineeRepository;
        this.trainerRepository = trainerRepository;
        this.trainerService = trainerService;
        this.userRepository = userRepository;
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

    @Transactional()
    public Trainee update(UserCredentialDto credential, TraineeDto traineeDto) {
        isMatchCredential(credential);
        validator.validate(traineeDto);
        int traineeId = traineeDto.getTraineeId();
        String userNameFromDto = traineeDto.getUserDto().getUserName();
        Trainee traineeToUpdate = findTraineeToUpdate(traineeId);
        checkIfUserNameAllowedToChange(userNameFromDto, traineeToUpdate.getUser().getUserName());
        updateTrainee(traineeDto, traineeToUpdate);
        log.info("{} trainee updated", userNameFromDto);
        return traineeToUpdate;
    }

    private Trainee findTraineeToUpdate(int traineeId) {
        if (traineeId == 0) {
            throw new EntityNotFoundException("Trainee with id = 0 cant be found");
        }
        return findById(traineeId);
    }

    @Override
    public Trainee findById(int id) {
        Optional<Trainee> trainee = traineeRepository.findById(id);
        log.info("Finding trainee by id={}", id);
        return trainee.orElseThrow(() -> new EntityNotFoundException(String.format("Trainee id= %s not found", id)));
    }

    @Override
    public Trainee findByUserName(String userName) {
        Optional<Trainee> trainee = traineeRepository.findByUserUserName(userName);
        log.info("Finding trainee by {}", userName);
        return trainee.orElseThrow(() -> new EntityNotFoundException(String.format("Trainee %s not found", userName)));

    }

    @Transactional()
    public Trainee changePassword(UserCredentialDto credential, String newPassword) {
        validatePassword(newPassword);
        isMatchCredential(credential);
        String userName = credential.getUserName();
        Trainee traineeForUpdate = findByUserName(userName);
        traineeForUpdate.getUser().setPassword(newPassword);
        log.info("{} password updated", userName);
        return traineeForUpdate;
    }

    @Override
    @Transactional
    public void deleteTrainee(UserCredentialDto credential) {
        isMatchCredential(credential);
        String userName = credential.getUserName();
        traineeRepository.deleteByUserUserName(userName);
        log.info("{} trainee  deleted", userName);
    }

    @Override
    public List<Trainee> getAllTrainees() {
        List<Trainee> allTrainees = traineeRepository.findAll();
        log.info("Finding all trainees from DB");
        return allTrainees;
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

    @Transactional(readOnly = true)
    public boolean matchCredential(UserCredentialDto credential) {
        Objects.requireNonNull(credential, "Credential can't be null");
        validator.validate(credential);

        String userName = credential.getUserName();
        Optional<Trainee> trainee = traineeRepository.findByUserUserName(userName);
        boolean result = trainee.isPresent() && trainee.get().getUser().getPassword().equals(credential.getPassword());
        log.info("User's {} credential matching is {}", userName, result);
        return result;
    }


    private void validatePassword(String newPassword) {
        if (newPassword == null || newPassword.isEmpty()) {
            throw new ValidationException("Password can't be null or empty");
        }
    }

    private void checkIfUserNameAllowedToChange(String userNameFromDto, String userNameFromDB) {
        if (!userNameFromDto.equals(userNameFromDB)) {
            if (userRepository.findByUserName(userNameFromDto).isPresent()) {
                throw new EntityAlreadyExistsException(String.format("User %s already exists", userNameFromDto));
            }
        }
    }

    private void updateTrainee(TraineeDto traineeDto, Trainee traineeToUpdate) {
        Optional.ofNullable(traineeDto.getDayOfBirth()).ifPresent(traineeToUpdate::setDayOfBirth);
        traineeToUpdate.setAddress(traineeDto.getAddress());
        User user = traineeToUpdate.getUser();
        Optional.ofNullable(traineeDto.getUserDto()).map(UserDto::getFirstName).ifPresent(user::setFirstName);
        Optional.ofNullable(traineeDto.getUserDto()).map(UserDto::getLastName).ifPresent(user::setLastName);
        Optional.ofNullable(traineeDto.getUserDto()).map(UserDto::getUserName).ifPresent(user::setUserName);
        Optional.ofNullable(traineeDto.getUserDto()).map(UserDto::isActive).ifPresent(user::setActive);
        Optional.ofNullable(traineeDto.getUserDto()).map(UserDto::getPassword).ifPresent(user::setPassword);
    }

    @Transactional()
    public Trainee updateActiveStatus(UserCredentialDto credential, boolean isActive) {
        isMatchCredential(credential);
        String userName = credential.getUserName();
        Trainee traineeToUpdate = traineeRepository.findByUserUserName(userName)
                .orElseThrow(() -> new EntityNotFoundException("Trainee not found"));
        User user = traineeToUpdate.getUser();
        if (!user.isActive() == isActive) {
            user.setActive(isActive);
            log.info("Trainee {} active status updated", userName);
        }
        return traineeToUpdate;
    }

    @Transactional
    public void updateTrainersList(UserCredentialDto credential, List<String> trainerNameList) {
        isMatchCredential(credential);
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


    private void isMatchCredential(UserCredentialDto credential) {
        if (!matchCredential(credential)) {
            throw new WrongCredentialException("Incorrect credentials, this operation is prohibited");
        }
    }
}
