package ru.works.dont.touch.server.servicies;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import ru.works.dont.touch.server.entities.Image;
import ru.works.dont.touch.server.exceptions.ExistsException;
import ru.works.dont.touch.server.exceptions.NotExistsException;
import ru.works.dont.touch.server.repositories.ImageRepository;

import java.awt.image.BufferedImage;
import java.io.*;
import java.util.stream.Stream;

@Service
public class ImageService {
    private ImageRepository imageRepository;

    public ImageService(ImageRepository imageRepository) {
        this.imageRepository = imageRepository;
    }

    public Iterable<Image> findAll() {
        return imageRepository.findAll();
    }

    public Image findImageById(Long imageId) throws NotExistsException {
        if (!imageRepository.existsById(imageId)) {
            throw new NotExistsException("" + imageId);
        }
        return imageRepository.findById(imageId);
    }
    public Iterable<Image> findAllImageByCardId(Long cardId){
        return imageRepository.findByCardId(cardId);
    }

    @Transactional
    public void deleteById(Long id) {
        imageRepository.deleteById(id);
    }

    /**
     * Delete all by cardId.
     *
     * @param cardId the id of card
     */
    @Transactional
    public void deleteByCardId(Long cardId) {
        imageRepository.deleteAllByCardId(cardId);
    }


    public Iterable<Image> findAllByCardId(Long cardId) {
        return imageRepository.findAllByCardId(cardId);
    }

    @Transactional
    public Image saveImage(Image image) throws ExistsException {
        if (imageRepository.existsByCardId(image.getId())) {
            throw new ExistsException("Card already exists");
        }
        return imageRepository.save(image);
    }

    @Transactional
    public Image saveImage(Long cardId, InputStream inputStream) throws IOException {
        Image newImage = new Image();
        newImage.setCardId(cardId);
        var savedCard = imageRepository.save(newImage);
        InputStreamReader reader = new InputStreamReader(inputStream);

        File dir = new File("src/main/resources/CardId_"
                + savedCard.getCardId());
        dir.mkdir();
        File file = new File("src/main/resources/CardId_"
                + savedCard.getCardId() + "/"
                + "Image_"+savedCard.getId());
        boolean resSave = file.createNewFile();
        try (OutputStream outputStream = new FileOutputStream(file)) {
            outputStream.write(inputStream.readAllBytes());
        }
        return savedCard;
    }


}
