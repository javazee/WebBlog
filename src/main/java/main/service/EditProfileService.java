package main.service;

import com.cloudinary.Cloudinary;
import main.api.response.ProfileEditResponse;
import main.model.User;
import main.model.repository.UserRepository;
import org.imgscalr.Scalr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class EditProfileService {

    private final UserRepository userRepository;

    @Value("${cloadinary.cloud_name}")
    private String cloadinaryName;

    @Value("${cloadinary.api_key}")
    private String cloadinaryApiKey;

    @Value("${cloadinary.api_secret}")
    private String cloadinaryApiSecret;

    @Autowired
    public EditProfileService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public ProfileEditResponse editProfile(MultipartFile photo,
                                           String name,
                                           String email,
                                           String password,
                                           int removePhoto){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(username).get();
        ProfileEditResponse response = new ProfileEditResponse();
        if (name != null) {
            if (name.length() < 3) {
                response.getErrors().put("name", "Слишком короткое имя");
            } else if (!name.matches("[a-zA-Zа-яА-ЯЁё\\s]+")){
                response.getErrors().put("name", "Имя содержит недопустимые символы");
            } else {
                user.setName(name);
            }
        }
        if (email != null) {
            if (email.matches("^([a-z0-9_-]+\\.)*[a-z0-9_-]+@[a-z0-9_-]+(\\.[a-z0-9_-]+)*\\.[a-z]{2,6}$")) {
                user.setEmail(email);
            } else {
                response.getErrors().put("email", "Некорректно введеный электронный адрес");
            }
        }
        if (password != null){
            String newPassword = new BCryptPasswordEncoder().
                    encode(password);
            user.setPassword(newPassword);
        }
        if (removePhoto == 1) {
            try {
                Files.delete(Path.of(user.getPhotoLink()));
                user.setPhotoLink(null);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (response.getErrors().isEmpty() && photo != null) {
            try {
                ByteArrayInputStream bis = new ByteArrayInputStream(photo.getBytes());
                BufferedImage image = ImageIO.read(bis);
                int width = image.getWidth();
                int height = image.getHeight();
                int minSide = Math.min(width, height);
                int x = (width - minSide) / 2;
                int y = (height - minSide) / 2;
                BufferedImage cutImage = image.getSubimage(x, y, minSide, minSide);
                BufferedImage newImage = Scalr.resize(cutImage,  36);
                Map<String, String> config = new HashMap<>();
                config.put("cloud_name", cloadinaryName);
                config.put("api_key", cloadinaryApiKey);
                config.put("api_secret", cloadinaryApiSecret);
                Cloudinary cloudinary = new Cloudinary(config);
                HashMap<String, String> param = new HashMap<>();
                String path = generatePath();
                param.put("public_id", path);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(newImage, "jpg", baos);
                cloudinary.uploader().upload(baos.toByteArray(), param);
                user.setPhotoLink(cloudinary.url().generate(path + ".jpg"));
            } catch (IOException e) {
                response.getErrors().put("photo", "Ошибка при обработке фотографии");
                e.printStackTrace();
            } catch (NullPointerException e) {
                response.getErrors().put("photo", "Неверный формат фотографии");
                e.printStackTrace();
            }
        }
        if (response.getErrors().isEmpty()){
            userRepository.save(user);
            response.setResult(true);
        }
        return response;
    }

    private static String generatePath() throws UnknownHostException {
        String characters = "abcdefghijklmnopqrstuvwxyz1234567890";
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 12; i++) {
            sb.append(characters.charAt(random.nextInt(characters.length())));
        }
        return sb.toString();
    }

}
