package ru.works.dont.touch.server.servicies;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import ru.works.dont.touch.server.entities.User;
import ru.works.dont.touch.server.exceptions.ExistsException;
import ru.works.dont.touch.server.exceptions.NotExistsException;
import ru.works.dont.touch.server.repositories.UserRepository;

@Service
public class UserService {
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    private final UserRepository userRepository;
    public User findUserByID(Long id) throws NotExistsException {
        var user = userRepository.findById(id);
        if (user.isEmpty()){
            throw new NotExistsException("User not exists with id: " + id);
        }

        return user.get();
    }

    public Iterable<User> findAll() {
        return userRepository.findAll();
    }

    public boolean userExist(User user) {
        return userExist(user.getLogin(), user.getPassword());
    }

    public boolean userExist(String login, byte[] password) {
        return userRepository.existsByLoginAndPassword(login, password);
    }

    public boolean userExistByLogin(String login) {
        return userRepository.existsByLogin(login);
    }

    @Transactional
    public User saveNewUser(User user) throws ExistsException {
        if (userExist(user)) {
            throw new ExistsException("User already exists: " + user);
        }
        return userRepository.save(user);
    }

    @Transactional
    public User saveNewUser(String login, byte[] password) throws ExistsException {
        if (userRepository.existsByLogin(login)) {
            throw new ExistsException("User exists by this login: " + login);
        }

        User newUser = new User();
        newUser.setLogin(login);
        newUser.setPassword(password);
        return saveNewUser(newUser);
    }

    @Transactional
    public boolean deleteUserByLogin(String login) {
        return userRepository.deleteByLogin(login);
    }

    @Transactional
    public void deleteAllUsers() {
        userRepository.deleteAll();
    }

    @Transactional
    public void changeByLogin(String login, byte[] password) throws NotExistsException {
        if (!userExistByLogin(login)) {
            throw new NotExistsException("Not exists: " + login);
        }
        userRepository.changeByLogin(login, password);

    }

    public User getUserByLogin(String login) throws NotExistsException {
        if (!userExistByLogin(login)) {
            throw new NotExistsException("User by this login" +
                    "doesnt exist: " + login);
        }
        return userRepository.findByLogin(login);
    }

    public byte[] getPasswordByLogin(String login) throws NotExistsException {
        return getUserByLogin(login).getPassword();
    }

}
