package main.api.response.authCheckResponse;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.HashMap;

@Data
public class RegistrationErrorResponse extends RegistrationResponse {

    private boolean result;

    @JsonProperty(value = "errors")
    private HashMap<String, String> invalidData = new HashMap<>();

}
