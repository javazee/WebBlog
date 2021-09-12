package main.api.response.postsResponse;

import lombok.Data;

@Data
public class CommentResponse {

    private int id;

    private long timestamp;

    private String text;

    private AuthorOfComment user = new AuthorOfComment();
}