package main.service;

import main.api.response.postsResponse.ListOfPostResponse;
import main.api.response.postsResponse.PostResponse;
import main.model.Post;
import main.model.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostService {


    private final PostRepository postRepository;

    private int page;

    @Autowired
    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public ListOfPostResponse listPosts(int offset, int limit, String mode) {
        ListOfPostResponse listOfPost = new ListOfPostResponse();
        listOfPost.setCount(postRepository.countWithAcceptedStatusAndActiveState());
        List<Object[]> posts;
        switch (mode) {
            case "early":
                if (offset == 0) page = 0;
                posts = postRepository.getPostsOrderByTime(PageRequest.of(page, limit));
                page++;
                break;
            case "popular":
                if (offset == 0) page = 0;
                posts = postRepository.getPostsOrderByCountOfComment(PageRequest.of(page, limit));
                page++;
                break;
            case "best":
                if (offset == 0) page = 0;
                posts = postRepository.getPostsOrderByCountOfLike(PageRequest.of(page, limit));
                page++;
                break;
            default:
                if (offset == 0) page = 0;
                posts = postRepository.getPostsOrderByTimeDesc(PageRequest.of(page, limit));
                page++;
        }
        for (Object[] object : posts) {
            PostResponse postResponse = new PostResponse();
            Post post = (Post) object[0];
            postResponse.setId(post.getId());
            postResponse.setTimestamp(post.getPublicationTime().getTime() / 1000);
            postResponse.setTitle(post.getTittle());
            postResponse.setAnnounce(createAnnounce(post.getText()));
            postResponse.setViewCount(post.getCountOfView());
            postResponse.setCommentCount(Integer.parseInt(object[1].toString()));
            postResponse.setLikeCount(Integer.parseInt(object[2].toString()));
            postResponse.setDislikeCount(Integer.parseInt(object[3].toString()));
            postResponse.getUser().setId(post.getUser().getId());
            postResponse.getUser().setName(post.getUser().getName());
            listOfPost.getPosts().add(postResponse);
        }
        return listOfPost;
    }

    String createAnnounce(String text){
        String noHTMLext = text.replaceAll("\\<.*?\\>", "");
        int count = 0;
        while (true){
            int index = noHTMLext.indexOf(" ", count + 1);
            if (index < 150) {
                count = index;
            } else return noHTMLext.substring(0, count) + "...";
        }
    }
}
