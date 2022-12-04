package ru.works.dont.touch.server.repositories;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import ru.works.dont.touch.server.entities.Coordinate;
import ru.works.dont.touch.server.entities.Image;

import java.util.Optional;
import java.util.stream.Stream;

public interface CoordinateRepository extends CrudRepository<Coordinate, Integer> {
    Iterable<Coordinate> findAll();

    Iterable<Coordinate> findAllByLocationId(Long locationId);

    Optional<Coordinate> findById(Long id);

    @Query("SELECT crd " +
            "FROM Coordinate crd " +
            "left join Location loc " +
            "on crd.locationId = loc.id " +
            "left join Card card " +
            "on loc.cardId = card.id " +
            "where card.id = :cardId")
    Iterable<Coordinate> findByCardId(@Param("cardId") Long cardId);

    @Modifying
    @Query("update Coordinate coord " +
            "set coord.latitude = :latitude," +
            "coord.locationId = :locationId," +
            "coord.latitude = :longitude " +
            "where coord.id = :coordId")
    void updateById(@Param("coordId") long coordId,
                    @Param("locationId") long locationId,
                    @Param("latitude") double latitude,
                    @Param("longitude") double longitude);


    void deleteById(Long id);

    void deleteAllByLocationId(Long locationId);


    boolean existsById(Long id);
}
