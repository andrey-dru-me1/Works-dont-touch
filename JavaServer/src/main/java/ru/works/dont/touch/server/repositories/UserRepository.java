package ru.works.dont.touch.server.repositories;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.works.dont.touch.server.entities.User;


// This will be AUTO IMPLEMENTED by Spring into a Bean called userRepository
// CRUD refers Create, Read, Update, Delete


public interface UserRepository extends CrudRepository<User, Integer> {
    boolean existsByLogin(String login);
    boolean existsByLoginAndPassword(String login, byte[] password);
    boolean deleteByLogin(String login);

    User findByLoginAndPassword(String login, byte[] password);

    User findByLogin(String login);

    Iterable<User> findAll();
    @Modifying
    @Query("update User u set u.password = ?2 where u.login = ?1")
    void changeByLogin(String login, byte[] password);

}