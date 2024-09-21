package ua.hodik.gym.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.hodik.gym.dto.TraineeDto;
import ua.hodik.gym.dto.UserCredentialDto;
import ua.hodik.gym.dto.mapper.TraineeMapper;
import ua.hodik.gym.dto.mapper.UserMapper;
import ua.hodik.gym.exception.EntityAlreadyExistsException;
import ua.hodik.gym.exception.ValidationException;
import ua.hodik.gym.exception.WrongCredentialException;
import ua.hodik.gym.model.Trainee;
import ua.hodik.gym.model.Trainer;
import ua.hodik.gym.model.User;
import ua.hodik.gym.repository.TraineeRepository;
import ua.hodik.gym.service.TraineeService;
import ua.hodik.gym.service.TrainerService;
import ua.hodik.gym.util.PasswordGenerator;
import ua.hodik.gym.util.UserNameGenerator;
import ua.hodik.gym.util.impl.validation.MyValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Log4j2
public class TraineeServiceImpl implements TraineeService {


    private UserNameGenerator userNameGenerator;
    private PasswordGenerator passwordGenerator;
    private final TraineeMapper traineeMapper;
    private final UserMapper userMapper;
    private final TraineeRepository traineeRepository;
    private final TrainerService trainerService;
    private final MyValidator credentialValidator;


    @Autowired
    public TraineeServiceImpl(TraineeMapper traineeMapper, UserMapper userMapper,
                              TraineeRepository traineeRepository, @Lazy TrainerService trainerService,
                              MyValidator credentialValidator) {
        this.traineeMapper = traineeMapper;
        this.userMapper = userMapper;
        this.traineeRepository = traineeRepository;
        this.trainerService = trainerService;
        this.credentialValidator = credentialValidator;
    }


    @Autowired
    public void setUserNameGenerator(UserNameGenerator userNameGenerator) {
        this.userNameGenerator = userNameGenerator;
    }

    @Autowired
    public void setPasswordGenerator(PasswordGenerator passwordGenerator) {
        this.passwordGenerator = passwordGenerator;
    }


    @Override
    public void delete(int id) {
        traineeRepository.deleteById(id);
        log.info("Deleting Trainee with id= {}", id);
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
        log.info("Finding trainee by userName", userName);
        return trainee.orElseThrow(() -> new EntityNotFoundException(String.format("Trainee  %s not found", userName)));

    }

    @Override
    public List<Trainee> getAllTrainees() {
        List<Trainee> allTrainees = traineeRepository.findAll();
        log.info("Finding all trainees from DB");
        return allTrainees;
    }


    @Override
    @Transactional
    public Trainee createTraineeProfile(TraineeDto traineeDto) {
        credentialValidator.validate(traineeDto);
        Trainee trainee = traineeMapper.convertToTrainee(traineeDto);
        setGeneratedUserName(trainee);
        setGeneratedPassword(trainee);
        trainee = traineeRepository.save(trainee);
        log.info("Trainee {} saved in DB", trainee.getUser().getUserName());
        return trainee;
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
        credentialValidator.validate(credential);

        String userName = credential.getUserName();
        Optional<Trainee> trainee = traineeRepository.findByUserUserName(userName);
        boolean result = trainee.isPresent() && trainee.get().getUser().getPassword().equals(credential.getPassword());
        log.info("User's {} credential matching is {}", userName, result);
        return result;
    }


    @Transactional()
    public Trainee changePassword(UserCredentialDto credential, String newPassword) {
        validatePassword(newPassword);
        String userName = credential.getUserName();
        isMatchCredential(credential);
        Optional<Trainee> optionalTrainee = traineeRepository.findByUserUserName(userName);
        optionalTrainee.orElseThrow().getUser().setPassword(newPassword);
        log.info("{} password updated", userName);
        return optionalTrainee.orElseThrow();
    }

    private void validatePassword(String newPassword) {
        if (newPassword == null || newPassword.isEmpty()) {
            throw new ValidationException("Password can't be null or empty");
        }
    }

    @Transactional()
    public Trainee update(UserCredentialDto credential, TraineeDto traineeDto) {
        String userName = credential.getUserName();
        isMatchCredential(credential);
        String userNameDto = traineeDto.getUserDto().getUserName();
        checkUserName(userName, userNameDto);
        Optional<Trainee> optionalTrainee = traineeRepository.findByUserUserName(userName);
        Trainee trainee = traineeMapper.convertToTrainee(traineeDto);
        Trainee traineeToUpdate = optionalTrainee.orElseThrow(() -> new EntityNotFoundException("Trainee not found"));
        getTraineeToUpdate(traineeDto, trainee, traineeToUpdate);
        log.info("{} trainee updated", userName);
        return traineeToUpdate;
    }

    private void checkUserName(String userName, String userNameDto) {
        if (!userName.equals(userNameDto)) {
            if (traineeRepository.findByUserUserName(userNameDto).isPresent()) {
                throw new EntityAlreadyExistsException(String.format("User %s already exists", userNameDto));
            }
        }
    }

    @Override
    @Transactional
    public void deleteTrainee(UserCredentialDto credential) {
        String userName = credential.getUserName();
        isMatchCredential(credential);
        traineeRepository.deleteByUserUserName(userName);
        log.info("{} trainee  deleted", userName);
    }

    private void getTraineeToUpdate(TraineeDto traineeDto, Trainee trainee, Trainee traineeToUpdate) {
        traineeToUpdate.setDayOfBirth(traineeDto.getDayOfBirth());
        traineeToUpdate.setAddress(traineeDto.getAddress());
        traineeToUpdate.getUser().setFirstName(trainee.getUser().getFirstName());
        traineeToUpdate.getUser().setLastName(trainee.getUser().getLastName());
        traineeToUpdate.getUser().setActive(trainee.getUser().isActive());
        traineeToUpdate.getUser().setUserName(trainee.getUser().getUserName());
        traineeToUpdate.getUser().setPassword(traineeToUpdate.getUser().getPassword());
    }

    @Transactional()
    public Trainee updateActiveStatus(UserCredentialDto credential, boolean isActive) {
        String userName = credential.getUserName();
        isMatchCredential(credential);
        Optional<Trainee> optionalTrainee = traineeRepository.findByUserUserName(userName);
        Trainee traineeToUpdate = optionalTrainee.orElseThrow(() -> new EntityNotFoundException("Trainee not found"));
        User user = traineeToUpdate.getUser();
        if (!user.isActive()) {
            user.setActive(isActive);
            log.info("Trainee {} active status updated", userName);
        }
        return traineeToUpdate;
    }

    @Transactional
    public void updateTrainersList(UserCredentialDto credential, List<String> trainers) {
        isMatchCredential(credential);
        String traineeUserName = credential.getUserName();
        Trainee trainee = traineeRepository.findByUserUserName(traineeUserName).orElseThrow(() ->
                new EntityNotFoundException(String.format("Trainee %S not found", traineeUserName)));
        List<Trainer> trainerList = new ArrayList<>();
        for (String trainerUserName : trainers) {
            Trainer trainer = trainerService.findByUserName(trainerUserName);
            trainerList.add(trainer);
        }
        trainee.addTrainersList(trainerList);
        log.info("Updating {} trainersList", traineeUserName);
    }


    private void isMatchCredential(UserCredentialDto credential) {
        if (!matchCredential(credential)) {
            throw new WrongCredentialException("Incorrect credentials, this operation is prohibited");
        }
    }
}
