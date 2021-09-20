package main.api.response.postsResponse;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ListOfPostResponse {

    private long count ;

    @JsonProperty("posts")
    private List<PostInfoResponse> posts = new ArrayList<>();

}
