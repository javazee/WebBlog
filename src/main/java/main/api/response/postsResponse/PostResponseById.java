package main.api.response.postsResponse;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class PostResponseById {

    private int id;

    @JsonProperty("active")
    private boolean isActive;

    private long timestamp;

    private AuthorOfPost user = new AuthorOfPost();

    private String title;

    private String text;

    private int likeCount;

    private int dislikeCount;

    private int viewCount;

    List<CommentResponse> comments = new ArrayList<>();

    List<String> tags = new ArrayList<>();

}

