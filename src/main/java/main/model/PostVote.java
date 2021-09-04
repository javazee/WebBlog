package main.model;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table (name = "post_votes")
public class PostVote {

    @Id
    @Column(nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @OneToOne
    @JoinColumn(name = "user_id", columnDefinition = "INT NOT NULL")
    private User user;

    @OneToOne
    @JoinColumn(name = "post_id", columnDefinition = "INT NOT NULL")
    private Post post;

    @Column(name = "time", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date voteTime;

    @Column(columnDefinition = "TINYINT NOT NULL")
    private short value;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public Date getVoteTime() {
        return voteTime;
    }

    public void setVoteTime(Date voteTime) {
        this.voteTime = voteTime;
    }

    public short getValue() {
        return value;
    }

    public void setValue(short value) {
        this.value = value;
    }

}
