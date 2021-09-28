package main.controller;

import main.api.request.ChangePasswordRequest;
import main.api.request.LoginRequest;
import main.api.request.RegistrationRequest;
import main.api.request.RestoreRequest;
import main.api.response.LogoutResponse;
import main.api.response.authCheckResponse.AuthResponse;
import main.api.response.authCheckResponse.CaptchaResponse;
import main.api.response.loginResponse.LoginResponse;
import main.model.repository.SettingsRepository;
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
    private final SettingsRepository settingsRepository;

    @Autowired
    public ApiAuthController(AuthCheckService service, SettingsRepository settingsRepository) {
        this.service = service;
        this.settingsRepository = settingsRepository;
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
    protected ResponseEntity<AuthResponse> register(@RequestBody RegistrationRequest registrationRequest){
        if (settingsRepository.findByCode("MULTIUSER_MODE").getValue().equals("NO"))
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        return ResponseEntity.ok().body(service.checkFormData(registrationRequest));
    }

    @PostMapping("/login")
    protected ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest){
        return ResponseEntity.ok(service.login(loginRequest));
    }

    @GetMapping("/logout")
    protected ResponseEntity<LogoutResponse> logout(){
        return ResponseEntity.ok(service.logout());
    }

    @PostMapping("/password")
    protected ResponseEntity<AuthResponse> changePassword(@RequestBody ChangePasswordRequest changePasswordRequest){
        return ResponseEntity.ok(service.changePassword(changePasswordRequest));
    }

    @PostMapping(value = "/restore")
    protected AuthResponse restore(@RequestBody RestoreRequest request){
        return service.restore(request.getEmail());
    }
}
