package main.service;

import com.cloudinary.Cloudinary;
import org.imgscalr.Scalr;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class ImageService {

    @Value("${cloadinary.cloud_name}")
    private String cloadinaryName;

    @Value("${cloadinary.api_key}")
    private String cloadinaryApiKey;

    @Value("${cloadinary.api_secret}")
    private String cloadinaryApiSecret;

    public String loadImage(byte[] byteImage, String extension) throws IOException {
        BufferedImage image = ImageIO.read(new ByteArrayInputStream(byteImage));
        int height = image.getHeight();
        int width = image.getWidth();
        if (height > 480 || width > 640){
            if (height > width) {
                image = Scalr.resize(image, Scalr.Mode.FIT_TO_HEIGHT, 480);
            } else {
                image = Scalr.resize(image, Scalr.Mode.FIT_TO_WIDTH, 640);
            }
        }
        Map<String, String> config = new HashMap<>();
        config.put("cloud_name", cloadinaryName);
        config.put("api_key", cloadinaryApiKey);
        config.put("api_secret", cloadinaryApiSecret);
        Cloudinary cloudinary = new Cloudinary(config);
        HashMap<String, String> param = new HashMap<>();
        String path = generatePath();
        param.put("public_id", path);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, extension, baos);
        cloudinary.uploader().upload(baos.toByteArray(), param);
        return cloudinary.url().generate(path + "." + extension);
    }

    private static String generatePath() {
        String characters = "abcdefghijklmnopqrstuvwxyz1234567890";
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 18; i++) {
            sb.append(characters.charAt(random.nextInt(characters.length())));
        }
        return sb.toString();
    }
}
