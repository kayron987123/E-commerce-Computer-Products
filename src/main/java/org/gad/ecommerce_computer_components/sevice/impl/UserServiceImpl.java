package org.gad.ecommerce_computer_components.sevice.impl;

import jakarta.transaction.Transactional;
import org.gad.ecommerce_computer_components.configuration.security.JWTAuthenticationConfig;
import org.gad.ecommerce_computer_components.persistence.entity.UserEntity;
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
    public void saveUserInRedis(UserDTO userDTO) {
        String token = generateToken();
        emailService.sendEmailTemporaryKey(userDTO.getEmail(), token);
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
        userEntity.setPassword(new BCryptPasswordEncoder().encode(userEntity.getPassword()));
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
}
