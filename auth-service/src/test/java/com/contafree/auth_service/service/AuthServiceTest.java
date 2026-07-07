package com.contafree.auth_service.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import com.contafree.auth_service.config.JwtProvider;
import com.contafree.auth_service.dto.LoginRequest;
import com.contafree.auth_service.dto.LoginResponse;
import com.contafree.auth_service.dto.RegisterRequest;
import com.contafree.auth_service.dto.UpdatePasswordRequest;
import com.contafree.auth_service.dto.UserResponse;
import com.contafree.auth_service.entity.RefreshToken;
import com.contafree.auth_service.entity.User;
import com.contafree.auth_service.repository.RefreshTokenRepository;
import com.contafree.auth_service.repository.UserRepository;
import com.contafree.common.exception.DuplicateResourceException;
import com.contafree.common.exception.ResourceNotFoundException;
import com.contafree.common.exception.UnauthorizedException;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private RefreshTokenRepository refreshTokenRepository;
    @Mock private JwtProvider jwtProvider;
    @Mock private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    private User testUser;
    private final UUID userId = UUID.randomUUID();
    private final String testEmail = "test@contafree.com";
    private final String testPasswordHash = "$2a$10$hashedpassword";
    private final String testAccessToken = "access.token.test";
    private final String testRefreshTokenValue = UUID.randomUUID().toString();

    @BeforeEach
    void setUp() {
        // @Value no se inyecta con @InjectMocks — lo seteamos manualmente
        ReflectionTestUtils.setField(authService, "refreshTokenExpiration", 604800L);

        testUser = User.builder()
                .id(userId)
                .email(testEmail)
                .passwordHash(testPasswordHash)
                .roles(Set.of("ROLE_USER"))
                .build();
    }

    // -------------------------------------------------------------------------
    // login
    // -------------------------------------------------------------------------

    @Test
    void login_happyPath_returnsLoginResponseWithTokens() {
        LoginRequest request = new LoginRequest(testEmail, "rawPassword");
        RefreshToken savedToken = RefreshToken.builder()
                .token(testRefreshTokenValue)
                .user(testUser)
                .expiresAt(LocalDateTime.now().plusSeconds(604800))
                .build();

        when(userRepository.findByEmail(testEmail)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("rawPassword", testPasswordHash)).thenReturn(true);
        when(jwtProvider.generateAccessToken(testUser)).thenReturn(testAccessToken);
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenReturn(savedToken);

        LoginResponse response = authService.login(request);

        assertThat(response.getAccessToken()).isEqualTo(testAccessToken);
        assertThat(response.getRefreshToken()).isEqualTo(testRefreshTokenValue);
        assertThat(response.getExpiresIn()).isEqualTo(900);
    }

    @Test
    void login_emailNotFound_throwsUnauthorizedException() {
        LoginRequest request = new LoginRequest(testEmail, "rawPassword");
        when(userRepository.findByEmail(testEmail)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage("Invalid email or password");
    }

    @Test
    void login_wrongPassword_throwsUnauthorizedException() {
        LoginRequest request = new LoginRequest(testEmail, "wrongPassword");
        when(userRepository.findByEmail(testEmail)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("wrongPassword", testPasswordHash)).thenReturn(false);

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage("Invalid email or password");
    }

    // -------------------------------------------------------------------------
    // refresh
    // -------------------------------------------------------------------------

    @Test
    void refresh_happyPath_returnsNewAccessToken() {
        RefreshToken validToken = RefreshToken.builder()
                .token(testRefreshTokenValue)
                .user(testUser)
                .expiresAt(LocalDateTime.now().plusHours(1))
                .build();

        when(refreshTokenRepository.findByToken(testRefreshTokenValue)).thenReturn(Optional.of(validToken));
        when(jwtProvider.generateAccessToken(testUser)).thenReturn(testAccessToken);

        LoginResponse response = authService.refresh(testRefreshTokenValue);

        assertThat(response.getAccessToken()).isEqualTo(testAccessToken);
        assertThat(response.getExpiresIn()).isEqualTo(900);
        assertThat(response.getRefreshToken()).isNull();
    }

    @Test
    void refresh_tokenNotFound_throwsUnauthorizedException() {
        when(refreshTokenRepository.findByToken(testRefreshTokenValue)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.refresh(testRefreshTokenValue))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage("Invalid refresh token");
    }

    @Test
    void refresh_tokenExpired_deletesTokenAndThrowsUnauthorizedException() {
        RefreshToken expiredToken = RefreshToken.builder()
                .token(testRefreshTokenValue)
                .user(testUser)
                .expiresAt(LocalDateTime.now().minusHours(1))
                .build();

        when(refreshTokenRepository.findByToken(testRefreshTokenValue)).thenReturn(Optional.of(expiredToken));

        assertThatThrownBy(() -> authService.refresh(testRefreshTokenValue))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage("Refresh token expired");

        verify(refreshTokenRepository).delete(expiredToken);
    }

    // -------------------------------------------------------------------------
    // logout
    // -------------------------------------------------------------------------

    @Test
    void logout_tokenExists_deletesToken() {
        RefreshToken token = RefreshToken.builder()
                .token(testRefreshTokenValue)
                .user(testUser)
                .expiresAt(LocalDateTime.now().plusHours(1))
                .build();

        when(refreshTokenRepository.findByToken(testRefreshTokenValue)).thenReturn(Optional.of(token));

        authService.logout(testRefreshTokenValue);

        verify(refreshTokenRepository).delete(token);
    }

    @Test
    void logout_tokenNotFound_doesNotThrowAndDoesNotDelete() {
        when(refreshTokenRepository.findByToken(testRefreshTokenValue)).thenReturn(Optional.empty());

        authService.logout(testRefreshTokenValue);

        verify(refreshTokenRepository, never()).delete(any());
    }

    // -------------------------------------------------------------------------
    // register
    // -------------------------------------------------------------------------

    @Test
    void register_happyPath_savesUserAndReturnsUserResponse() {
        RegisterRequest request = new RegisterRequest(testEmail, "ValidPass1!", Set.of("ROLE_USER"));

        when(userRepository.findByEmail(testEmail)).thenReturn(Optional.empty());
        when(passwordEncoder.encode("ValidPass1!")).thenReturn(testPasswordHash);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        UserResponse response = authService.register(request);

        assertThat(response.getEmail()).isEqualTo(testEmail);
        assertThat(response.getRoles()).contains("ROLE_USER");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void register_duplicateEmail_throwsDuplicateResourceException() {
        RegisterRequest request = new RegisterRequest(testEmail, "ValidPass1!", Set.of("ROLE_USER"));
        when(userRepository.findByEmail(testEmail)).thenReturn(Optional.of(testUser));

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(DuplicateResourceException.class);
    }

    // -------------------------------------------------------------------------
    // updatePassword
    // -------------------------------------------------------------------------

    @Test
    void updatePassword_happyPath_encodesAndSavesNewPassword() {
        UpdatePasswordRequest request = new UpdatePasswordRequest("currentPass", "NewValidPass1!");
        String newHash = "$2a$10$newhash";

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("currentPass", testPasswordHash)).thenReturn(true);
        when(passwordEncoder.encode("NewValidPass1!")).thenReturn(newHash);

        authService.updatePassword(userId, request);

        assertThat(testUser.getPasswordHash()).isEqualTo(newHash);
        verify(userRepository).save(testUser);
    }

    @Test
    void updatePassword_userNotFound_throwsResourceNotFoundException() {
        UpdatePasswordRequest request = new UpdatePasswordRequest("currentPass", "NewValidPass1!");
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.updatePassword(userId, request))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void updatePassword_wrongCurrentPassword_throwsUnauthorizedException() {
        UpdatePasswordRequest request = new UpdatePasswordRequest("wrongPass", "NewValidPass1!");
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("wrongPass", testPasswordHash)).thenReturn(false);

        assertThatThrownBy(() -> authService.updatePassword(userId, request))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage("Current password is incorrect");
    }

    // -------------------------------------------------------------------------
    // getCurrentUser
    // -------------------------------------------------------------------------

    @Test
    void getCurrentUser_userFound_returnsOptionalWithUserResponse() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));

        Optional<UserResponse> result = authService.getCurrentUser(userId);

        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo(testEmail);
        assertThat(result.get().getRoles()).contains("ROLE_USER");
    }

    @Test
    void getCurrentUser_userNotFound_returnsEmptyOptional() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        Optional<UserResponse> result = authService.getCurrentUser(userId);

        assertThat(result).isEmpty();
    }
}
