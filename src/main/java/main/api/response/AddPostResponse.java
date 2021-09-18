package main.api.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.HashMap;

@Data
public class AddPostResponse {

    private boolean result;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonProperty(value = "errors")
    private HashMap<String, String> invalidData = new HashMap<>();
}
