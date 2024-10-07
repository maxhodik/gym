package ua.hodik.gym.service.impl;


import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.hodik.gym.dto.*;
import ua.hodik.gym.exception.MyEntityNotFoundException;
import ua.hodik.gym.exception.MyValidationException;
import ua.hodik.gym.model.Trainee;
import ua.hodik.gym.model.Trainer;
import ua.hodik.gym.model.User;
import ua.hodik.gym.repository.TraineeRepository;
import ua.hodik.gym.repository.UserRepository;
import ua.hodik.gym.service.TraineeService;
import ua.hodik.gym.service.TrainerService;
import ua.hodik.gym.service.UserService;
import ua.hodik.gym.service.mapper.TraineeMapper;
import ua.hodik.gym.service.mapper.TrainerMapper;
import ua.hodik.gym.util.CredentialChecker;
import ua.hodik.gym.util.PasswordGenerator;
import ua.hodik.gym.util.UserNameGenerator;
import ua.hodik.gym.util.impl.validation.MyValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Log4j2
public class TraineeServiceImpl implements TraineeService {

    private final UserNameGenerator userNameGenerator;

    private final PasswordGenerator passwordGenerator;
    private final TraineeMapper traineeMapper;
    private final TrainerMapper trainerMapper;

    private final TraineeRepository traineeRepository;
    private final TrainerService trainerService;
    private final UserRepository userRepository;
    private final UserService userService;

    private final CredentialChecker credentialChecker;


    @Autowired
    public TraineeServiceImpl(UserNameGenerator userNameGenerator, PasswordGenerator passwordGenerator,
                              TraineeMapper traineeMapper,
                              TrainerMapper trainerMapper, TraineeRepository traineeRepository,
                              TrainerService trainerService, UserRepository userRepository, UserService userService, CredentialChecker credentialChecker, MyValidator validator) {
        this.userNameGenerator = userNameGenerator;
        this.passwordGenerator = passwordGenerator;
        this.traineeMapper = traineeMapper;
        this.trainerMapper = trainerMapper;
        this.traineeRepository = traineeRepository;
        this.trainerService = trainerService;
        this.userRepository = userRepository;
        this.userService = userService;
        this.credentialChecker = credentialChecker;
    }


    @Override
    @Transactional
    public UserCredentialDto createTraineeProfile(TraineeDto traineeDto) {
        Trainee trainee = traineeMapper.convertToTrainee(traineeDto);
        setGeneratedUserName(trainee);
        setGeneratedPassword(trainee);
        trainee.getUser().setActive(true);
        trainee = traineeRepository.save(trainee);
        UserCredentialDto credentialDto = new UserCredentialDto(trainee.getUser().getUserName(), trainee.getUser().getPassword());
        log.debug("[TraineeService] Registration trainee  username {}, TransactionId {}", credentialDto.getUserName(), MDC.get("transactionId"));
        return credentialDto;
    }

    @Transactional
    @Override
    public TraineeDto update(int id, TraineeUpdateDto traineeDto) {
        Trainee traineeToUpdate = findById(id);
        User updatedUser = userService.update(traineeToUpdate.getUser().getId(), traineeDto.getUserUpdateDto());
        traineeToUpdate.setUser(updatedUser);
        updateTrainee(traineeDto, traineeToUpdate);
        log.debug("[TraineeService] Updating  by id= {}, TransactionId {}", id, MDC.get("transactionId"));
        return traineeMapper.convertToTraineeDto(traineeToUpdate);
    }

    @Override
    @Transactional(readOnly = true)
    public Trainee findById(int id) {
        Optional<Trainee> trainee = traineeRepository.findById(id);
        return trainee.orElseThrow(() -> new MyEntityNotFoundException(String.format("Trainee id= %s not found", id)));
    }

    @Override
    public Trainee findByUserName(String userName) {
        Optional<Trainee> trainee = traineeRepository.findByUserUserName(userName);
        return trainee.orElseThrow(() -> new MyEntityNotFoundException(String.format("Trainee %s not found", userName)));
    }

    @Override
    public TraineeDto findTraineeDtoByUserName(String username) {
        log.debug("[TraineeService] Finding trainee by username {}, TransactionId {}", username, MDC.get("transactionId"));
        return traineeMapper.convertToTraineeDto(findByUserName(username));
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
    public void deleteTrainee(String userName) {
        traineeRepository.deleteByUserUserName(userName);
        log.debug("[TraineeService] Deleting trainee by username {}, TransactionId {}", userName, MDC.get("transactionId"));

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
            throw new MyValidationException("Password can't be null or empty");
        }
    }


    private void updateTrainee(TraineeUpdateDto traineeDto, Trainee traineeToUpdate) {
        traineeToUpdate.setDayOfBirth(traineeDto.getDayOfBirth());
        traineeToUpdate.setAddress(traineeDto.getAddress());

    }

    @Transactional
    @Override
    public void updateActiveStatus(String userName, boolean isActive) {
        Trainee traineeToUpdate = findByUserName(userName);
        User user = traineeToUpdate.getUser();
        user.setActive(isActive);
        log.debug("[TraineeService] Updating trainee's active status  by username {}, TransactionId {}", userName, MDC.get("transactionId"));

    }

    @Transactional
    @Override
    public List<TrainerDto> updateTrainersList(int traineeId, List<UserNameDto> trainerNameList) {

        Trainee trainee = findById(traineeId);
        List<Trainer> trainerList = new ArrayList<>();
        trainerNameList.stream()
                .map(UserNameDto::getUserName)
                .map(trainerService::findByUserName)
                .forEach(trainerList::add);
        trainee.addTrainersList(trainerList);
        log.debug("[TraineeService] Updating trainee's trainersList.Trainee id= {} , TransactionId {}", traineeId, MDC.get("transactionId"));
        return trainerList.stream()
                .map(trainerMapper::convertToTrainerDto).toList();
    }

}
