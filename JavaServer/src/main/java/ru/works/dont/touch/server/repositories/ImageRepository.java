package ru.works.dont.touch.server.repositories;

import org.springframework.data.repository.CrudRepository;
import ru.works.dont.touch.server.entities.Card;
import ru.works.dont.touch.server.entities.Image;

import java.util.stream.Stream;

public interface ImageRepository extends CrudRepository<Image, Integer> {
    Image findById(Long id);

    void deleteById(Long id);
    void deleteAllByCardId(Long cardId);

    boolean existsById(Long id);
    boolean existsByCardId(Long cardId);

    Stream<Image> findAllByCardId(Long cardId);


}
