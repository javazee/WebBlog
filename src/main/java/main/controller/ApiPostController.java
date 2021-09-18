package main.controller;

import main.api.request.DecisionRequest;
import main.api.request.PostRequest;
import main.api.response.AddPostResponse;
import main.api.response.postsResponse.ListOfPostResponse;
import main.api.response.postsResponse.PostResponseById;
import main.api.response.postsResponse.PostsCountByDateResponse;
import main.service.PostService;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ApiPostController {

    private final PostService postService;

    public ApiPostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping(path = "/post")
    protected ResponseEntity<ListOfPostResponse> posts(@RequestParam(defaultValue = "0", required = false) int offset,
                                     @RequestParam(defaultValue = "10", required = false) int limit,
                                     @RequestParam(defaultValue = "recent", required = false) String mode){
        return ResponseEntity.status(HttpStatus.OK).body(postService.listPosts(offset, limit, mode));
    }

    @GetMapping(path = "/post/search")
    protected ResponseEntity<ListOfPostResponse> searchPosts(@RequestParam(defaultValue = "0", required = false) int offset,
                                                           @RequestParam(defaultValue = "10", required = false) int limit,
                                                           @RequestParam(defaultValue = "", required = false) String query) {
        return ResponseEntity.status(HttpStatus.OK).body(postService.searchPosts(offset, limit, query));
    }

    @GetMapping("/post/byDate")
    protected ResponseEntity<ListOfPostResponse> getPostsByDate(@RequestParam(defaultValue = "0", required = false) int offset,
                                               @RequestParam(defaultValue = "10", required = false) int limit,
                                               @Param("date") String date){
        return ResponseEntity.status(HttpStatus.OK).body(postService.getPostsByDate(offset, limit, date));
    }

    @GetMapping("/post/byTag")
    protected ResponseEntity<ListOfPostResponse> getPostsByTag(@RequestParam(defaultValue = "0", required = false) int offset,
                                              @RequestParam(defaultValue = "10", required = false) int limit,
                                              @RequestParam String tag){
        return ResponseEntity.status(HttpStatus.OK).body(postService.getPostsByTag(offset, limit, tag));
    }

    @GetMapping("/calendar")
    protected PostsCountByDateResponse getCountOfPostsByDate(@RequestParam(required = false) Integer year){
        return postService.getCountOfPostsByDate(year);
    }

    @GetMapping("/post/{id}")
    protected ResponseEntity<?> getPostById(@PathVariable Integer id) {
        PostResponseById postResponseById = postService.getPostById(id);
        if (postResponseById == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Документ не найден");
        return new ResponseEntity<>(postResponseById, HttpStatus.OK);
    }

    @GetMapping(path = "/post/my")
    @PreAuthorize("hasAuthority('user:write')")
    protected ResponseEntity<ListOfPostResponse> getMyPosts(@RequestParam(defaultValue = "0", required = false) int offset,
                                                             @RequestParam(defaultValue = "10", required = false) int limit,
                                                             @RequestParam(defaultValue = "inactive", required = false) String status) {
        return ResponseEntity.status(HttpStatus.OK).body(postService.findMyPosts(offset, limit, status));
    }

    @GetMapping(path = "/post/moderation")
    @PreAuthorize("hasAuthority('user:moderate')")
    protected ResponseEntity<ListOfPostResponse> getPostsForModeration(@RequestParam(defaultValue = "0", required = false) int offset,
                                                                       @RequestParam(defaultValue = "10", required = false) int limit,
                                                                       @RequestParam(defaultValue = "new", required = false) String status) {
        return ResponseEntity.status(HttpStatus.OK).body(postService.getPostsForModeration(offset, limit, status));
    }

    @PostMapping("/moderation")
    @PreAuthorize("hasAuthority('user:moderate')")
    protected HttpStatus moderatePost(@RequestBody DecisionRequest decision){
        postService.moderatePost(decision.getId(), decision.getDecision());
        return HttpStatus.OK;
    }

    @PostMapping("/post")
    @PreAuthorize("hasAuthority('user:write')")
    protected ResponseEntity<AddPostResponse> addPost(@RequestBody PostRequest postRequest){
        return ResponseEntity.status(HttpStatus.OK)
                .body(
                        postService.addPost(
                                postRequest.getTimestamp(),
                                postRequest.getActive(),
                                postRequest.getTitle(),
                                postRequest.getTags(),
                                postRequest.getText()));
    }

}
