package main.api.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class RegistrationRequest {

    @JsonProperty("e_mail")
    private String email;

    private String password;

    private String name;

    private String captcha;

    @JsonProperty("captcha_secret")
    private String secretCode;
}
