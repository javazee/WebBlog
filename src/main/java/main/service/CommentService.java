package main.service;

import main.api.response.AddCommentResponse;
import main.model.Post;
import main.model.PostComment;
import main.model.User;
import main.model.repository.CommentRepository;
import main.model.repository.PostRepository;
import main.model.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
public class CommentService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;

    @Autowired
    public CommentService(PostRepository postRepository,
                          CommentRepository commentRepository,
                          UserRepository userRepository) {
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
    }

    public AddCommentResponse comment(int parentId, int postId, String text){
        System.out.println("parentId = " + parentId);
        System.out.println("postId =" + postId);
        Optional<Post> post = postRepository.findById(postId);
        Optional<PostComment> parentComment = commentRepository.findById(parentId);
        AddCommentResponse response = new AddCommentResponse();
        if (post.isEmpty()) {
            if (parentId != 0 && parentComment.isEmpty()){
                response.getErrors().put("message", "соответствующие комментарий и пост не существуют)");
                return response;
            }
            response.getErrors().put("message", "соответствующий пост не существует)");
            return response;
        }
        if (text.length() < 2) {
            response.setResult(false);
            response.getErrors().put("text", "Текст комментария не задан или слишком короткий");
            return response;
        }
        PostComment comment = new PostComment();
        comment.setCommentTime(new Date());
        comment.setPost(post.get());
        parentComment.ifPresent(comment::setParentComment);
        comment.setText(text);
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<User> user = userRepository.findByEmail(username);
        comment.setUser(user.get());
        commentRepository.save(comment);
        response.setId(comment.getId());
        return response;
    }
}
