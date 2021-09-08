package main.api.response.postsResponse;

import lombok.Data;

@Data
public class PostResponse {
    private int id;

    private long timestamp;

    private AuthorOfPost user = new AuthorOfPost();

    private String title;

    private String announce;

    private int likeCount;

    private int dislikeCount;

    private int commentCount;

    private int viewCount;

}