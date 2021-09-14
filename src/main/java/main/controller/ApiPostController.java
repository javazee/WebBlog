package main.controller;

import main.api.response.postsResponse.PostResponseById;
import main.api.response.postsResponse.PostsCountByDateResponse;
import main.service.PostService;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ApiPostController {

    private final PostService postService;

    public ApiPostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping("/post")
    private ResponseEntity<?> posts(@Param("offset") int offset,
                                     @Param("limit") int limit,
                                     @RequestParam(required = false) String mode){
        return ResponseEntity.status(HttpStatus.OK).body(postService.listPosts(offset, limit, mode));
    }

    @GetMapping("/post/search")
    private ResponseEntity<?> searchPosts(@Param("offset") int offset,
                                     @Param("limit") int limit,
                                     @RequestParam(required = false) String query) {
        return ResponseEntity.status(HttpStatus.OK).body(postService.searchPosts(offset, limit, query));
    }

    @GetMapping("/post/byDate")
    private ResponseEntity<?> getPostsByDate(@Param("offset") int offset,
                                              @Param("limit") int limit,
                                              @Param("date") String date){
        return ResponseEntity.status(HttpStatus.OK).body(postService.getPostsByDate(offset, limit, date));
    }

    @GetMapping("/post/byTag")
    private ResponseEntity<?> getPostsByTag(@Param("offset") int offset,
                                              @Param("limit") int limit,
                                              @RequestParam String tag){
        return ResponseEntity.status(HttpStatus.OK).body(postService.getPostsByTag(offset, limit, tag));
    }

    @GetMapping("/calendar")
    private PostsCountByDateResponse getCountOfPostsByDate(@RequestParam(required = false) Integer year){
        return postService.getCountOfPostsByDate(year);
    }

    @GetMapping("/post/{id}")
    private ResponseEntity<?> getPostById(@PathVariable Integer id) {
        PostResponseById postResponseById = postService.getPostById(id);
        if (postResponseById == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Документ не найден");
        return new ResponseEntity<>(postResponseById, HttpStatus.OK);
    }
}
