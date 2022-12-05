package ru.works.dont.touch.server.servicies;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.works.dont.touch.server.entities.Image;
import ru.works.dont.touch.server.exceptions.ExistsException;
import ru.works.dont.touch.server.exceptions.NotExistsException;
import ru.works.dont.touch.server.repositories.ImageRepository;

import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Service
public class ImageService {
    @Value("${image.directory}")
    private String imageDirectory;
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
    public Image saveImage(Long cardId) throws IOException {
        Image newImage = new Image();
        newImage.setCardId(cardId);
        var savedCard = imageRepository.save(newImage);

        return savedCard;
    }

    public URI saveImageInMemory(Image image, InputStream inputStream) throws NotExistsException, IOException {
        if (image.getId() == null){
            throw new NotExistsException();
        }
        BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
        File dir = new File(imageDirectory
                + "/CardId_"
                + image.getCardId() + "/");
        if (!dir.exists()){
            dir.mkdirs();
        }
        System.out.println(dir.getPath());
        File file = new File(dir, "Image_"+image.getId());
        System.out.println(file);
        if (!file.exists()){
            //file.createNewFile();
        }
        try (BufferedOutputStream bufferedWriter = new BufferedOutputStream(new FileOutputStream(file))) {
            int readReturn = 0;
            byte[] buf = new byte[1];
            while (true){
                readReturn = bufferedInputStream.read(buf);
                if (readReturn<=0){
                    break;
                }
                bufferedWriter.write(buf,0, readReturn);
            }
        }
        System.out.println(file.getPath());
        return file.toURI();
    }

    public URI getUriByImage(Image image) throws NotExistsException {
        File file = new File(imageDirectory +
                "/CardId_" + image.getCardId()
                + "/Image_" + image.getId());
        if (!file.exists()){
            throw new NotExistsException("Image not Exists");
        }
        return file.toURI();
    }
    public URI getUriByImageId(Long imageId) throws NotExistsException {
        return getUriByImage(findImageById(imageId));
    }
    public List<URI> getUrisByCardId(Long cardId){
        var images = findAllByCardId(cardId);
        List<URI> uriList = new ArrayList<>();
        for (Image image : images) {
            try {
                uriList.add(getUriByImage(image));
            } catch (NotExistsException e) {
                continue;
            }
        }
        return uriList;
    }
}
