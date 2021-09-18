package main.model;

import main.model.enums.Role;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "users")
public class User {

    @Id
    @Column(nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "is_moderator", columnDefinition = "TINYINT(1) NOT NULL")
    private boolean isModerator;

    @Column(name = "reg_time", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date registrationDate;

    @Column(columnDefinition = "VARCHAR(255) NOT NULL")
    private String name;

    @Column(columnDefinition = "VARCHAR(255) NOT NULL")
    private String password;

    @Column(name = "code", columnDefinition = "VARCHAR(255)")
    private String recoveryCode;

    @Column(columnDefinition = "VARCHAR(255) NOT NULL")
    private String email;

    @Column(name = "photo")
    private String photoLink;

    @ManyToMany(mappedBy = "moderators")
    private List<Post> moderatedPosts = new ArrayList<>();

    public User(Boolean isModerator, Date registrationDate, String name, String password, String email) {
        this.isModerator = isModerator;
        this.registrationDate = registrationDate;
        this.name = name;
        this.password = password;
        this.email = email;
    }

    public User() {}

    public Role getRole(){
        return isModerator ? Role.MODERATOR : Role.USER;
    }

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

    public boolean isModerator() {
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

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", isModerator=" + isModerator +
                ", registrationDate=" + registrationDate +
                ", name='" + name + '\'' +
                ", password='" + password + '\'' +
                ", recoveryCode='" + recoveryCode + '\'' +
                ", email='" + email + '\'' +
                ", photoLink='" + photoLink + '\'' +
                '}';
    }
}
