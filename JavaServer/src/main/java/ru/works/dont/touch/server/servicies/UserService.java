package ru.works.dont.touch.server.servicies;

import org.springframework.stereotype.Service;
import ru.works.dont.touch.server.entities.User;
import ru.works.dont.touch.server.repositories.UserRepository;

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
        return userRepository.existsByLogin(user.getLogin());
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


}
