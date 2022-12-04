package ru.works.dont.touch.server.servicies;

import org.springframework.stereotype.Service;
import ru.works.dont.touch.server.entities.Card;
import ru.works.dont.touch.server.entities.Image;
import ru.works.dont.touch.server.repositories.ImageRepository;
import ru.works.dont.touch.server.servicies.exceptions.ExistsException;
import ru.works.dont.touch.server.servicies.exceptions.NotExistsException;

@Service
public class ImageService {
    private ImageRepository imageRepository;
    public ImageService(ImageRepository imageRepository){
        this.imageRepository = imageRepository;
    }
    public Iterable<Image> findAll(){
        return imageRepository.findAll();
    }

    public void deleteById(Long id){
        imageRepository.deleteById(id);
    }

    /**
     * Delete all by cardId.
     * @param cardId the id of card
     */
    public void deleteByCardId(Long cardId){
        imageRepository.deleteAllByCardId(cardId);
    }


    public Iterable<Image> findAllByCardId(Long cardId){
        return imageRepository.findAllByCardId(cardId);
    }

    public Image saveImage(Image image) throws ExistsException {
        if (imageRepository.existsByCardId(image.getId())){
            throw new ExistsException("Card already exists");
        }
        return imageRepository.save(image);
    }

    public Image saveImage(Long cardId){
        Image newImage = new Image();
        newImage.setCardId(cardId);
        imageRepository.save(newImage);
        return newImage;
    }


}
