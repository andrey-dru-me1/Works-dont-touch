package ru.works.dont.touch.server.servicies;

import jakarta.transaction.Transactional;
import org.aspectj.util.FileUtil;
import org.jetbrains.annotations.NotNull;
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
    private final ImageRepository imageRepository;

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
    public Image deleteById(Long id) throws NotExistsException {
        var img = findImageById(id);
        new File(getDirectory(img.getCardId()), "Image_"+img.getId()).delete();
        return img;
    }

    /**
     * Delete all by cardId.
     *
     * @param cardId the id of card
     */
    @Transactional
    public Iterable<Image> deleteByCardId(Long cardId) throws NotExistsException{
        if (!imageRepository.existsByCardId(cardId)){
            throw new NotExistsException("Image with cardId not exist, id: "+cardId);
        }
        var imgs = imageRepository.findAllByCardId(cardId);
        File dir = getDirectory(cardId);
        for (Image img : imgs) {
            try {
                deleteById(img.getId());
            } catch (NotExistsException ignore) {}
        }
        System.out.println(dir);
        dir.delete();
        imageRepository.deleteAllByCardId(cardId);
        return imgs;
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
        return imageRepository.save(newImage);
    }

    public URI saveImageInMemory(Image image, InputStream inputStream) throws NotExistsException, IOException {
        if (image.getId() == null){
            throw new NotExistsException();
        }
        File dir = getDirectory(image.getCardId());
        dir.mkdirs();
        File file = new File(dir, "Image_"+image.getId());
        try (OutputStream outputStream = new FileOutputStream(file)) {
            FileUtil.copyStream(inputStream, outputStream);
        }
        return file.toURI();
    }

    public URI getUriByImage(Image image) throws NotExistsException {
        File file = new File(getDirectory(image.getCardId()), "Image_" + image.getId());
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
                continue; // Ignore exceptions? Strange code. I don't know how fix it, maybe combine database and file methods
            }
        }
        return uriList;
    }

    private File getDirectory(@NotNull Long cardId) {
        return new File(imageDirectory, cardId.toString());
    }


    private File getImageFile(Image image){
        return new File(imageDirectory +
                "/CardId_" + image.getCardId()
                + "/Image_" + image.getId());
    }
}
