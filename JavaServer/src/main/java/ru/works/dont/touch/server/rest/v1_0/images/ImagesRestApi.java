package ru.works.dont.touch.server.rest.v1_0.images;

import org.aspectj.util.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileUrlResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.works.dont.touch.server.entities.Card;
import ru.works.dont.touch.server.entities.Image;
import ru.works.dont.touch.server.entities.User;
import ru.works.dont.touch.server.exceptions.NotExistsException;
import ru.works.dont.touch.server.rest.v1_0.auth.AuthorizationService;
import ru.works.dont.touch.server.rest.v1_0.cards.exception.UnknownCardException;
import ru.works.dont.touch.server.rest.v1_0.excepton.ForbiddenException;
import ru.works.dont.touch.server.rest.v1_0.excepton.IOServerException;
import ru.works.dont.touch.server.rest.v1_0.excepton.InternalServerException;
import ru.works.dont.touch.server.rest.v1_0.excepton.NoAuthorizationException;
import ru.works.dont.touch.server.rest.v1_0.images.exception.UnknownImageException;
import ru.works.dont.touch.server.servicies.CardService;
import ru.works.dont.touch.server.servicies.ImageService;

import java.io.*;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping(path="/v1.0/images")
public class ImagesRestApi {

    @Autowired
    private AuthorizationService authorizationService;

    @Autowired
    private CardService cardService;

    @Autowired
    private ImageService imageService;

    private final Logger logger = LoggerFactory.getLogger(ImagesRestApi.class);

    @RequestMapping(path = "/upload",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = { MediaType.MULTIPART_FORM_DATA_VALUE },
            method = RequestMethod.POST)
    public ru.works.dont.touch.server.rest.v1_0.images.object.image.Image uploadImage(
            @RequestHeader(value = "Authorization", required = true) String authorization,
            @RequestPart MultipartFile file,
            @RequestParam(name = "cardId", required = true) long cardId) {
        Optional<User> optionalUser = authorizationService.authorization(authorization);
        if(optionalUser.isEmpty()) {
            throw new NoAuthorizationException();
        }
        User user = optionalUser.get();
        Card card;
        try {
            card = cardService.getCardById(cardId);
            if (card == null || !Objects.equals(card.getOwnerId(), user.getId()))
                throw new UnknownCardException();
        } catch (NotExistsException e) {
            throw new UnknownCardException();
        }
        try (InputStream inputStream = file.getInputStream()) {
            var image = imageService.saveImage(cardId);
            imageService.saveImageInMemory(image, inputStream);
            return new ru.works.dont.touch.server.rest.v1_0.images.object.image.Image(image.getId(), image.getCardId());
        } catch (IOException e) {
            throw new IOServerException();
        } catch (NotExistsException e) {
            throw new InternalServerException();
        }
    }

    @RequestMapping(path = "/get",
            produces = MediaType.MULTIPART_FORM_DATA_VALUE,
            method = RequestMethod.GET)
    public ResponseEntity<Resource> getImage(
            @RequestHeader(value = "Authorization", required = true) String authorization,
            @RequestParam(value = "id", required = true) long id
    ) {
        Optional<User> optionalUser = authorizationService.authorization(authorization);
        if(optionalUser.isEmpty()) {
            throw new NoAuthorizationException();
        }
        User user = optionalUser.get();
        Image image;
        try {
            image = imageService.findImageById(id);
            if (image == null) {
                throw new UnknownImageException();
            }
        } catch (NotExistsException e) {
            throw new UnknownImageException();
        }
        Card card;
        try {
            card = cardService.getCardById(image.getCardId());
            if (card == null || !Objects.equals(card.getOwnerId(), user.getId()))
                throw new UnknownCardException();
        } catch (NotExistsException e) {
            throw new UnknownCardException();
        }
        if(!user.getId().equals(card.getOwnerId())) {
            throw new ForbiddenException();
        }
        try {
            Resource file = FileUrlResource.from(imageService.getUriByImage(image));
            return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=\"" + file.getFilename() + "\"").body(file);
        } catch (NotExistsException e) {
            throw new UnknownImageException();
        }
    }

    @RequestMapping(path = "/edit",
            produces = MediaType.APPLICATION_JSON_VALUE,
            method = RequestMethod.POST)
    public ru.works.dont.touch.server.rest.v1_0.images.object.image.Image editImage(
            @RequestHeader(value = "Authorization", required = true) String authorization,
            @RequestPart MultipartFile file,
            @RequestParam(value = "id", required = true) long id
    ) {
        Optional<User> optionalUser = authorizationService.authorization(authorization);
        if(optionalUser.isEmpty()) {
            throw new NoAuthorizationException();
        }
        User user = optionalUser.get();
        Image image;
        try {
            image = imageService.findImageById(id);
            if (image == null) {
                throw new UnknownImageException();
            }
        } catch (NotExistsException e) {
            throw new UnknownImageException();
        }
        Card card;
        try {
            card = cardService.getCardById(image.getCardId());
            if (card == null || !Objects.equals(card.getOwnerId(), user.getId()))
                throw new UnknownCardException();
        } catch (NotExistsException e) {
            throw new UnknownCardException();
        }
        if(!user.getId().equals(card.getOwnerId())) {
            throw new ForbiddenException();
        }
        try (InputStream inputStream = file.getInputStream()) {
            imageService.saveImageInMemory(image, inputStream);
            return new ru.works.dont.touch.server.rest.v1_0.images.object.image.Image(image.getId(), image.getCardId());
        } catch (IOException e) {
            throw new IOServerException();
        } catch (NotExistsException e) {
            throw new InternalServerException();
        }
    }


}
