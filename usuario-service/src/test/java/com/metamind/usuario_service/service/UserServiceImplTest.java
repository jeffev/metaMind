package com.metamind.usuario_service.service;

import com.metamind.usuario_service.model.User;
import com.metamind.usuario_service.repository.UserRepository;
import com.metamind.usuario_service.util.PasswordStrengthValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.SignUpRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.SignUpResponse;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private CognitoIdentityProviderClient cognitoClient;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User();
        user.setId(1L);
        user.setName("Teste User");
        user.setEmail("teste@example.com");
        user.setPassword("StrongPassword123!");
    }

    @Test
    void createUser_successful() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(null);
        when(passwordEncoder.encode(user.getPassword())).thenReturn("encodedPassword");
        SignUpResponse signUpResponse = SignUpResponse.builder().userSub("cognitoUserId").build();
        when(cognitoClient.signUp(any(SignUpRequest.class))).thenReturn(signUpResponse);
        when(userRepository.save(any(User.class))).thenReturn(user);

        User createdUser = userService.createUser(user);

        assertNotNull(createdUser);
        assertEquals(user.getName(), createdUser.getName());
        assertEquals("encodedPassword", createdUser.getPassword());
        assertEquals("cognitoUserId", createdUser.getCognitoUserId());
        verify(userRepository, times(1)).save(any(User.class));
        verify(cognitoClient, times(1)).signUp(any(SignUpRequest.class));
    }

    @Test
    void createUser_emailAlreadyExists() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(user);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> userService.createUser(user));

        assertEquals("E-mail já cadastrado", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
        verify(cognitoClient, never()).signUp(any(SignUpRequest.class));
    }

    @Test
    void createUser_weakPassword() {
        user.setPassword("weak");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> userService.createUser(user));

        assertEquals("A senha não atende aos critérios de segurança", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
        verify(cognitoClient, never()).signUp(any(SignUpRequest.class));
    }

    @Test
    void getUserById_existingId() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        Optional<User> foundUser = userService.getUserById(1L);

        assertTrue(foundUser.isPresent());
        assertEquals(user.getName(), foundUser.get().getName());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void getUserById_nonExistingId() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<User> foundUser = userService.getUserById(1L);

        assertFalse(foundUser.isPresent());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void getUserByEmail_existingEmail() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(user);

        User foundUser = userService.getUserByEmail(user.getEmail());

        assertNotNull(foundUser);
        assertEquals(user.getId(), foundUser.getId());
        verify(userRepository, times(1)).findByEmail(user.getEmail());
    }

    @Test
    void getUserByEmail_nonExistingEmail() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(null);

        User foundUser = userService.getUserByEmail(user.getEmail());

        assertNull(foundUser);
        verify(userRepository, times(1)).findByEmail(user.getEmail());
    }

    @Test
    void getAllUsers_usersExist() {
        List<User> users = Arrays.asList(user, new User());
        when(userRepository.findAll()).thenReturn(users);

        List<User> allUsers = userService.getAllUsers();

        assertEquals(2, allUsers.size());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void getAllUsers_noUsersExist() {
        when(userRepository.findAll()).thenReturn(List.of());

        List<User> allUsers = userService.getAllUsers();

        assertTrue(allUsers.isEmpty());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void updateUser_existingId_withPassword() {
        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setPassword("oldEncodedPassword");
        User updatedUser = new User();
        updatedUser.setName("Updated Name");
        updatedUser.setEmail("updated@example.com");
        updatedUser.setPassword("NewStrongPassword!");

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.encode(updatedUser.getPassword())).thenReturn("newEncodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);

        User result = userService.updateUser(1L, updatedUser);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Updated Name", result.getName());
        assertEquals("newEncodedPassword", result.getPassword());
        verify(userRepository, times(1)).findById(1L);
        verify(passwordEncoder, times(1)).encode("NewStrongPassword!");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void updateUser_existingId_withoutPassword() {
        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setPassword("oldEncodedPassword");
        User updatedUser = new User();
        updatedUser.setName("Updated Name");
        updatedUser.setEmail("updated@example.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);

        User result = userService.updateUser(1L, updatedUser);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Updated Name", result.getName());
        assertEquals("oldEncodedPassword", result.getPassword()); // Senha antiga mantida
        verify(userRepository, times(1)).findById(1L);
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void updateUser_nonExistingId() {
        User updatedUser = new User();
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        User result = userService.updateUser(1L, updatedUser);

        assertNull(result);
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, never()).save(any(User.class));
        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    void deleteUser_existingId() {
        doNothing().when(userRepository).deleteById(1L);

        userService.deleteUser(1L);

        verify(userRepository, times(1)).deleteById(1L);
    }
}