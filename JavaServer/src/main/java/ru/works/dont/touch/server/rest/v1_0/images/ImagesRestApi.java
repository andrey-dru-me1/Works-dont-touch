package ru.works.dont.touch.server.rest.v1_0.images;

import org.aspectj.util.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.works.dont.touch.server.entities.User;
import ru.works.dont.touch.server.rest.v1_0.auth.AuthorizationService;
import ru.works.dont.touch.server.rest.v1_0.excepton.IOServerException;
import ru.works.dont.touch.server.rest.v1_0.excepton.NoAuthorizationException;
import ru.works.dont.touch.server.rest.v1_0.objects.image.Image;
import ru.works.dont.touch.server.servicies.CardService;
import ru.works.dont.touch.server.servicies.ImageService;

import java.io.*;
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

    @RequestMapping(path = "/save",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            method = RequestMethod.GET)
    public Image saveImage(
            @RequestHeader(value = "Authorization", required = true) String authorization,
            @RequestParam MultipartFile file,
            @RequestParam(name = "cardId", required = true) long cardId) {
        Optional<User> optionalUser = authorizationService.authorization(authorization);
        if(optionalUser.isEmpty()) {
            throw new NoAuthorizationException();
        }
        User user = optionalUser.get();
        File f = new File(file.getName());
        try (InputStream inputStream = file.getInputStream()) {
            try (OutputStream outputStream = new FileOutputStream(f)) {
                FileUtil.copyStream(inputStream, outputStream);
            }
        } catch (IOException e) {
            throw new IOServerException();
        }
        return new Image(0, 23);
    }

}
