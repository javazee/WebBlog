package main.service;

import main.api.response.AddOrEditPostResponse;
import main.api.response.postsResponse.*;
import main.model.*;
import main.model.enums.ModerationStatus;
import main.model.repository.*;
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

    private final TagRepository tagRepository;

    private final UserRepository userRepository;

    @Autowired
    public PostService(PostRepository postRepository,
                       PostCommentRepository postCommentRepository,
                       TagToPostRepository tagToPostRepository,
                       TagRepository tagRepository,
                       UserRepository userRepository) {
        this.postRepository = postRepository;
        this.postCommentRepository = postCommentRepository;
        this.tagToPostRepository = tagToPostRepository;
        this.tagRepository = tagRepository;
        this.userRepository = userRepository;
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
        Optional<Post> post = postRepository.findById(postId);
        if (post.isPresent()){
            Post moderatedPost = post.get();
            moderatedPost.setModerationStatus(decision.equals("decline") ? ModerationStatus.DECLINED : ModerationStatus.ACCEPTED);
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            Optional<User> user = userRepository.findByEmail(email);
            moderatedPost.addModerator(user.get());
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
            String authName = SecurityContextHolder.getContext().getAuthentication().getName();
            Optional<User> user = userRepository.findByEmail(authName);
            if (user.get().getId() != post.getUser().getId() && !user.get().isModerator()) {
                post.setCountOfView(post.getCountOfView() + 1);
                postRepository.save(post);
            }
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

    public AddOrEditPostResponse createOrUpdatePost(long timestamp, int active, String title, List<String> tags, String text,  Integer id){
        AddOrEditPostResponse response = new AddOrEditPostResponse();
        Post post;
        if (id == null){
            post = new Post();
        } else {
            Optional<Post> optional = postRepository.findById(id);
            if (optional.isPresent()) {
                post = optional.get();
            } else {
                response.getInvalidData().put("id", "Пост по указанному идентификатору не найден");
                return response;
            }
        }
        if (title.length() < 3 || text.length() < 50){
            if (title.length() < 3) response.getInvalidData().put("title", "Заголовок не установлен");
            if (text.length() < 50) response.getInvalidData().put("text", "Текст публикации слишком короткий");
            return response;
        }
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<User> user = userRepository.findByEmail(email);
        post.setUser(user.get());
        if (id == null) {
            post.setModerationStatus(ModerationStatus.NEW);
        } else if (!user.get().isModerator()){
            post.setModerationStatus(ModerationStatus.NEW);
        }
        Date currentTime = new Date();
        post.setPublicationTime((timestamp < currentTime.getTime() / 1000) ? currentTime : new Date(timestamp * 1000));
        post.setTittle(title);
        post.setText(text);
        post.setActive(active == 1);
        postRepository.save(post);
        if (id != null) tagToPostRepository.deleteAllByPost(post);
        for (String tagText : tags){
            Optional<Tag> tag = tagRepository.findByText(tagText);
            TagToPost tagToPost = new TagToPost();
            if (tag.isPresent()){
                tagToPost.setTag(tag.get());
                tagToPost.setPostId(post);
            } else {
                Tag newTag = new Tag();
                newTag.setText(tagText);
                tagToPost.setTag(newTag);
                tagToPost.setPostId(post);
                tagRepository.save(newTag);
            }
            tagToPostRepository.save(tagToPost);
        }
        response.setResult(true);
        return response;
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
            PostInfoResponse postInfoResponse = new PostInfoResponse();
            postInfoResponse.setId(post.getId());
            postInfoResponse.setTimestamp(post.getPublicationTime().getTime() / 1000);
            postInfoResponse.setTitle(post.getTittle());
            postInfoResponse.setAnnounce(createAnnounce(post.getText()));
            postInfoResponse.setViewCount(post.getCountOfView());
            postInfoResponse.setCommentCount(post.getComments().size());
            int likeCount = (int) post.getLikes().stream().filter(p -> p.getValue() == 1).count();
            int dislikeCount = (int) post.getLikes().stream().filter(p -> p.getValue() == -1).count();
            postInfoResponse.setLikeCount(likeCount);
            postInfoResponse.setDislikeCount(dislikeCount);
            postInfoResponse.getUser().setId(post.getUser().getId());
            postInfoResponse.getUser().setName(post.getUser().getName());
            listOfPost.getPosts().add(postInfoResponse);
        }
        return listOfPost;
    }
}
