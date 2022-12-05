package ru.works.dont.touch.server.servicies;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import ru.works.dont.touch.server.entities.User;
import ru.works.dont.touch.server.exceptions.ExistsException;
import ru.works.dont.touch.server.exceptions.NotExistsException;
import ru.works.dont.touch.server.repositories.UserRepository;

import java.util.WeakHashMap;

@Service
public class UserService {
    private final WeakHashMap<String, User> loginCacheMap;
    private final WeakHashMap<Long, User> idCacheMap;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
        loginCacheMap = new WeakHashMap<>();
        idCacheMap = new WeakHashMap<>();
    }

    private final UserRepository userRepository;

    public User findUserByID(Long id) throws NotExistsException {
        if (idCacheMap.containsKey(id)) {
            return idCacheMap.get(id);
        }
        var user = userRepository.findById(id);
        if (user.isEmpty()) {
            throw new NotExistsException("User not exists with id: " + id);
        }

        return user.get();
    }

    public Iterable<User> findAll() {
        return userRepository.findAll();
    }

    public boolean userExist(User user) {
        return idCacheMap.containsKey(user.getId())
                || userExist(user.getLogin(), user.getPassword());
    }

    public boolean userExist(String login, byte[] password) {
        return loginCacheMap.containsKey(login)
                || userRepository.existsByLoginAndPassword(login, password);
    }

    public boolean userExistByLogin(String login) {
        return loginCacheMap.containsKey(login)
                || userRepository.existsByLogin(login);
    }

    @Transactional
    public User saveNewUser(User user) throws ExistsException {
        if (userExistByLogin(user.getLogin())) {
            throw new ExistsException("User already exists: " + user.getLogin());
        }
        var newUser = userRepository.save(user);
        String login = new String(newUser.getLogin());
        Long id = newUser.getId().longValue();
        loginCacheMap.put(login, newUser);
        idCacheMap.put(id, newUser);
        login = null;
        id = null;
        return newUser;
    }

    @Transactional
    public User saveNewUser(String login, byte[] password) throws ExistsException {
        if (userExistByLogin(login)) {
            throw new ExistsException("User exists by this login: " + login);
        }

        User newUser = new User();
        newUser.setLogin(login);
        newUser.setPassword(password);
        return saveNewUser(newUser);
    }

    @Transactional
    public boolean deleteUserByLogin(String login) {
        try {
            idCacheMap.remove(getUserByLogin(login).getId());
        } catch (NotExistsException ignored) {

        }
        loginCacheMap.remove(login);
        return userRepository.deleteByLogin(login);
    }

    @Transactional
    public void deleteAllUsers() {
        idCacheMap.clear();
        loginCacheMap.clear();
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
        if (loginCacheMap.containsKey(login)) {
            return loginCacheMap.get(login);
        }
        return userRepository.findByLogin(login).get();
    }

    public byte[] getPasswordByLogin(String login) throws NotExistsException {
        return getUserByLogin(login).getPassword();
    }

}
