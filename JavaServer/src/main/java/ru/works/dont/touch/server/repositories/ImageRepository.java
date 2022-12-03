package ru.works.dont.touch.server.repositories;

import org.springframework.data.repository.CrudRepository;
import ru.works.dont.touch.server.entities.Image;

public interface ImageRepository extends CrudRepository<Image, Integer> {
    Image findById(Long id);
}
