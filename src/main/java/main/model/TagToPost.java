package main.model;

import javax.persistence.*;

@Entity
@Table (name = "tag2post")
public class TagToPost {

    @Id
    @Column(nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @OneToOne
    @JoinColumn(name = "tag_id", nullable = false)
    private Tag tag;

    @OneToOne
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Tag getTag() {
        return tag;
    }

    public void setTag(Tag tag) {
        this.tag = tag;
    }

    public Post getPost() {
        return post;
    }

    public void setPostId(Post post) {
        this.post = post;
    }
}
