package ru.works.dont.touch.server.network.images;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping(path="/images")
public class ImagesRestController {

    @RequestMapping(
            value = "/save",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            method = {RequestMethod.POST})
    public void saveImage(
            @RequestHeader(value = "Authorization", required = true) String authorization,
            @RequestParam(value = "cardid", required = true) long cardID,
            @RequestPart(value = "image", required = true) MultipartFile image) {

    }

    @RequestMapping(
            value = "/load",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            method = {RequestMethod.GET})
    public void loadImage(
            @RequestHeader(value = "Authorization", required = true) String authorization,
            @RequestParam(value = "id", required = true) long cardID) {

    }

    @RequestMapping(
            value = "/edit",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            method = {RequestMethod.GET})
    public void loadImage(
            @RequestHeader(value = "Authorization", required = true) String authorization,
            @RequestParam(value = "id", required = true) long cardID,
            @RequestPart(value = "image", required = true) MultipartFile image) {

    }


}
