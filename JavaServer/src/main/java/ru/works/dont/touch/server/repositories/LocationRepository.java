package ru.works.dont.touch.server.repositories;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import ru.works.dont.touch.server.entities.Location;

import java.util.stream.Stream;

public interface LocationRepository extends CrudRepository<Location, Long> {
    Location getLocationById(Long locationId);

    Stream<Location> findAllByCardId(Long cardId);

    Stream<Location> findAllByCustom(boolean isCustom);

    void deleteAllById(Long id);

    void deleteAllByCardId(Long cardId);

    @Modifying
    @Query("update Location location set location.name = :name," +
            "location.custom = :custom," +
            "location.cardId = :cardId " +
            "where location.id = :locationId")
    void update(@Param("locationId") Long locationId,
                @Param("name") String name,
                @Param("custom") Boolean custom,
                @Param("cardId") Long cardId);

}
