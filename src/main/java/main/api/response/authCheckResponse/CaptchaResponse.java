package main.api.response.authCheckResponse;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CaptchaResponse {

    @JsonProperty(value = "secret")
    private String secretCode;

    private String image;
}
