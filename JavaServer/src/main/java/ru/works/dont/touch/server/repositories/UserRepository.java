package ru.works.dont.touch.server.repositories;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import ru.works.dont.touch.server.entities.User;

import java.util.Optional;
import java.util.stream.Stream;


// This will be AUTO IMPLEMENTED by Spring into a Bean called userRepository
// CRUD refers Create, Read, Update, Delete


public interface UserRepository extends CrudRepository<User, Integer> {
    boolean existsByLogin(String login);

    boolean existsByLoginAndPassword(String login, byte[] password);

    boolean deleteByLogin(String login);

    User findByLoginAndPassword(String login, byte[] password);
    Optional<User> findById(Long id);

    User findByLogin(String login);

    Iterable<User> findAll();

    @Query("SELECT u from User u")
    Iterable<User> findAllStream();


    @Modifying
    @Query("update User u set u.password = ?2 where u.login = ?1")
    void changeByLogin(String login, byte[] password);

}