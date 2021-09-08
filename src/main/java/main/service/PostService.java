package main.service;

import main.api.response.postsResponse.ListOfPostResponse;
import main.api.response.postsResponse.PostResponse;
import main.model.Post;
import main.model.User;
import main.model.repository.PostCommentRepository;
import main.model.repository.PostRepository;
import main.model.repository.PostVoteRepository;
import main.model.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PostService {

    @Autowired
    PostRepository postRepository;

    @Autowired
    PostVoteRepository postVoteRepository;

    @Autowired
    PostCommentRepository postCommentRepository;

    @Autowired
    UserRepository userRepository;

    ListOfPostResponse listOfPost;

    public ListOfPostResponse listPosts(int offset, int limit, String mode){
        listOfPost = new ListOfPostResponse();
        listOfPost.setCount(postRepository.countWithAcceptedStatusAndActiveState());
        List<Post> posts;
        switch (mode) {
            case "early":
                posts = postRepository.getPostsOrderByTime(PageRequest.of(offset, limit, Sort.by("publicationTime")));
                break;
            case "popular":
                posts = postRepository.getPostsOrderByCountOfComment(PageRequest.of(offset, limit));
                break;
            case "best":
                posts = postRepository.getPostsOrderByCountOfLike(PageRequest.of(offset, limit));
                break;
            default:
                posts = postRepository.getPostsOrderByTime(PageRequest.of(offset, limit, Sort.by("publicationTime").descending()));
        }
        for (Post post: posts){
            PostResponse postResponse = new PostResponse();
            postResponse.setId(post.getId());
            postResponse.setTitle(post.getTittle());
            postResponse.setTimestamp(post.getPublicationTime().getTime() / 1000);
            postResponse.setAnnounce(createAnnounce(post.getText()));
            postResponse.setViewCount(post.getCountOfView());
            postResponse.setLikeCount(postVoteRepository.countLikeForPost(post.getId()));
            postResponse.setDislikeCount(postVoteRepository.countDislikeForPost(post.getId()));
            postResponse.setCommentCount(postCommentRepository.countCommentForPost(post.getId()));
            Optional<User> author = userRepository.findById(post.getUser().getId());
            if (author.isPresent()) {
                postResponse.getUser().setId(author.get().getId());
                postResponse.getUser().setName(author.get().getName());
            }
            listOfPost.getPosts().add(postResponse);
        }
        return listOfPost;
    }

    String createAnnounce(String text){
        int count = 0;
        while (true){
            int index = text.indexOf(" ", count + 1);
            if (index < 150) {
                count = index;
            } else return text.substring(0, count) + "...";
        }
    }
}
