package ru.works.dont.touch.server.data.servicies;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import ru.works.dont.touch.server.data.entities.User;
import ru.works.dont.touch.server.data.repositories.UserRepository;

@Service
public class UserService {
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    private final UserRepository userRepository;

    public Iterable<User> findAll() {
        return userRepository.findAll();
    }

    public boolean userExist(User user) {
        return userExist(user.getLogin());
    }
    public boolean userExist(String login, byte[] password) {
        return userRepository.existsByLoginAndPassword(login, password);
    }
    public boolean userExist(String login) {
        return userRepository.existsByLogin(login);
    }

    public boolean saveNewUser(User user) {
        if (!userExist(user)) {
            userRepository.save(user);
            return true;
        }
        return false;
    }
    public boolean saveNewUser(String login, byte[] password){
        User newUser = new User();
        newUser.setLogin(login);
        newUser.setPassword(password);
        return saveNewUser(newUser);
    }

    public boolean deleteUserByLogin(String login) {
        return userRepository.deleteByLogin(login);
    }

    public void deleteAllUsers() {
        userRepository.deleteAll();
    }

    @Transactional
    public boolean changeByLogin(String login, byte[] password){
        if (!userExist(login)){
            return false;
        }
        userRepository.changeByLogin(login, password);
        return true;
    }

    public User getUserByLogin(String login){
        if (!userExist(login)){
            return null;
        }
        return userRepository.findByLogin(login);
    }

    public byte[] getPasswordByLogin(String login){
        var out = getUserByLogin(login);
        return out == null? null : out.getPassword();
    }

}
