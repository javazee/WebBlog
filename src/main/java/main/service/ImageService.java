package main.service;

import com.cloudinary.Cloudinary;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
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

    public String loadImage(byte[] image) throws IOException {
        Map<String, String> config = new HashMap<>();
        config.put("cloud_name", cloadinaryName);
        config.put("api_key", cloadinaryApiKey);
        config.put("api_secret", cloadinaryApiSecret);
        Cloudinary cloudinary = new Cloudinary(config);
        HashMap<String, String> param = new HashMap<>();
        String path = generatePath();
        param.put("public_id", path);
        cloudinary.uploader().upload(image, param);
        return cloudinary.url().generate(path + ".jpg");
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
