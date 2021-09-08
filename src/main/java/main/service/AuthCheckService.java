package main.service;

import main.api.response.authCheckResponse.AuthCheckResponse;
import main.model.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthCheckService {

    @Autowired
    UserRepository userRepository;

    AuthCheckResponse authCheckResponse;

    public AuthCheckResponse authCheck(){
        return new AuthCheckResponse();
    }
}
