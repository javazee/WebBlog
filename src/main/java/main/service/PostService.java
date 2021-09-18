package main.service;

import main.api.response.postsResponse.*;
import main.model.Post;
import main.model.PostComment;
import main.model.enums.ModerationStatus;
import main.model.repository.PostCommentRepository;
import main.model.repository.PostRepository;
import main.model.repository.TagToPostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class PostService {


    private final PostRepository postRepository;

    private final PostCommentRepository postCommentRepository;

    private final TagToPostRepository tagToPostRepository;

    @Autowired
    public PostService(PostRepository postRepository,
                       PostCommentRepository postCommentRepository,
                       TagToPostRepository tagToPostRepository) {
        this.postRepository = postRepository;
        this.postCommentRepository = postCommentRepository;
        this.tagToPostRepository = tagToPostRepository;
    }

    public ListOfPostResponse listPosts(int offset, int limit, String mode) {
        Page<Post> posts;
        int page = offset / limit;
        switch (mode) {
            case "early":
                posts = postRepository.getPostsOrderByTime(PageRequest.of(page, limit, Sort.by("publicationTime")));
                break;
            case "popular":
                posts = postRepository.getPostsOrderByCountOfComment(PageRequest.of(page, limit));
                break;
            case "best":
                posts = postRepository.getPostsOrderByCountOfLike(PageRequest.of(page, limit));
                break;
            default:
                posts = postRepository.getPostsOrderByTime(PageRequest.of(page, limit, Sort.by("publicationTime").descending()));
        }
        if (posts == null) return new ListOfPostResponse();
        ListOfPostResponse listOfPost = createPostsList(posts.getContent());
        listOfPost.setCount(posts.getTotalElements());
        return listOfPost;
    }

    public ListOfPostResponse searchPosts(int offset, int limit, String query){
        Page<Post> posts;
        int page = offset / limit;
        if (query == null || query.trim().isEmpty()){
            posts = postRepository.getPostsOrderByTime(PageRequest.of(page, limit, Sort.by("publicationTime").descending()));
        } else {
            posts = postRepository.searchPostsOrderByTimeDesc(query, PageRequest.of(page, limit, Sort.by("publicationTime").descending()));
        }
        if (posts == null) return new ListOfPostResponse();
        ListOfPostResponse listOfPost = createPostsList(posts.getContent());
        listOfPost.setCount(posts.getTotalElements());
        return listOfPost;
    }

    public ListOfPostResponse getPostsByDate(int offset, int limit, String date){
        Page<Post> posts;
        int page = offset / limit;
        try {
            Date publicationDate = new SimpleDateFormat("yyyy-MM-dd").parse(date);
            posts = postRepository.getPostsByDate(publicationDate, PageRequest.of(page, limit, Sort.by("publicationTime").descending()));
            if (posts == null) return new ListOfPostResponse();
            ListOfPostResponse listOfPost = createPostsList(posts.getContent());
            listOfPost.setCount(posts.getTotalElements());
            return listOfPost;
        } catch (ParseException ex){
            ex.printStackTrace();
            return new ListOfPostResponse();
        }
    }

    public ListOfPostResponse findMyPosts(int offset, int limit, String status){
        Page<Post> posts;
        int page = offset / limit;
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        switch (status) {
            case "pending":
                posts = postRepository.getMyPosts(PageRequest.of(page, limit, Sort.by("publicationTime")),
                        true,
                        ModerationStatus.NEW,
                        email);
                break;
            case "declined":
                posts = postRepository.getMyPosts(PageRequest.of(page, limit, Sort.by("publicationTime")),
                        true,
                        ModerationStatus.DECLINED,
                        email);
                break;
            case "published":
                posts = postRepository.getMyPosts(PageRequest.of(page, limit, Sort.by("publicationTime")),
                        true,
                        ModerationStatus.ACCEPTED, email);
                break;
            default:
                posts = postRepository.getMyPosts(PageRequest.of(page, limit, Sort.by("publicationTime")),
                        false,
                        ModerationStatus.NEW,
                        email);
        }
        if (posts.isEmpty()) return new ListOfPostResponse();
        ListOfPostResponse listOfPost = createPostsList(posts.getContent());
        listOfPost.setCount(posts.getTotalElements());
        return listOfPost;
    }

    public ListOfPostResponse getPostsForModeration(int offset, int limit, String status){
        Page<Post> posts;
        int page = offset / limit;
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        switch (status) {
            case "declined":
                posts = postRepository.findMyModeratedPosts(PageRequest.of(page, limit, Sort.by("publicationTime").descending()),
                        ModerationStatus.DECLINED, email);
                break;
            case "accepted":
                posts = postRepository.findMyModeratedPosts(PageRequest.of(page, limit, Sort.by("publicationTime").descending()),
                        ModerationStatus.ACCEPTED, email);
                break;
            default:
                posts = postRepository.findNewPostsForModeration(PageRequest.of(page, limit, Sort.by("publicationTime")));
        }
        if (posts.isEmpty()) return new ListOfPostResponse();
        ListOfPostResponse listOfPost = createPostsList(posts.getContent());
        listOfPost.setCount(posts.getTotalElements());
        return listOfPost;
    }



    public ListOfPostResponse getPostsByTag(int offset, int limit, String tag){
        int page = offset / limit;
        Page<Post> posts = postRepository.findPostsByTag(tag, PageRequest.of(page, limit));
        if (posts == null) return new ListOfPostResponse();
        ListOfPostResponse listOfPost = createPostsList(posts.getContent());
        listOfPost.setCount(posts.getTotalElements());
        return listOfPost;
    }

    public PostsCountByDateResponse getCountOfPostsByDate(Integer year){
        List<Integer> years = postRepository.getYears(new Date());
        HashMap<String, Integer> postsCountByDate = new HashMap<>();
        List<Object[]> posts = postRepository.getPostsCountByDate(year == null ? Calendar.getInstance().get(Calendar.YEAR) : year);
        PostsCountByDateResponse postsCountByDateResponse = new PostsCountByDateResponse();
        postsCountByDateResponse.getYears().addAll(years);
        for (Object[] post: posts){
            postsCountByDate.put(new SimpleDateFormat("yyyy-MM-dd").format(post[0]), Integer.parseInt(post[1].toString()));
        }
        postsCountByDateResponse.getPostsCountByDate().putAll(postsCountByDate);
        return postsCountByDateResponse;
    }

    public void moderatePost(int postId, String decision){
        Optional<Post> post = postRepository.findPostById(postId);
        if (post.isPresent()){
            Post moderatedPost = post.get();
            moderatedPost.setModerationStatus(decision.equals("decline") ? ModerationStatus.DECLINED : ModerationStatus.ACCEPTED);
            postRepository.save(moderatedPost);
        }
    }

    public PostResponseById getPostById(Integer id){
        Post post = postRepository.getPostById(id);
         if (post == null) return null;
        PostResponseById postResponseById = new PostResponseById();
        postResponseById.setId(post.getId());
        postResponseById.setActive(post.isActive());
        postResponseById.setTitle(post.getTittle());
        postResponseById.setText(post.getText());
        if (!(SecurityContextHolder.getContext().getAuthentication() instanceof AnonymousAuthenticationToken)) {
            post.setCountOfView(post.getCountOfView() + 1);
            postRepository.save(post);
        }
        postResponseById.setViewCount(post.getCountOfView());
        postResponseById.setTimestamp(post.getPublicationTime().getTime() / 1000);
        postResponseById.getUser().setId(post.getUser().getId());
        postResponseById.getUser().setName(post.getUser().getName());
        postResponseById.setLikeCount((int) post.getLikes().stream().filter(p -> p.getValue() == 1).count());
        postResponseById.setDislikeCount((int) post.getLikes().stream().filter(p -> p.getValue() == -1).count());
        List<PostComment> comments = postCommentRepository.findAllByPostId(id);
        for (PostComment comment: comments){
            CommentResponse commentResponse = new CommentResponse();
            commentResponse.setId(comment.getId());
            commentResponse.setText(comment.getText());
            commentResponse.setTimestamp(comment.getCommentTime().getTime() / 1000);

            commentResponse.getUser().setId(comment.getUser().getId());
            commentResponse.getUser().setName(comment.getUser().getName());
            commentResponse.getUser().setPhoto(comment.getUser().getPhotoLink());
            postResponseById.getComments().add(commentResponse);
        }
        List<String> tags = tagToPostRepository.findTagsByPostId(id);
        postResponseById.getTags().addAll(tags);
        return postResponseById;
    }


    private String createAnnounce(String text){
        String noHTMLText = text.replaceAll("\\<.*?\\>", "");
        if (noHTMLText.length() <= 150) return noHTMLText + "...";
        int count = 0;
        while (true){
            int index = noHTMLText.indexOf(" ", count + 1);
            if (index < 150) {
                count = index;
            } else return noHTMLText.substring(0, count) + "...";
        }
    }

    private ListOfPostResponse createPostsList(List<Post> posts){
        ListOfPostResponse listOfPost = new ListOfPostResponse();
        for (Post post : posts) {
            PostResponse postResponse = new PostResponse();
            postResponse.setId(post.getId());
            postResponse.setTimestamp(post.getPublicationTime().getTime() / 1000);
            postResponse.setTitle(post.getTittle());
            postResponse.setAnnounce(createAnnounce(post.getText()));
            postResponse.setViewCount(post.getCountOfView());
            postResponse.setCommentCount(post.getComments().size());
            int likeCount = (int) post.getLikes().stream().filter(p -> p.getValue() == 1).count();
            int dislikeCount = (int) post.getLikes().stream().filter(p -> p.getValue() == -1).count();
            postResponse.setLikeCount(likeCount);
            postResponse.setDislikeCount(dislikeCount);
            postResponse.getUser().setId(post.getUser().getId());
            postResponse.getUser().setName(post.getUser().getName());
            listOfPost.getPosts().add(postResponse);
        }
        return listOfPost;
    }
}
