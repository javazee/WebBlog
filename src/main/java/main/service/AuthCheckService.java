package main.service;

import com.github.cage.Cage;
import com.github.cage.GCage;
import main.api.request.RegistrationRequest;
import main.api.response.authCheckResponse.RegistrationErrorResponse;
import main.api.response.authCheckResponse.CaptchaResponse;
import main.api.response.authCheckResponse.AuthCheckResponse;
import main.api.response.authCheckResponse.RegistrationResponse;
import main.model.CaptchaCode;
import main.model.User;
import main.model.repository.CaptchaRepository;
import main.model.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.Date;
import java.util.Objects;

@Service
public class AuthCheckService {

    @Value("${captcha.cleaning}")
    private int interval;

    private final UserRepository userRepository;

    private final CaptchaRepository captchaRepository;

    @Autowired
    public AuthCheckService(UserRepository userRepository,
                            CaptchaRepository captchaRepository) {
        this.userRepository = userRepository;
        this.captchaRepository = captchaRepository;
    }

    public AuthCheckResponse authCheck(){
        return new AuthCheckResponse();
    }

    public CaptchaResponse getCaptcha(){
        try {
            captchaRepository.deleteOldCaptcha(new Date(new Date().getTime() - interval * 60000L));
            GCage gCage = new GCage();
            String code = gCage.getTokenGenerator().next();
            String image = generateImage(gCage, code);
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
        } catch (IOException ex){
            ex.printStackTrace();
            return new CaptchaResponse();
        }
    }

    public RegistrationResponse checkFormData(RegistrationRequest registrationRequest){
        captchaRepository.deleteOldCaptcha(new Date(new Date().getTime() - interval * 60000L));
        RegistrationErrorResponse response = null;
        if (userRepository.existsByEmail(registrationRequest.getEmail())) {
            response= new RegistrationErrorResponse();
            response.getInvalidData().put("email", "Этот e-mail уже зарегистрирован");
        }
        if (!Objects.equals(captchaRepository.getCodeBySecretCode(registrationRequest.getSecretCode()),
                registrationRequest.getCaptcha())){
            if (response == null) response= new RegistrationErrorResponse();
            response.getInvalidData().put("captcha", "Код с картинки введён неверно");
        }
        if (response == null) {
            User user = new User();
            user.setName(registrationRequest.getName());
            user.setEmail(registrationRequest.getEmail());
            user.setPassword(registrationRequest.getPassword());
            user.setRegistrationDate(new Date());
            userRepository.save(user);
            return new RegistrationResponse();
        }
        return response;
    }

    String generateImage(Cage cage, String code) throws IOException {
        String path = "temp/" + code;
        try (OutputStream os = new FileOutputStream(path, false)) {
            cage.draw(code, os);
            byte[] fileContent = Files.readAllBytes(Path.of(path));
            String imageCode = Base64.getEncoder().encodeToString(fileContent);
            File file = new File(path);
            os.flush();
            os.close();
            file.delete();
            return imageCode;
        }
    }

    String generateSecretCode() {
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
}
