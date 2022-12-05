package ru.works.dont.touch.server.servicies;

import jakarta.transaction.Transactional;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.aspectj.util.FileUtil;
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
    public Image deleteById(Long id) throws NotExistsException {
        var img = findImageById(id);
        getImageFile(img).delete();
        return img;
    }

    /**
     * Delete all by cardId.
     *
     * @param cardId the id of card
     */
    @Transactional
    public Iterable<Image> deleteByCardId(Long cardId) throws NotExistsException {
        if (!imageRepository.existsByCardId(cardId)){
            throw new NotExistsException("Image with cardId not exist, id: "+cardId);
        }
        var imgs = imageRepository.findByCardId(cardId);
        imageRepository.deleteAllByCardId(cardId);
        for (Image img : imgs) {
            var fileImg = getImageFile(img);
            if (fileImg.exists()){
                fileImg.delete();
            }
        }
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
        var savedCard = imageRepository.save(newImage);

        return savedCard;
    }

    public URI saveImageInMemory(Image image, InputStream inputStream) throws NotExistsException, IOException {
        if (image.getId() == null){
            throw new NotExistsException();
        }

        File dir = new File(imageDirectory
                + "/CardId_"
                + image.getCardId() + "/");

        if (!dir.exists()){
            dir.mkdirs();
        }

        File file = new File(dir, "Image_"+image.getId());
        try (BufferedOutputStream bufferedWriter = new BufferedOutputStream(new FileOutputStream(file))) {
            int readReturn;
            byte[] buf = new byte[10000];
            while (true){
                readReturn = inputStream.read(buf);
                if (readReturn<=0){
                    break;
                }
                bufferedWriter.write(buf,0, readReturn);
            }
        }
        return file.toURI();
    }

    public URI getUriByImage(Image image) throws NotExistsException {
        File file = getImageFile(image);
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

    private File getImageFile(Image image){
        return new File(imageDirectory +
                "/CardId_" + image.getCardId()
                + "/Image_" + image.getId());
    }
}
