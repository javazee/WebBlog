package main.api.response.loginResponse;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class LoginResponse {

    private boolean result;

    @JsonProperty("user")
    private UserLoginResponse userLoginResponse;
}
