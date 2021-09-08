package main.controller;

import main.api.response.authCheckResponse.AuthCheckResponse;
import main.service.AuthCheckService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth/")
public class ApiAuthController {

    private final AuthCheckService service;


    public ApiAuthController(AuthCheckService service) {
        this.service = service;
    }

    @GetMapping("/check")
    private AuthCheckResponse authCheck(){
        return service.authCheck();
    }
}
