package com.metamind.usuario_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.SignUpRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.SignUpResponse;
import com.metamind.usuario_service.model.User;
import com.metamind.usuario_service.repository.UserRepository;
import com.metamind.usuario_service.util.PasswordStrengthValidator;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private CognitoIdentityProviderClient cognitoClient;

    @Override
    public User createUser(User user) {
        if (userRepository.findByEmail(user.getEmail()) != null) {
            throw new RuntimeException("E-mail já cadastrado");
        }

        if (!PasswordStrengthValidator.isStrong(user.getPassword())) {
            throw new IllegalArgumentException("A senha não atende aos critérios de segurança");
        }

        // Registro no AWS Cognito
        SignUpRequest signUpRequest = SignUpRequest.builder()
                .clientId("seu_client_id") // Substitua pelo seu Client ID
                .username(user.getEmail())
                .password(user.getPassword())
                .userAttributes(a -> a.name("email").value(user.getEmail()))
                .build();

        SignUpResponse signUpResponse = cognitoClient.signUp(signUpRequest);

        // Salvar no banco de dados local
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        user.setCognitoUserId(signUpResponse.userSub()); // Armazena o ID do Cognito
        return userRepository.save(user);
    }

    @Override
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User updateUser(Long id, User user) {
        Optional<User> existingUser = userRepository.findById(id);
        if (existingUser.isPresent()) {
            User userToUpdate = existingUser.get();
            user.setId(userToUpdate.getId());

            if (user.getPassword() != null && !user.getPassword().isEmpty()) {
                String encodedPassword = passwordEncoder.encode(user.getPassword());
                user.setPassword(encodedPassword);
            } else {
                user.setPassword(userToUpdate.getPassword());
            }
            return userRepository.save(user);
        }
        return null;
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}