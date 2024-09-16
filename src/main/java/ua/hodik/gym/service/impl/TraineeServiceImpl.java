package ua.hodik.gym.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.hodik.gym.dao.TraineeDao;
import ua.hodik.gym.dto.TraineeDto;
import ua.hodik.gym.dto.UserCredentialDto;
import ua.hodik.gym.dto.mapper.TraineeMapper;
import ua.hodik.gym.dto.mapper.UserMapper;
import ua.hodik.gym.exception.WrongCredentialException;
import ua.hodik.gym.model.Trainee;
import ua.hodik.gym.repository.TraineeRepository;
import ua.hodik.gym.service.TraineeService;
import ua.hodik.gym.util.PasswordGenerator;
import ua.hodik.gym.util.UserNameGenerator;

import javax.validation.Valid;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@Log4j2
public class TraineeServiceImpl implements TraineeService {

    private TraineeDao traineeDao;
    private UserNameGenerator userNameGenerator;
    private PasswordGenerator passwordGenerator;
    private final TraineeMapper traineeMapper;
    private final UserMapper userMapper;
    private final TraineeRepository traineeRepository;


    @Autowired
    public TraineeServiceImpl(TraineeMapper traineeMapper, UserMapper userMapper, TraineeRepository traineeRepository) {
        this.traineeMapper = traineeMapper;
        this.userMapper = userMapper;
        this.traineeRepository = traineeRepository;
    }

    @Autowired
    public void setTraineeDao(TraineeDao traineeDao) {
        this.traineeDao = traineeDao;
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
    public Trainee create(Trainee trainee) {
        Objects.requireNonNull(trainee, "Trainee can't be null");
        int userId = traineeDao.getMaxId() + 1;
        trainee.setTraineeId(userId);
        String firstName = trainee.getUser().getFirstName();
        String lastName = trainee.getUser().getLastName();
        String userName = userNameGenerator.generateUserName(firstName, lastName);
        String password = passwordGenerator.generatePassword();
        trainee.getUser().setUserName(userName);
        trainee.getUser().setPassword(password);
        Trainee addedTrainee = traineeDao.add(trainee);
        log.info("Trainee {} added successfully", userName);
        return addedTrainee;
    }


    @Override
    public Trainee update(Trainee trainee, int id) {
        Objects.requireNonNull(trainee, "Trainee can't be null");
        trainee.setTraineeId(id);
        Trainee updatedTrainee = traineeDao.update(trainee, id);
        log.info("Trainee with id ={} updated", id);
        return updatedTrainee;
    }

    @Override
    public boolean delete(int id) {
        boolean delete = traineeDao.delete(id);
        log.info("Deleting Trainee with id= {}", id);
        return delete;
    }

    @Override
    public Trainee findById(int id) {
        Trainee traineeById = traineeDao.getById(id);
        log.info("Finding trainee by id={}", id);
        return traineeById;
    }

    @Override
    public List<Trainee> getAllTrainees() {
        List<Trainee> allTrainees = traineeRepository.findAll();
        log.info("Finding all trainees from DB");
        return allTrainees;
    }


    @Override
    public Trainee createTraineeProfile(@Valid TraineeDto traineeDto) {
        Trainee trainee = traineeMapper.convertToTrainee(traineeDto);
        setGeneratedUserName(trainee);
        setGeneratedPassword(trainee);
        trainee = traineeRepository.saveAndFlush(trainee);
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
    public boolean matchCredential(@Valid UserCredentialDto credential) {
        String userName = credential.getUserName();
        Optional<Trainee> trainee = traineeRepository.findByUserUserName(userName);
        boolean result = trainee.isPresent() && trainee.get().getUser().getPassword().equals(credential.getPassword());
        log.info("User's {} credential matching is {}", userName, result);
        return result;
    }

    @Transactional()
    public Trainee changePassword(@Valid UserCredentialDto credential, @Valid String newPassword) {
        String userName = credential.getUserName();
        isMatchCredential(credential);
        Optional<Trainee> optionalTrainee = traineeRepository.findByUserUserName(userName);
        optionalTrainee.orElseThrow().getUser().setPassword(newPassword);
        log.info("{} password updated", userName);
        return optionalTrainee.orElseThrow();
    }

    @Transactional()
    public Trainee update(@Valid UserCredentialDto credential, @Valid TraineeDto traineeDto) {
        String userName = credential.getUserName();
        isMatchCredential(credential);
        Optional<Trainee> optionalTrainee = traineeRepository.findByUserUserName(userName);
        Trainee trainee = traineeMapper.convertToTrainee(traineeDto);
        setGeneratedUserName(trainee);
        Trainee traineeToUpdate = optionalTrainee.orElseThrow(() -> new EntityNotFoundException("Trainee not found"));
        getTraineeToUpdate(traineeDto, trainee, traineeToUpdate);
        log.info("{} trainee updated", userName);
        return traineeToUpdate;
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
        traineeToUpdate.getUser().setUserName(trainee.getUser().getUserName());
        traineeToUpdate.getUser().setPassword(traineeToUpdate.getUser().getPassword());
    }

    @Transactional()
    public Trainee updateActiveStatus(@Valid UserCredentialDto credential, @Valid boolean isActive) {
        String userName = credential.getUserName();
        isMatchCredential(credential);
        Optional<Trainee> optionalTrainee = traineeRepository.findByUserUserName(userName);
        Trainee traineeToUpdate = optionalTrainee.orElseThrow(() -> new EntityNotFoundException("Trainee not found"));
        traineeToUpdate.getUser().setActive(isActive);
        log.info("{} trainee active status updated", userName);
        return traineeToUpdate;
    }

    private void isMatchCredential(UserCredentialDto credential) {
        if (!matchCredential(credential)) {
            throw new WrongCredentialException("Incorrect credentials, this operation is prohibited");
        }
    }
}
