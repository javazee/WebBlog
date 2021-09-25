package main.service;

import main.api.response.ProfileEditResponse;
import main.model.User;
import main.model.repository.UserRepository;
import org.imgscalr.Scalr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Random;

@Service
public class EditProfileService {

    private final UserRepository userRepository;

    @Autowired
    public EditProfileService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public ProfileEditResponse editProfile(MultipartFile photo,
                                           String name,
                                           String email,
                                           String password,
                                           int removePhoto) throws IOException {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(username).get();
        ProfileEditResponse response = new ProfileEditResponse();
        if (name != null) {
            if (name.length() < 3) {
                response.getErrors().put("name", "Слишком короткое имя");
            } else if (name.matches("[a-zA-Zа-яА-ЯЁё]+]")){
                response.getErrors().put("name", "Имя содержит недопустимые символы");
            } else {
                user.setName(name);
            }
        }
        if (email != null) {
            if (email.matches("^([a-z0-9_-]+\\.)*[a-z0-9_-]+@[a-z0-9_-]+(\\.[a-z0-9_-]+)*\\.[a-z]{2,6}$")) {
                user.setEmail(email);
            } else {
                response.getErrors().put("email", "Некорректный введеный электронный адрес");
            }
        }
        if (password != null){
            String newPassword = new BCryptPasswordEncoder().
                    encode(password);
            user.setPassword(newPassword);
        }
        if (removePhoto == 1) user.setPhotoLink(null);
        if (photo != null) {
            ByteArrayInputStream bis = new ByteArrayInputStream(photo.getBytes());
            BufferedImage image = ImageIO.read(bis);
            int width = image.getWidth();
            int height = image.getHeight();
            int minSide = Math.min(width, height);
            int x = (width - minSide) / 2;
            int y = (height - minSide) / 2;
            BufferedImage cutImage = image.getSubimage(x, y, minSide, minSide);
            BufferedImage newImage = Scalr.resize(cutImage,  36);
            String path = generatePath();
            File file = new File(path);
            file.mkdirs();
            user.setPhotoLink("file:///" + file.getAbsolutePath());
            ImageIO.write(newImage, "jpg", file);
        }
        userRepository.save(user);
        if (response.getErrors().isEmpty()) response.setResult(true);
        return response;
    }

    private static String generatePath() throws UnknownHostException {
        String characters = "abcdefghijklmnopqrstuvwxyz1234567890";
        StringBuilder sb = new StringBuilder("profilePhoto/");
        Random random = new Random();
        for (int i = 0; i < 12; i++) {
            sb.append(characters.charAt(random.nextInt(characters.length())));
        }
        sb.append(".jpg");
        return sb.toString();
    }

}
