package main.api.response.postsResponse;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Data
public class PostsCountByDateResponse {

    private List<Integer> years = new ArrayList<>();

    @JsonProperty("posts")
    private HashMap<String, Integer> postsCountByDate = new HashMap<>();
}

