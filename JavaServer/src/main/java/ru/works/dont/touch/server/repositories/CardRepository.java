package ru.works.dont.touch.server.repositories;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import ru.works.dont.touch.server.entities.Card;

public interface CardRepository extends CrudRepository<Card, Integer> {
    Iterable<Card> findAll();
    Iterable<Card> findAllByOwnerId(Long ownerId);
    boolean deleteAllByOwnerId(Long ownerId);


    @Query("SELECT card from Card card " +
            "left join User user " +
            "on card.ownerId = user.id " +
            "where user.login = ?1")
    Card findByUserLogin(String userLogin);

    @Override
    <S extends Card> S save(S entity);

    boolean existsById(Long id);

    Card getCardById(Long id);

    @Query("update Card card set card.name=?2," +
            "card.barcode = ?3," +
            "card.ownerId = ?4" +
            " where card.id = ?1")
    void cardUpdate(Long id, String name,
                    String barcode, Long ownerId);
}
