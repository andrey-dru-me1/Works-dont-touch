package ru.works.dont.touch.server.servicies;

import org.springframework.stereotype.Service;
import ru.works.dont.touch.server.entities.Image;
import ru.works.dont.touch.server.repositories.ImageRepository;

@Service
public class ImageService {
    private ImageRepository imageRepository;
    public ImageService(ImageRepository imageRepository){
        this.imageRepository = imageRepository;
    }
    public Iterable<Image> findAll(){
        return imageRepository.findAll();
    }
}
