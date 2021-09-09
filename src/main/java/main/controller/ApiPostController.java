package main.controller;

import main.api.response.postsResponse.ListOfPostResponse;
import main.service.PostService;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ApiPostController {

    private final PostService postService;

    public ApiPostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping("/post")
    private ListOfPostResponse posts(@Param("offset") int offset,
                                     @Param("limit") int limit,
                                     @Param("mode") String mode){
        return postService.listPosts(offset, limit, mode);
    }
}
