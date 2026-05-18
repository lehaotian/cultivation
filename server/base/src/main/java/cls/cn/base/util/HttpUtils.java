package cls.cn.base.util;

import com.google.common.base.Joiner;
import org.springframework.http.HttpHeaders;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HttpUtils {
    public static HttpHeaders createHeader(Object request) {
        long timestamp = System.currentTimeMillis();
        String checksum = generateChecksum(request, timestamp, "f95ei1xfwrs5x2u73864wa6fk1ez91l0");
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        headers.set("platform-auth-version", "v3");
        headers.set("content-encrypt-type", "v3");
        headers.set("platform-auth-timestamp", String.valueOf(timestamp));
        headers.set("platform-auth-key-id", "2000011612");
        headers.set("platform-auth-checksum", checksum);
        return headers;
    }

    public static String generateChecksum(Object request, long timestamp, String secretKey) {
        String requestBody = JsonUtils.toJson(request);
        Joiner joiner = Joiner.on("&").skipNulls();
        String plainText = joiner.join(requestBody, timestamp, secretKey);
        return md5Hash(plainText);
    }

    private static String md5Hash(String plainText) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hashInBytes = md.digest(plainText.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hashInBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5 algorithm not found", e);
        }
    }
}