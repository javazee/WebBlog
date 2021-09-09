package main.service;

import main.api.response.authCheckResponse.AuthCheckResponse;
import main.model.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthCheckService {

    private final UserRepository userRepository;

    @Autowired
    public AuthCheckService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public AuthCheckResponse authCheck(){
        return new AuthCheckResponse();
    }
}
