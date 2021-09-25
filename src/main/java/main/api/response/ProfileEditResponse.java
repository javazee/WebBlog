package main.api.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.HashMap;

@Data
public class ProfileEditResponse {

    private boolean result;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    HashMap<String, String> errors = new HashMap<>();
}
