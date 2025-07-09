package com.example.AuthService.test;
import com.example.AuthService.DTO.*;
import com.example.AuthService.entity.User;
import com.example.AuthService.entity.VerificationToken;
import com.example.AuthService.repository.VerificationTokenRepository;
import com.example.AuthService.repository.userrepository;
import com.example.AuthService.service.AuthService;
import com.example.AuthService.service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private userrepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private TokenStore refreshTokenStore;

    @Mock
    private VerificationTokenRepository verificationTokenRepository;

    @InjectMocks
    private AuthService authService;

    private RegisterRequest registerRequest;
    private AuthRequest authRequest;
    private UpdateProfileRequest updateProfileRequest;
    private ChangePasswordRequest changePasswordRequest;
    private User user;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest();
        registerRequest.setUsername("testuser");
        registerRequest.setEmail("test@example.com");
        registerRequest.setPhone("1234567890");
        registerRequest.setPassword("password123");
        registerRequest.setFirstname("Test");
        registerRequest.setLastname("User");

        authRequest = new AuthRequest();
        authRequest.setEmail("test@example.com");
        authRequest.setPassword("password123");

        updateProfileRequest = new UpdateProfileRequest();
        updateProfileRequest.setPhone("0987654321");
        updateProfileRequest.setFirstname("Updated");
        updateProfileRequest.setLastname("User");
        updateProfileRequest.setBio("New bio");
        updateProfileRequest.setPicture(new byte[]{1, 2, 3});

        changePasswordRequest = new ChangePasswordRequest();
        changePasswordRequest.setCurrentPassword("password123");
        changePasswordRequest.setNewPassword("newpassword123");

        user = User.builder()
                .username("testuser")
                .email("test@example.com")
                .phone("1234567890")
                .password("encodedPassword")
                .firstname("Test")
                .lastname("User")
                .role("ROLE_USER")
                .enabled(true)
                .build();
    }

    @Test
    void register_SuccessfulRegistration_ReturnsAuthResponse() {
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.existsByPhone(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(verificationTokenRepository.save(any(VerificationToken.class))).thenReturn(new VerificationToken());

        AuthResponse response = authService.register(registerRequest);

        assertNotNull(response);
        assertEquals("Veuillez vérifier votre email pour activer votre compte.", response.getAccessToken());
        assertNull(response.getRefreshToken());
        verify(userRepository).save(any(User.class));
        verify(verificationTokenRepository).save(any(VerificationToken.class));
        verify(mailSender).send(any(SimpleMailMessage.class));
    }

    @Test
    void register_UsernameTaken_ThrowsIllegalArgumentException() {
        when(userRepository.existsByUsername("testuser")).thenReturn(true);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> authService.register(registerRequest));

        assertEquals("Nom d'utilisateur déjà pris.", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void register_EmailTaken_ThrowsIllegalArgumentException() {
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> authService.register(registerRequest));

        assertEquals("Adresse email déjà utilisée.", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void register_PhoneTaken_ThrowsIllegalArgumentException() {
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.existsByPhone("1234567890")).thenReturn(true);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> authService.register(registerRequest));

        assertEquals("Numéro de telephone déjà utilisé.", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }



    @Test
    void updateProfile_ValidRequest_UpdatesUserFields() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        User updatedUser = authService.updateProfile("testuser", updateProfileRequest);

        assertEquals("0987654321", updatedUser.getPhone());
        assertEquals("Updated", updatedUser.getFirstname());
        assertEquals("User", updatedUser.getLastname());
        assertEquals("New bio", updatedUser.getBio());
        assertArrayEquals(new byte[]{1, 2, 3}, updatedUser.getPicture());
        verify(userRepository).save(user);
    }

    @Test
    void updateProfile_UserNotFound_ThrowsUsernameNotFoundException() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());

        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class,
                () -> authService.updateProfile("testuser", updateProfileRequest));

        assertEquals("User not found", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void changePassword_ValidRequest_ChangesPassword() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password123", "encodedPassword")).thenReturn(true);
        when(passwordEncoder.encode("newpassword123")).thenReturn("newEncodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        authService.changePassword("testuser", changePasswordRequest);

        verify(userRepository).save(argThat(u -> u.getPassword().equals("newEncodedPassword")));
    }

    @Test
    void changePassword_IncorrectCurrentPassword_ThrowsIllegalArgumentException() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password123", "encodedPassword")).thenReturn(false);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> authService.changePassword("testuser", changePasswordRequest));

        assertEquals("Current password is incorrect", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void loadUserByUsername_UserExists_ReturnsUser() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        User foundUser = authService.loadUserByUsername("testuser");

        assertEquals(user, foundUser);
        verify(userRepository).findByUsername("testuser");
    }

    @Test
    void loadUserByUsername_UserNotFound_ThrowsRuntimeException() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> authService.loadUserByUsername("testuser"));

        assertEquals("Utilisateur non trouvé", exception.getMessage());
    }

    @Test
    void authenticate_ValidCredentials_ReturnsAuthResponse() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password123", "encodedPassword")).thenReturn(true);
        when(jwtService.generateAccessToken(user)).thenReturn("accessToken");
        when(jwtService.generateRefreshToken(user)).thenReturn("refreshToken");

        AuthResponse response = authService.authenticate(authRequest);

        assertEquals("accessToken", response.getAccessToken());
        assertEquals("refreshToken", response.getRefreshToken());
        verify(refreshTokenStore).put("testuser", "refreshToken");
    }

    @Test
    void authenticate_NonExistentEmail_ThrowsRuntimeException() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> authService.authenticate(authRequest));

        assertEquals("Compte inexistant", exception.getMessage());
    }

    @Test
    void authenticate_IncorrectPassword_ThrowsRuntimeException() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password123", "encodedPassword")).thenReturn(false);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> authService.authenticate(authRequest));

        assertEquals("Mot de passe incorrect", exception.getMessage());
    }

    @Test
    void authenticate_DisabledUser_ThrowsRuntimeException() {
        user.setEnabled(false);
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password123", "encodedPassword")).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> authService.authenticate(authRequest));

        assertEquals("Veuillez d'abord vérifier votre adresse email.", exception.getMessage());
    }
}