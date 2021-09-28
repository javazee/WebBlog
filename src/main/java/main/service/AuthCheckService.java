package main.service;

import com.github.cage.GCage;
import main.api.request.ChangePasswordRequest;
import main.api.request.LoginRequest;
import main.api.request.RegistrationRequest;
import main.api.response.LogoutResponse;
import main.api.response.authCheckResponse.AuthResponse;
import main.api.response.authCheckResponse.CaptchaResponse;
import main.api.response.loginResponse.LoginResponse;
import main.api.response.loginResponse.UserLoginResponse;
import main.model.CaptchaCode;
import main.model.User;
import main.model.repository.CaptchaRepository;
import main.model.repository.PostRepository;
import main.model.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.*;

@Service
public class AuthCheckService {

    @Value("${captcha.cleaning}")
    private int interval;

    @Value("${appEmail.email}")
    private String emailFrom;


    private final JavaMailSender emailSender;

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CaptchaRepository captchaRepository;

    private final AuthenticationManager authenticationManager;

    @Autowired
    public AuthCheckService(JavaMailSender emailSender,
                            UserRepository userRepository,
                            PostRepository postRepository,
                            CaptchaRepository captchaRepository,
                            AuthenticationManager authenticationManager) {
        this.emailSender = emailSender;
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.captchaRepository = captchaRepository;
        this.authenticationManager = authenticationManager;
    }

    public CaptchaResponse getCaptcha(){
        captchaRepository.deleteOldCaptcha(new Date(new Date().getTime() - interval * 60000L));
        GCage gCage = new GCage();
        String code = gCage.getTokenGenerator().next();
        String image = Base64.getEncoder().encodeToString(gCage.draw(code));
        String secretCode = generateSecretCode();
        CaptchaCode captcha = new CaptchaCode();
        captcha.setGenerationTime(new Date());
        captcha.setCode(code);
        captcha.setSecretCode(secretCode);
        captchaRepository.save(captcha);
        CaptchaResponse captchaResponse = new CaptchaResponse();
        captchaResponse.setSecretCode(secretCode);
        captchaResponse.setImage("data:image/png;base64, " + image);
        return captchaResponse;
    }

    public AuthResponse checkFormData(RegistrationRequest registrationRequest){
        captchaRepository.deleteOldCaptcha(new Date(new Date().getTime() - interval * 60000L));
        AuthResponse response = new AuthResponse();
        if (userRepository.existsByEmail(registrationRequest.getEmail())) {
            response.getInvalidData().put("email", "Этот e-mail уже зарегистрирован");
        }
        if (!Objects.equals(captchaRepository.getCodeBySecretCode(registrationRequest.getSecretCode()),
                registrationRequest.getCaptcha())){
            response.getInvalidData().put("captcha", "Код с картинки введён неверно");
        }
        if (response.getInvalidData().isEmpty()) {
            User user = new User();
            user.setName(registrationRequest.getName());
            user.setEmail(registrationRequest.getEmail());
            user.setPassword(
                    new BCryptPasswordEncoder().
                            encode(registrationRequest.getPassword()));
            user.setRegistrationDate(new Date());
            userRepository.save(user);
            response.setResult(true);
        }
        return response;
    }

    public LoginResponse login(LoginRequest loginRequest){
        Authentication auth =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                loginRequest.getEmail(),
                                loginRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(auth);
        org.springframework.security.core.userdetails.User userAuth = (org.springframework.security.core.userdetails.User) auth.getPrincipal();
        return getLoginResponse(userAuth.getUsername());
    }

    public LogoutResponse logout(){
        SecurityContextHolder.clearContext();
        return new LogoutResponse();
    }

    public LoginResponse check(String email){
        return getLoginResponse(email);
    }

    public AuthResponse restore(String email){
        AuthResponse response = new AuthResponse();
        Optional<User> user = userRepository.findByEmail(email);
        try {
            if (user.isPresent()) {
                MimeMessage mimeMessage = emailSender.createMimeMessage();
                MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true);
                messageHelper.setFrom(emailFrom);
                messageHelper.setTo(email);
                messageHelper.setSubject("Recovery code");
                String code = generateRandomHexString();
                user.get().setRecoveryCode(code);
                userRepository.save(user.get());
                messageHelper.setText("To change your password, please " +
                        "<a href='http://localhost:8080/login/change-password/" + code + "'>click here</a>", true);
                emailSender.send(mimeMessage);
                response.setResult(true);
            }
        } catch (MessagingException ex){
            ex.printStackTrace();
        }
        return response;
    }

    public AuthResponse changePassword(ChangePasswordRequest request){
        captchaRepository.deleteOldCaptcha(new Date(new Date().getTime() - interval * 60000L));
        AuthResponse response = new AuthResponse();
        if (!captchaRepository.existsBySecretCode(request.getSecretCode())){
            response.getInvalidData().put("code", "Ссылка для восстановления пароля устарела. " +
                    "<a href='/login/restore-password'>Запросить ссылку снова</a>");
            return response;
        }
        if (!Objects.equals(captchaRepository.getCodeBySecretCode(request.getSecretCode()),
                request.getCaptcha())){
            response.getInvalidData().put("captcha", "Код с картинки введён неверно");
        }
        Optional<User> user = userRepository.findByRecoveryCode(request.getCode());
        if (user.isPresent()){
            user.get().setPassword(new BCryptPasswordEncoder().
                    encode(request.getPassword()));
            userRepository.save(user.get());
            response.setResult(true);
        }
        return response;
    }

    private String generateSecretCode() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ" +
                            "abcdefghijklmnopqrstuvxyz" +
                            "0123456789";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 20; i++){
            int index = (int) (characters.length() * Math.random());
            sb.append(characters.charAt(index));
        }
        return sb.toString();
    }

    public static String generateRandomHexString(){
        Random r = new Random();
        StringBuilder sb = new StringBuilder();
        while(sb.length() < 32){
            sb.append(Integer.toHexString(r.nextInt()));
        }
        return sb.toString();
    }

    private LoginResponse getLoginResponse(String email){
        User user = userRepository.findByEmail(
                email).orElseThrow(()->new UsernameNotFoundException(email + " not found"));
        UserLoginResponse userLoginResponse = new UserLoginResponse();
        userLoginResponse.setId(user.getId());
        userLoginResponse.setEmail(user.getEmail());
        userLoginResponse.setName(user.getName());
        userLoginResponse.setPhoto(user.getPhotoLink());
        userLoginResponse.setSettings(user.isModerator());
        userLoginResponse.setModeration(user.isModerator());
        userLoginResponse.setModerationCount(
                user.isModerator() ? postRepository.countPostsForModeration() : 0);
        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setUserLoginResponse(userLoginResponse);
        loginResponse.setResult(true);
        return loginResponse;
    }
}
