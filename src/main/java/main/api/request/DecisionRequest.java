package main.api.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class DecisionRequest {

    @JsonProperty("post_id")
    private int id;

    private String decision;
}
