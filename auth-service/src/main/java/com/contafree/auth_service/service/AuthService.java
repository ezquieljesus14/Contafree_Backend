package com.contafree.auth_service.service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    @Value("${jwt.refresh-token-expiration}")
    private long refreshTokenExpiration;

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;

    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UnauthorizedException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new UnauthorizedException("Invalid email or password");
        }

        String accessToken = jwtProvider.generateAccessToken(user);
        RefreshToken refreshToken = createRefreshToken(user);

        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .expiresIn(900)
                .build();
    }

    public LoginResponse refresh(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new UnauthorizedException("Invalid refresh token"));

        if (refreshToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            refreshTokenRepository.delete(refreshToken);
            throw new UnauthorizedException("Refresh token expired");
        }

        String accessToken = jwtProvider.generateAccessToken(refreshToken.getUser());
        return LoginResponse.builder()
                .accessToken(accessToken)
                .expiresIn(900)
                .build();
    }

    public void logout(String token) {
        refreshTokenRepository.findByToken(token)
                .ifPresent(refreshTokenRepository::delete);
    }

    private RefreshToken createRefreshToken(User user) {
        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .token(UUID.randomUUID().toString())
                .expiresAt(LocalDateTime.now().plusSeconds(refreshTokenExpiration))
                .build();
        return refreshTokenRepository.save(refreshToken);
    }
    
    
    public UserResponse register(RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new DuplicateResourceException("email", "Email already registered");
        }
        User user = User.builder()
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .roles(request.getRoles())
                .build();
        User saved = userRepository.save(user);
        return new UserResponse(saved.getEmail(), saved.getRoles(), saved.getCreatedAt(), saved.getUpdatedAt());
    }

    public void updatePassword(UUID userId, UpdatePasswordRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPasswordHash())) {
            throw new UnauthorizedException("Current password is incorrect");
        }

        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    public Optional<UserResponse> getCurrentUser(UUID userId) {
        return userRepository.findById(userId)
                .map(user -> new UserResponse(
                		user.getEmail(),
                		user.getRoles(),
                		user.getCreatedAt(),
                		user.getUpdatedAt()
                ));
    }
}
