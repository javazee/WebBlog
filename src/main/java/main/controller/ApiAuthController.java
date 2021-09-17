package main.controller;

import main.api.request.LoginRequest;
import main.api.request.RegistrationRequest;
import main.api.response.LogoutResponse;
import main.api.response.loginResponse.LoginResponse;
import main.api.response.authCheckResponse.CaptchaResponse;
import main.api.response.authCheckResponse.RegistrationResponse;
import main.service.AuthCheckService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/auth")
public class ApiAuthController {

    private final AuthCheckService service;

    @Autowired
    public ApiAuthController(AuthCheckService service) {
        this.service = service;
    }

    @GetMapping("/check")
    protected ResponseEntity<LoginResponse> check(Principal principal){
        if (principal == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        return ResponseEntity.ok(service.check(principal.getName()));
    }

    @GetMapping("/captcha")
    protected CaptchaResponse getCaptcha(){
        return service.getCaptcha();
    }


    @PostMapping("/register")
    protected RegistrationResponse register(@RequestBody RegistrationRequest registrationRequest){
        return service.checkFormData(registrationRequest);
    }

    @PostMapping("/login")
    protected ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest){
        return ResponseEntity.ok(service.login(loginRequest));
    }

    @GetMapping("/logout")
    protected ResponseEntity<LogoutResponse> logout(){
        return ResponseEntity.ok(service.logout());
    }
}
