package main.service;

import main.api.response.postsResponse.ListOfPostResponse;
import main.api.response.postsResponse.PostResponse;
import main.model.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Date;
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
                posts = postRepository.getPostsOrderByTime(PageRequest.of(page, limit, Sort.by("time")));
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
                posts = postRepository.getPostsOrderByTime(PageRequest.of(page, limit, Sort.by("time").descending()));
                page++;
        }
        for (Object[] post : posts) {
            PostResponse postResponse = new PostResponse();
            postResponse.setId((int) post[0]);
            postResponse.setTimestamp(((Date) post[1]).getTime() / 1000);
            postResponse.setTitle(post[2].toString());
            postResponse.setAnnounce(createAnnounce((String) post[3]));
            postResponse.setViewCount((int) post[4]);
            postResponse.setCommentCount(Integer.parseInt(post[5].toString()));
            postResponse.setLikeCount(Integer.parseInt(post[6].toString()));
            postResponse.setDislikeCount(Integer.parseInt(post[7].toString()));
            postResponse.getUser().setId(Integer.parseInt(post[8].toString()));
            postResponse.getUser().setName((String) post[9]);
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
