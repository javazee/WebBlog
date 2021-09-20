package main.controller;

import main.api.response.ImageResponse;
import main.api.response.InitResponse;
import main.api.response.SettingsResponse;
import main.api.response.tagsResponse.TagsResponse;
import main.service.GeneralService;
import main.service.SettingsService;
import main.service.TagsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.Objects;

@RestController
@RequestMapping("/api")
public class ApiGeneralController {

    private final InitResponse initResponse;
    private final SettingsService settingsService;
    private final TagsService tagsService;
    private final GeneralService generalService;

    @Autowired
    public ApiGeneralController(InitResponse initResponse,
                                SettingsService service,
                                TagsService tagsService,
                                GeneralService generalService) {
        this.initResponse = initResponse;
        this.settingsService = service;
        this.tagsService = tagsService;
        this.generalService = generalService;
    }

    @GetMapping("/init")
    private InitResponse init(){
        return initResponse;
    }

    @GetMapping("/settings")
    private SettingsResponse settings(){
        return settingsService.getGlobalSettings();
    }

    @GetMapping("/tag")
    private TagsResponse tags(@RequestParam(required = false) String query){
        return tagsService.getTags(query);
    }

    @PostMapping(value = "/image", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    private ResponseEntity<?> image(@RequestPart MultipartFile image) throws IOException {
        if (Objects.requireNonNull(image.getOriginalFilename()).endsWith(".jpg") ||
                image.getOriginalFilename().endsWith(".png")){
            if (image.getSize() < 1000000) {
                return ResponseEntity.status(HttpStatus.OK).body(generalService.loadImage(image.getBytes()));
            } else {
                ImageResponse imageResponse = new ImageResponse();
                imageResponse.getInvalidData().put("image", "Размер файла превышает допустимый размер");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(imageResponse);
            }
        } else {
            ImageResponse imageResponse = new ImageResponse();
            imageResponse.getInvalidData().put("type", "файл не формата изображения jpg или png");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(imageResponse);
        }
    }
}
