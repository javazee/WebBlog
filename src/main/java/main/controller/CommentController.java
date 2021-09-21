package main.controller;

import main.api.request.CommentRequest;
import main.api.response.AddCommentResponse;
import main.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class CommentController {

    private final CommentService service;

    @Autowired
    public CommentController(CommentService service) {
        this.service = service;
    }

    @PostMapping("/comment")
    @PreAuthorize("hasAuthority('user:write')")
    protected ResponseEntity<?> comment(@RequestBody CommentRequest comment){
        AddCommentResponse response = service.comment(comment.getParentId(), comment.getPostId(), comment.getText());
        if (response.getResult() == null && response.getErrors().isEmpty()) return ResponseEntity.status(HttpStatus.OK).body(response);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);

    }
}
