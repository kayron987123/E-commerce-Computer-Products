package org.gad.ecommerce_computer_components.sevice.impl;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.SignatureException;
import jakarta.transaction.Transactional;
import org.gad.ecommerce_computer_components.configuration.security.JWTAuthenticationConfig;
import org.gad.ecommerce_computer_components.persistence.entity.UserEntity;
import org.gad.ecommerce_computer_components.persistence.enums.AccountStatement;
import org.gad.ecommerce_computer_components.persistence.repository.UserRepository;
import org.gad.ecommerce_computer_components.presentation.dto.UserDTO;
import org.gad.ecommerce_computer_components.presentation.dto.VerifyUserToken;
import org.gad.ecommerce_computer_components.sevice.interfaces.EmailService;
import org.gad.ecommerce_computer_components.sevice.interfaces.UserService;
import org.gad.ecommerce_computer_components.utils.mappers.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.gad.ecommerce_computer_components.configuration.security.Constants.*;

@Service
public class UserServiceImpl implements UserService {

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*-";
    private static final int TOKEN_LENGTH = 10;
    private static final List<String> VALID_EXTENSIONS = Arrays.asList("jpg", "jpeg", "png");
    private static final String USER_NOT_FOUND = "User not found";

    @Autowired
    private EmailService emailService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JWTAuthenticationConfig jwtAuthenticationConfig;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public UserDTO findByUsername(String username) {
        UserEntity user = userRepository.findByUsername(username);
        if(user == null) {
            throw new UsernameNotFoundException(USER_NOT_FOUND);
        }
        return UserMapper.INSTANCE.userEntityToUserDTO(user);
    }

    @Override
    public String authenticateUser(String username, String password){
        UserEntity userResult = userRepository.findByUsername(username);
        if (userResult == null) {
            throw new UsernameNotFoundException(USER_NOT_FOUND);
        }
        if (new BCryptPasswordEncoder().matches(password, userResult.getPassword())) {
            return jwtAuthenticationConfig.getJWTToken(userResult);
        }
        throw new UsernameNotFoundException("Invalid credentials");
    }

    @Override
    public void saveUserInRedis(UserDTO userDTO, String emailKey) {
        String token = generateToken();

        if(emailKey.equals("TOKEN")){
            emailService.sendEmailTemporaryKey(userDTO.getEmail(), token);
        } else if (emailKey.equals("UPDATE")){
            emailService.sendEmailTemporaryKeyUpdate(userDTO.getEmail(), token);
        }else {
            throw new RuntimeException("Invalid email key");
        }

        ValueOperations<String, Object> ops = redisTemplate.opsForValue();
        ops.set(token, userDTO, Duration.ofMinutes(1));
    }

    @Override
    public String generateToken() {
        SecureRandom random = new SecureRandom();
        StringBuilder token = new StringBuilder(TOKEN_LENGTH);

        for (int i = 0; i < TOKEN_LENGTH; i++) {
            int index = random.nextInt(CHARACTERS.length());
            token.append(CHARACTERS.charAt(index));
        }
        return token.toString();
    }

    @Override
    public String verifyUserToken(VerifyUserToken verifyUserToken) {
        ValueOperations<String, Object> ops = redisTemplate.opsForValue();
        Object userObj = ops.get(verifyUserToken.getToken());

        if (userObj instanceof UserDTO) {
            UserDTO userDTO = (UserDTO) userObj;
            this.saveUser(userDTO);
            String tokenJWT = this.authenticateUser(userDTO.getUsername(), userDTO.getPassword());
            redisTemplate.delete(verifyUserToken.getToken());
            return tokenJWT;
        }
        return null;
    }

    @Override
    @Transactional
    public UserDTO saveUser(UserDTO userDTO) {
        UserEntity userEntity = UserMapper.INSTANCE.userDTOToUserEntity(userDTO);
        if(userEntity.getPassword() != null && !userEntity.getPassword().startsWith("$2a$")){
            userEntity.setPassword(new BCryptPasswordEncoder().encode(userEntity.getPassword()));
        }
        UserEntity savedUser = this.userRepository.save(userEntity);
        return UserMapper.INSTANCE.userEntityToUserDTO(savedUser);
    }

    @Override
    public boolean isImageFile(String fileName) {
        String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        return VALID_EXTENSIONS.contains(fileExtension);
    }

    @Override
    public UserDTO findByEmail(String email) {
        UserEntity user = userRepository.findByEmail(email);
        return UserMapper.INSTANCE.userEntityToUserDTO(user);
    }

    @Override
    public Claims extractClaimsFromJWT(String tokenJWT) {
        try {
            // Elimina el prefijo "Bearer " si está presente
            if (tokenJWT.startsWith("Bearer ")) {
                tokenJWT = tokenJWT.substring(7);
            }

            // Log del token para debugging
            System.out.println("Token to parse: " + tokenJWT);

            return Jwts.parserBuilder()
                    .setSigningKey(getSigningKey(SUPER_SECRET_KEY))
                    .build()
                    .parseClaimsJws(tokenJWT)
                    .getBody();
        } catch (IllegalArgumentException e) {
            System.out.println("JWT claims string is empty: " + e.getMessage());
            throw new RuntimeException("JWT claims string is empty", e);
        } catch (ExpiredJwtException e) {
            System.out.println("JWT token has expired: " + e.getMessage());
            throw new RuntimeException("JWT token has expired", e);
        } catch (UnsupportedJwtException e) {
            System.out.println("Unsupported JWT token: " + e.getMessage());
            throw new RuntimeException("Unsupported JWT token", e);
        } catch (MalformedJwtException e) {
            System.out.println("Invalid JWT token: " + e.getMessage());
            throw new RuntimeException("Invalid JWT token", e);
        } catch (SignatureException e) {
            System.out.println("Invalid JWT signature: " + e.getMessage());
            throw new RuntimeException("Invalid JWT signature", e);
        } catch (Exception e) {
            System.out.println("Error parsing JWT: " + e.getMessage());
            throw new RuntimeException("Error parsing JWT: " + e.getMessage(), e);
        }
    }

    @Override
    public UserDTO deleteUser(String email) {
        UserDTO userDTO = findByEmail(email);

        if(userDTO == null) {
            throw new RuntimeException("Usuario no fue encontrado");
        }

        userDTO.setAccountStatus(AccountStatement.ELIMINADO);
        emailService.sendEmailToDeleteUser(userDTO.getEmail());
        userRepository.save(UserMapper.INSTANCE.userDTOToUserEntity(userDTO));
        return userDTO;
    }

    @Override
    public Optional<UserDTO> findById(Long id) {
        Optional<UserEntity> userOptional = userRepository.findById(id);
        if (userOptional.isPresent()) {
            return Optional.of(UserMapper.INSTANCE.userEntityToUserDTO(userOptional.get()));
        }
        return Optional.empty();
    }
}
