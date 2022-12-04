package ru.works.dont.touch.server.data.repositories;

import org.springframework.data.repository.CrudRepository;
import ru.works.dont.touch.server.data.entities.Image;

public interface ImageRepository extends CrudRepository<Image, Integer> {
    Image findById(Long id);
}
