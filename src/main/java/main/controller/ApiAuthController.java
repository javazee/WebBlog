package main.controller;

import main.api.request.RegistrationRequest;
import main.api.response.authCheckResponse.CaptchaResponse;
import main.api.response.authCheckResponse.AuthCheckResponse;
import main.api.response.authCheckResponse.RegistrationResponse;
import main.service.AuthCheckService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class ApiAuthController {

    private final AuthCheckService service;


    public ApiAuthController(AuthCheckService service) {
        this.service = service;
    }

    @GetMapping("/check")
    private AuthCheckResponse authCheck(){
        return service.authCheck();
    }

    @GetMapping("/captcha")
    private CaptchaResponse getCaptcha(){
        return service.getCaptcha();
    }


    @PostMapping("/register")
    private RegistrationResponse register(@RequestBody RegistrationRequest registrationRequest){
        return service.checkFormData(registrationRequest);
    }
}
