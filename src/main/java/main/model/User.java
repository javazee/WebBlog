package main.model;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "users")
public class User {

    @Id
    @Column(nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column(nullable = false, columnDefinition = "TINYINT(1) NOT NULL")
    private Boolean isModerator;

    @Column(name = "reg_time", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date registrationDate;

    @Column(columnDefinition = "VARCHAR(255) NOT NULL")
    private String name;

    @Column(columnDefinition = "VARCHAR(255) NOT NULL")
    private String password;

    @Column(name = "code", columnDefinition = "VARCHAR(255) NOT NULL")
    private String recoveryCode;

    @Column(columnDefinition = "VARCHAR(255) NOT NULL")
    private String email;

    @Column(name = "photo")
    private String photoLink;

    @ManyToMany(mappedBy = "moderators")
    private List<Post> moderatedPosts;

    public List<Post> getModeratedPosts() {
        return moderatedPosts;
    }

    public void setModeratedPosts(List<Post> moderatedPosts) {
        this.moderatedPosts = moderatedPosts;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Boolean getModerator() {
        return isModerator;
    }

    public void setModerator(Boolean moderator) {
        isModerator = moderator;
    }

    public Date getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(Date registrationDate) {
        this.registrationDate = registrationDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRecoveryCode() {
        return recoveryCode;
    }

    public void setRecoveryCode(String recoveryCode) {
        this.recoveryCode = recoveryCode;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhotoLink() {
        return photoLink;
    }

    public void setPhotoLink(String photoLink) {
        this.photoLink = photoLink;
    }
}
