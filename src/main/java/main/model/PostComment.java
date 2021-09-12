package main.model;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table (name = "post_comments")
public class PostComment {
    @Id
    @Column(nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    private PostComment parentComment;

    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "time", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date commentTime;

    @Column(nullable = false)
    private String text;

    public PostComment(Post post, User user, Date commentTime, String text) {
        this.post = post;
        this.user = user;
        this.commentTime = commentTime;
        this.text = text;
    }

    public PostComment(){}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public PostComment getParentComment() {
        return parentComment;
    }

    public void setParentComment(PostComment parentComment) {
        this.parentComment = parentComment;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Date getCommentTime() {
        return commentTime;
    }

    public void setCommentTime(Date commentTime) {
        this.commentTime = commentTime;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
