package ru.works.dont.touch.server.data.servicies;

import org.springframework.stereotype.Service;
import ru.works.dont.touch.server.data.entities.Image;
import ru.works.dont.touch.server.data.repositories.ImageRepository;

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
