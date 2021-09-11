package main.service;

import main.api.response.postsResponse.ListOfPostResponse;
import main.api.response.postsResponse.PostResponse;
import main.model.Post;
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
        List<Object[]> posts;
        switch (mode) {
            case "early":
                if (offset == 0) page = 0;
                posts = postRepository.getPostsOrderByTime(new Date(), PageRequest.of(page, limit, Sort.by("publicationTime")));
                page++;
                break;
            case "popular":
                if (offset == 0) page = 0;
                posts = postRepository.getPostsOrderByCountOfComment(new Date(), PageRequest.of(page, limit));
                page++;
                break;
            case "best":
                if (offset == 0) page = 0;
                posts = postRepository.getPostsOrderByCountOfLike(new Date(), PageRequest.of(page, limit));
                page++;
                break;
            default:
                if (offset == 0) page = 0;
                posts = postRepository.getPostsOrderByTime(new Date(), PageRequest.of(page, limit, Sort.by("publicationTime").descending()));
                page++;
        }
        return createPostsList(posts);
    }

    public ListOfPostResponse searchPosts(int offset, int limit, String query){
        List<Object[]> posts;
        if (query.trim().isEmpty()){
            if (offset == 0) page = 0;
            posts = postRepository.getPostsOrderByTime(new Date(), PageRequest.of(page, limit, Sort.by("publicationTime").descending()));
            page++;
        } else {
            if (offset == 0) page = 0;
            posts = postRepository.searchPostsOrderByTimeDesc(query, new Date(), PageRequest.of(page, limit));
            page++;
        }
        return createPostsList(posts);
    }

    String createAnnounce(String text){
        String noHTMLText = text.replaceAll("\\<.*?\\>", "");
        int count = 0;
        while (true){
            int index = noHTMLText.indexOf(" ", count + 1);
            if (index < 150) {
                count = index;
            } else return noHTMLText.substring(0, count) + "...";
        }
    }

    ListOfPostResponse createPostsList(List<Object[]> posts){
        ListOfPostResponse listOfPost = new ListOfPostResponse();
        for (Object[] object : posts) {
            PostResponse postResponse = new PostResponse();
            Post post = (Post) object[0];
            postResponse.setId(post.getId());
            postResponse.setTimestamp(post.getPublicationTime().getTime() / 1000);
            postResponse.setTitle(post.getTittle());
            postResponse.setAnnounce(createAnnounce(post.getText()));
            postResponse.setViewCount(post.getCountOfView());
            postResponse.setCommentCount(Integer.parseInt(object[2].toString()));
            postResponse.setLikeCount(Integer.parseInt(object[3].toString()));
            postResponse.setDislikeCount(Integer.parseInt(object[4].toString()));
            postResponse.getUser().setId(post.getUser().getId());
            postResponse.getUser().setName(post.getUser().getName());
            listOfPost.getPosts().add(postResponse);
        }
        listOfPost.setCount(posts.size() != 0? Integer.parseInt(posts.get(0)[1].toString()) : 0);
        return listOfPost;
    }
}
