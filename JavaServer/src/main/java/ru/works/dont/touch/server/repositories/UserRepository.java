package ru.works.dont.touch.server.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.works.dont.touch.server.entities.User;


// This will be AUTO IMPLEMENTED by Spring into a Bean called userRepository
// CRUD refers Create, Read, Update, Delete


public interface UserRepository extends CrudRepository<User, Integer> {
    boolean existsByLogin(String login);
    boolean deleteByLogin(String login);

    Iterable<User> findAll();

}