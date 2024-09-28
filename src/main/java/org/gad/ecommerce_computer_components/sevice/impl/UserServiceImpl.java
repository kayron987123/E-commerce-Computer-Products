package org.gad.ecommerce_computer_components.sevice.impl;

import jakarta.transaction.Transactional;
import org.gad.ecommerce_computer_components.configuration.security.JWTAuthenticationConfig;
import org.gad.ecommerce_computer_components.persistence.entity.UserEntity;
import org.gad.ecommerce_computer_components.persistence.repository.UserRepository;
import org.gad.ecommerce_computer_components.presentation.dto.UserDTO;
import org.gad.ecommerce_computer_components.presentation.dto.UserRequest;
import org.gad.ecommerce_computer_components.presentation.dto.VerifyUserToken;
import org.gad.ecommerce_computer_components.sevice.interfaces.EmailService;
import org.gad.ecommerce_computer_components.sevice.interfaces.UserService;
import org.gad.ecommerce_computer_components.utils.mappers.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Duration;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int TOKEN_LENGTH = 10;

    @Autowired
    private EmailService emailService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JWTAuthenticationConfig jwtAuthenticationConfig;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public UserEntity findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public String authenticateUser(String username, String password) throws UsernameNotFoundException {
        UserEntity userResult = userRepository.findByUsername(username);
        if (userResult == null) {
            throw new UsernameNotFoundException("User not found");
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
        ops.set(token, userDTO, Duration.ofMinutes(5));
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

        if (userObj != null) {
            UserDTO userDTO;
            if (userObj instanceof Map) {
                // Convert Map to UserDTO
                Map<String, Object> userMap = (Map<String, Object>) userObj;
                userDTO = new UserDTO();
                userDTO.setName((String) userMap.get("name"));
                userDTO.setLastName((String) userMap.get("lastName"));
                userDTO.setUsername((String) userMap.get("username"));
                userDTO.setEmail((String) userMap.get("email"));
                userDTO.setPassword((String) userMap.get("password"));
                userDTO.setAddress((String) userMap.get("address"));
                userDTO.setCellphone((String) userMap.get("cellphone"));
                userDTO.setProfileImage((String) userMap.get("profileImage"));
                userDTO.setDni((String) userMap.get("dni"));
                // Set other fields as necessary
            } else if (userObj instanceof UserDTO) {
                userDTO = (UserDTO) userObj;
            } else {
                throw new IllegalStateException("Unexpected object type in Redis");
            }

            this.saveUser(userDTO);
            String tokenJWT = this.authenticateUser(userDTO.getUsername(), userDTO.getPassword());
            return tokenJWT;
        }
        return null;
    }

    @Override
    public UserDTO saveUser(UserDTO userDTO) {
        UserEntity userEntity = UserMapper.INSTANCE.userDTOToUserEntity(userDTO);
        userEntity.setPassword(new BCryptPasswordEncoder().encode(userEntity.getPassword()));
        UserEntity savedUser = this.userRepository.save(userEntity);
        return UserMapper.INSTANCE.userEntityToUserDTO(savedUser);
    }
}
