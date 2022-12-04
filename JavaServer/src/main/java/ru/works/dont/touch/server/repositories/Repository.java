package ru.works.dont.touch.server.repositories;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import ru.works.dont.touch.server.entities.Coordinate;

import java.util.stream.Stream;

@NoRepositoryBean
public interface Repository {
    @Query("SELECT coord FROM Coordinate coord " +
            "LEFT JOIN Location loc on " +
            "loc.id = coord.locationId " +
            "left join Card card on " +
            "card.id = loc.cardId " +
            "left join User user on " +
            "user.id = :userId")
    Stream<Coordinate> findAllUserCoordinates(Long userId);
}
