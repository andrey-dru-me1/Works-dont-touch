package ru.works.dont.touch.server.rest.v1_0.images;

import org.aspectj.util.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileUrlResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.works.dont.touch.server.entities.User;
import ru.works.dont.touch.server.rest.v1_0.auth.AuthorizationService;
import ru.works.dont.touch.server.rest.v1_0.excepton.IOServerException;
import ru.works.dont.touch.server.rest.v1_0.excepton.NoAuthorizationException;
import ru.works.dont.touch.server.rest.v1_0.images.object.image.Image;
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

    @RequestMapping(path = "/upload",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = { MediaType.MULTIPART_FORM_DATA_VALUE },
            method = RequestMethod.POST)
    public Image uploadImage(
            @RequestHeader(value = "Authorization", required = true) String authorization,
            @RequestPart MultipartFile file,
            @RequestParam(name = "cardId", required = true) long cardId) {
        Optional<User> optionalUser = authorizationService.authorization(authorization);
        if(optionalUser.isEmpty()) {
            throw new NoAuthorizationException();
        }
        User user = optionalUser.get();
        File f = new File("image.png");
        try (InputStream inputStream = file.getInputStream()) {
            try (OutputStream outputStream = new FileOutputStream(f)) {
                FileUtil.copyStream(inputStream, outputStream);
            }
        } catch (IOException e) {
            throw new IOServerException();
        }
        return new Image(0, 23);
    }

    @RequestMapping(path = "/get",
            produces = MediaType.MULTIPART_FORM_DATA_VALUE,
            method = RequestMethod.GET)
    public ResponseEntity<Resource> getImage(
            @RequestHeader(value = "Authorization", required = true) String authorization,
            @RequestParam(value = "id", required = true) long id
    ) {
        Resource file = FileUrlResource.from(new File("image.png").toURI());
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }

    @RequestMapping(path = "/edit",
            produces = MediaType.MULTIPART_FORM_DATA_VALUE,
            method = RequestMethod.POST)
    public ResponseEntity<Resource> editImage(
            @RequestHeader(value = "Authorization", required = true) String authorization,
            @RequestPart MultipartFile file,
            @RequestParam(value = "id", required = true) long id
    ) {
        Resource file1 = FileUrlResource.from(new File("image.png").toURI());
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + file1.getFilename() + "\"").body(file1);
    }


}
