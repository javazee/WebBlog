package main.service;

import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.Random;

@Service
public class ImageService {

    public String loadImage(byte[] image) throws IOException {
        ByteArrayInputStream bis = new ByteArrayInputStream(image);
        BufferedImage bImage2 = ImageIO.read(bis);
        String path = generatePath();
        File file = new File(path);
        file.mkdirs();
        ImageIO.write(bImage2, "jpg", file);
        return path;
    }

    private static String generatePath() {
        String characters = "abcdefghijklmnopqrstuvwxyz1234567890";
        StringBuilder sb = new StringBuilder("upload/");
        Random random = new Random();
        for (int i = 0; i < 18; i++) {
            if (i == 3 || i == 7 || i == 11) {
                sb.append("/");
                continue;
            }
            sb.append(characters.charAt(random.nextInt(characters.length())));
        }
        sb.append(".jpg");
        return sb.toString();
    }
}
