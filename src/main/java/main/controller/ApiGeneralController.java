package main.controller;

import main.api.request.CommentRequest;
import main.api.response.*;
import main.api.response.tagsResponse.TagsResponse;
import main.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
    private final ImageService generalService;
    private final CommentService commentService;

    @Autowired
    public ApiGeneralController(InitResponse initResponse,
                                SettingsService service,
                                TagsService tagsService,
                                ImageService generalService,
                                CommentService commentService) {
        this.initResponse = initResponse;
        this.settingsService = service;
        this.tagsService = tagsService;
        this.generalService = generalService;
        this.commentService = commentService;
    }

    @GetMapping("/init")
    protected InitResponse init(){
        return initResponse;
    }

    @GetMapping("/settings")
    protected SettingsResponse settings(){
        return settingsService.getGlobalSettings();
    }

    @GetMapping("/tag")
    protected TagsResponse tags(@RequestParam(required = false) String query){
        return tagsService.getTags(query);
    }

    @PostMapping(value = "/image", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @PreAuthorize("hasAuthority('user:write')")
    protected ResponseEntity<?> image(@RequestPart MultipartFile image) throws IOException {
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

    @PostMapping("/comment")
    @PreAuthorize("hasAuthority('user:write')")
    protected ResponseEntity<AddCommentResponse> comment(@RequestBody CommentRequest comment){
        AddCommentResponse response = commentService.comment(comment.getParentId(), comment.getPostId(), comment.getText());
        if (response.getResult() == null && response.getErrors().isEmpty()) return ResponseEntity.status(HttpStatus.OK).body(response);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);

    }
}
