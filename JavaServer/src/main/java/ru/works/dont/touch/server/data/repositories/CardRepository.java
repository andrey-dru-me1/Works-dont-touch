package ru.works.dont.touch.server.data.repositories;

import org.springframework.data.repository.CrudRepository;
import ru.works.dont.touch.server.data.entities.Card;

public interface CardRepository extends CrudRepository<Card, Integer> {
    Iterable<Card> findAll();
    Iterable<Card> findAllByOwnerId(int ownerId);
    boolean deleteAllByOwnerId(int ownerId);
}
