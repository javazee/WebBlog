package main.api.response.authCheckResponse;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.HashMap;

@Data
public class RegistrationResponse{

    private boolean result;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private HashMap<String, String> invalidData = new HashMap<>();
}
