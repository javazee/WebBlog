package main.service;

import main.api.response.postsResponse.*;
import main.model.Post;
import main.model.PostComment;
import main.model.User;
import main.model.repository.PostCommentRepository;
import main.model.repository.PostRepository;
import main.model.repository.TagToPostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class PostService {


    private final PostRepository postRepository;

    private final PostCommentRepository postCommentRepository;

    private final TagToPostRepository tagToPostRepository;

    private int page;

    @Autowired
    public PostService(PostRepository postRepository,
                       PostCommentRepository postCommentRepository,
                       TagToPostRepository tagToPostRepository) {
        this.postRepository = postRepository;
        this.postCommentRepository = postCommentRepository;
        this.tagToPostRepository = tagToPostRepository;
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
        if (query == null || query.trim().isEmpty()){
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

    public ListOfPostResponse getPostsByDate(int offset, int limit, String date){
        if (offset == 0) page = 0;
        List<Object[]> posts;
        try {
            posts = postRepository.getPostsByDate(new SimpleDateFormat("yyyy-MM-dd").parse(date),
                    new Date(),
                    PageRequest.of(page, limit));
            page++;
        } catch (ParseException ex){
            ex.printStackTrace();
            return new ListOfPostResponse();
        }
        return createPostsList(posts);
    }

    public ListOfPostResponse getPostsByTag(int offset, int limit, String tag){
        if (offset == 0) page = 0;
        List<Object[]> posts = postRepository.findPostsByTag(tag, new Date(), PageRequest.of(page, limit));
        page++;
        return createPostsList(posts);
    }

    public PostsCountByDateResponse getCountOfPostsByDate(Integer year){
        List<Integer> years = postRepository.getYears(new Date());
        HashMap<String, Integer> postsCountByDate = new HashMap<>();
        List<Object[]> posts;
        if (year == null){
             posts = postRepository.getPostsCountByDate(Calendar.getInstance().get(Calendar.YEAR), new Date());
        } else {
             posts = postRepository.getPostsCountByDate(year, new Date());
        }
        PostsCountByDateResponse postsCountByDateResponse = new PostsCountByDateResponse();
        postsCountByDateResponse.getYears().addAll(years);
        for (Object[] post: posts){
            postsCountByDate.put(new SimpleDateFormat("yyyy-MM-dd").format(post[0]), Integer.parseInt(post[1].toString()));
        }
        postsCountByDateResponse.getPostsCountByDate().putAll(postsCountByDate);
        return postsCountByDateResponse;
    }

    public PostResponseById getPostById(Integer id){
        List<Object[]> postData = postRepository.findPostById(id, new Date());
        Post post =(Post) postData.get(0)[0];
        PostResponseById postResponseById = new PostResponseById();
        postResponseById.setId(post.getId());
        postResponseById.setActive(post.isActive());
        postResponseById.setTitle(post.getTittle());
        postResponseById.setText(post.getText());
        postResponseById.setViewCount(post.getCountOfView());
        postResponseById.setTimestamp(post.getPublicationTime().getTime() / 1000);
        postResponseById.getUser().setId(Integer.parseInt(postData.get(0)[1].toString()));
        postResponseById.getUser().setName(postData.get(0)[2].toString());
        postResponseById.setLikeCount(Integer.parseInt(postData.get(0)[3].toString()));
        postResponseById.setDislikeCount(Integer.parseInt(postData.get(0)[4].toString()));

        List<Object[]> comments = postCommentRepository.findAllByPostId(id);
        for (Object[] object: comments){
            PostComment postComment = (PostComment) object[0];
            User user = (User) object[1];
            CommentResponse commentResponse = new CommentResponse();
            commentResponse.setId(postComment.getId());
            commentResponse.setText(postComment.getText());
            commentResponse.setTimestamp(postComment.getCommentTime().getTime() / 1000);

            commentResponse.getUser().setId(user.getId());
            commentResponse.getUser().setName(user.getName());
            commentResponse.getUser().setPhoto(user.getPhotoLink());
            postResponseById.getComments().add(commentResponse);
        }

        List<String> tags = tagToPostRepository.findTagsByPostId(id);
        postResponseById.getTags().addAll(tags);
        return postResponseById;
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
