package main.api.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.HashMap;

@Data
public class AddCommentResponse {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer id;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Boolean result;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private HashMap<String, String> errors = new HashMap<>();
}
