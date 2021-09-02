package main.model;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "posts")
public class Post {

    @Id
    @Column(nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column(name = "is_active", columnDefinition = "TINYINT(1) NOT NULL")
    private boolean isActive;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "enum('NEW' , 'ACCEPTED', 'DECLINED') NOT NULL")
    private ModerationStatus moderationStatus;

    @ManyToMany
    @JoinTable(name = "moderator_posts",
            joinColumns = @JoinColumn (name = "post_id"),
            inverseJoinColumns = @JoinColumn (name = "moderator_id"))
    private List<User> moderators;

    @ManyToOne
    @JoinColumn(name = "user_id", columnDefinition = "INT NOT NULL")
    private User user;

    @Column(name = "time", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date publicationTime;

    @Column(columnDefinition = "VARCHAR(255) NOT NULL")
    private String tittle;

    @Column(columnDefinition = "TEXT NOT NULL")
    private String text;

    @Column(name = "view_count", columnDefinition = "INT NOT NULL")
    private int countOfView;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public ModerationStatus getModerationStatus() {
        return moderationStatus;
    }

    public void setModerationStatus(ModerationStatus moderationStatus) {
        this.moderationStatus = moderationStatus;
    }

    public List<User> getModerators() {
        return moderators;
    }

    public void setModerators(List<User> moderators) {
        this.moderators = moderators;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Date getPublicationTime() {
        return publicationTime;
    }

    public void setPublicationTime(Date publicationTime) {
        this.publicationTime = publicationTime;
    }

    public String getTittle() {
        return tittle;
    }

    public void setTittle(String tittle) {
        this.tittle = tittle;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getCountOfView() {
        return countOfView;
    }

    public void setCountOfView(int countOfView) {
        this.countOfView = countOfView;
    }

}

enum ModerationStatus{
    NEW, ACCEPTED, DECLINED
}
