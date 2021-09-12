package main.controller;

import main.api.response.postsResponse.PostResponseById;
import main.api.response.postsResponse.PostsCountByDateResponse;
import main.api.response.postsResponse.ListOfPostResponse;
import main.service.PostService;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.*;

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
                                     @RequestParam(required = false) String mode){
        return postService.listPosts(offset, limit, mode);
    }

    @GetMapping("/post/search")
    private ListOfPostResponse searchPosts(@Param("offset") int offset,
                                     @Param("limit") int limit,
                                     @RequestParam(required = false) String query) {
        return postService.searchPosts(offset, limit, query);
    }

    @GetMapping("/post/byDate")
    private ListOfPostResponse getPostsByDate(@Param("offset") int offset,
                                              @Param("limit") int limit,
                                              @Param("date") String date){
        return postService.getPostsByDate(offset, limit, date);
    }

    @GetMapping("/post/byTag")
    private ListOfPostResponse getPostsByTag(@Param("offset") int offset,
                                              @Param("limit") int limit,
                                              @RequestParam String tag){
        return postService.getPostsByTag(offset, limit, tag);
    }

    @GetMapping("/calendar")
    private PostsCountByDateResponse getCountOfPostsByDate(@RequestParam(required = false) Integer year){
        return postService.getCountOfPostsByDate(year);
    }

    @GetMapping("/post/{id}")
    private PostResponseById getPostById(@PathVariable Integer id){
        return postService.getPostById(id);
    }

}
