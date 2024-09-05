package ua.hodik.gym.service.serviceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.hodik.gym.dao.TraineeDao;
import ua.hodik.gym.model.Trainee;
import ua.hodik.gym.service.TraineeService;
import ua.hodik.gym.util.PasswordGenerator;
import ua.hodik.gym.util.UserNameGenerator;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class TraineeServiceImpl implements TraineeService {
    @Autowired
    private TraineeDao traineeDao;
    @Autowired
    private UserNameGenerator userNameGenerator;
    @Autowired
    private PasswordGenerator passwordGenerator;

    @Override
    public Optional<Trainee> create(Trainee trainee) {
        Objects.requireNonNull(trainee);

        int userId = traineeDao.getMaxId()+1;
        trainee.setUserId(userId);
        String firstName = trainee.getFirstName();
        String lastName = trainee.getLastName();
        String userName = userNameGenerator.generateUserName(firstName, lastName);
        String password = passwordGenerator.generatePassword();
        trainee.setUserName(userName);
        trainee.setPassword(password);
        return traineeDao.add(trainee);
    }

//    private int findNextId() {
//        return traineeDB.keySet().size();
//    }

    @Override
    public Optional<Trainee> update(Trainee trainee, int id) {
        Objects.requireNonNull(trainee);
        return traineeDao.update(trainee, id);
    }

    @Override
    public boolean delete(int id) {
        return traineeDao.delete(id);
    }

    @Override
    public Optional<Trainee> findById(int id) {
        return traineeDao.getById(id);
    }

    @Override
    public List<Trainee> getAllTrainees() {
        return traineeDao.getAllTrainees();
    }
}
