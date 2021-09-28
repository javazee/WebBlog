package main.controller;

import main.api.request.PostRequest;
import main.api.request.VoteRequest;
import main.api.response.AddOrEditPostResponse;
import main.api.response.VoteResponse;
import main.api.response.postsResponse.ListOfPostResponse;
import main.api.response.postsResponse.PostResponseById;
import main.service.PostService;
import main.service.VoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/post")
public class ApiPostController {

    private final PostService postService;
    private final VoteService voteService;

    @Autowired
    public ApiPostController(PostService postService, VoteService voteService) {
        this.postService = postService;
        this.voteService = voteService;
    }

    @GetMapping
    protected ResponseEntity<ListOfPostResponse> posts(@RequestParam(defaultValue = "0", required = false) int offset,
                                     @RequestParam(defaultValue = "10", required = false) int limit,
                                     @RequestParam(defaultValue = "recent", required = false) String mode){
        return ResponseEntity.status(HttpStatus.OK).body(postService.listPosts(offset, limit, mode));
    }

    @GetMapping(path = "/search")
    protected ResponseEntity<ListOfPostResponse> searchPosts(@RequestParam(defaultValue = "0", required = false) int offset,
                                                           @RequestParam(defaultValue = "10", required = false) int limit,
                                                           @RequestParam(defaultValue = "", required = false) String query) {
        return ResponseEntity.status(HttpStatus.OK).body(postService.searchPosts(offset, limit, query));
    }

    @GetMapping("/byDate")
    protected ResponseEntity<ListOfPostResponse> getPostsByDate(@RequestParam(defaultValue = "0", required = false) int offset,
                                               @RequestParam(defaultValue = "10", required = false) int limit,
                                               @Param("date") String date){
        return ResponseEntity.status(HttpStatus.OK).body(postService.getPostsByDate(offset, limit, date));
    }

    @GetMapping("/byTag")
    protected ResponseEntity<ListOfPostResponse> getPostsByTag(@RequestParam(defaultValue = "0", required = false) int offset,
                                              @RequestParam(defaultValue = "10", required = false) int limit,
                                              @RequestParam String tag){
        return ResponseEntity.status(HttpStatus.OK).body(postService.getPostsByTag(offset, limit, tag));
    }

    @GetMapping("/{id}")
    protected ResponseEntity<?> getPostById(@PathVariable Integer id) {
        PostResponseById postResponseById = postService.getPostById(id);
        if (postResponseById == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Документ не найден");
        return new ResponseEntity<>(postResponseById, HttpStatus.OK);
    }

    @GetMapping(path = "/my")
    @PreAuthorize("hasAuthority('user:write')")
    protected ResponseEntity<ListOfPostResponse> getMyPosts(@RequestParam(defaultValue = "0", required = false) int offset,
                                                             @RequestParam(defaultValue = "10", required = false) int limit,
                                                             @RequestParam(defaultValue = "inactive", required = false) String status) {
        return ResponseEntity.status(HttpStatus.OK).body(postService.findMyPosts(offset, limit, status));
    }

    @GetMapping(path = "/moderation")
    @PreAuthorize("hasAuthority('user:moderate')")
    protected ResponseEntity<ListOfPostResponse> getPostsForModeration(@RequestParam(defaultValue = "0", required = false) int offset,
                                                                       @RequestParam(defaultValue = "10", required = false) int limit,
                                                                       @RequestParam(defaultValue = "new", required = false) String status) {
        return ResponseEntity.status(HttpStatus.OK).body(postService.getPostsForModeration(offset, limit, status));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('user:write')")
    protected ResponseEntity<AddOrEditPostResponse> addPost(@RequestBody PostRequest postRequest){
        return ResponseEntity.status(HttpStatus.OK)
                .body(
                        postService.createOrUpdatePost(
                                postRequest.getTimestamp(),
                                postRequest.getActive(),
                                postRequest.getTitle(),
                                postRequest.getTags(),
                                postRequest.getText(),
                                null));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('user:write')")
    protected ResponseEntity<AddOrEditPostResponse> editPost(@RequestBody PostRequest postRequest,
                                                             @PathVariable Integer id){
        return ResponseEntity.status((HttpStatus.OK))
                .body(
                        postService.createOrUpdatePost(
                                postRequest.getTimestamp(),
                                postRequest.getActive(),
                                postRequest.getTitle(),
                                postRequest.getTags(),
                                postRequest.getText(),
                                id));
    }

    @PostMapping("/like")
    @PreAuthorize("hasAuthority('user:write')")
    protected ResponseEntity<VoteResponse> like(@RequestBody VoteRequest vote) {
        return ResponseEntity.status(HttpStatus.OK).body(voteService.like(vote.getPostId()));
    }

    @PostMapping("/dislike")
    @PreAuthorize("hasAuthority('user:write')")
    protected ResponseEntity<VoteResponse> dislike(@RequestBody VoteRequest vote) {
        return ResponseEntity.status(HttpStatus.OK).body(voteService.dislike(vote.getPostId()));
    }
}
