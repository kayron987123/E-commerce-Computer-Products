package org.gad.ecommerce_computer_components.configuration.security;

import io.jsonwebtoken.security.Keys;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.nio.charset.StandardCharsets;
import java.security.Key;

public class Constants {
    //Constantes para Spring Security
    public static final String LOGIN_URL = "/api/auth/login";
    public static final String HEADER_AUTHORIZATION_KEY = "Authorization";
    public static final String TOKEN_BEARER_PREFIX = "Bearer ";

    //Constantes para JWT
    public static final String SUPER_SECRET_KEY = "ZnJhc2VzbGFyZ2FzcGFyYWNvbG9jYXJjb21vY2xhdmVlbnVucHJvamVjdG9kZWVtZXBsb3BhcmFqd3Rjb25zcHJpbmdzZWN1cml0eQbWlwcnVlYmFkZWVgYGFyYWJhc2U2MxMjM0NTY3ODk=";
    public static final long TOKEN_EXPIRATION_TIME = 1_200_000; // 20 minutos

    public static Key getSigningKey(String secretKey) {
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public static String encryptPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt(10));
    }

    public static boolean verifyPassword(String rawPassword, String hashedPassword) {
        return BCrypt.checkpw(rawPassword, hashedPassword);
    }

    public static void main(String[] args) {
        String encryptedPassword = encryptPassword("test123456");
        System.out.println(encryptedPassword);
    }
}
