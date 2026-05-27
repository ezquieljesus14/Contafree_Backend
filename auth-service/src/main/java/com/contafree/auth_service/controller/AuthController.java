package com.contafree.auth_service.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.contafree.auth_service.dto.LoginRequest;
import com.contafree.auth_service.dto.LoginResponse;
import com.contafree.auth_service.dto.RefreshRequest;
import com.contafree.auth_service.dto.UserResponse;
import com.contafree.auth_service.service.AuthService;
import com.contafree.common.dto.ApiResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    
    @GetMapping("/ping")
    public String ping() {
    	return "pinggasjdnañsldn";
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<LoginResponse>> refresh(@Valid @RequestBody RefreshRequest request) {
        LoginResponse response = authService.refresh(request.getRefreshToken());
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(@Valid @RequestBody RefreshRequest request) {
        authService.logout(request.getRefreshToken());
        return ResponseEntity.ok(ApiResponse.ok(null));
    }
    
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> getUserIngo(Authentication auth){
    	UUID userId = UUID.fromString((String) auth.getPrincipal());
    	
        return authService.getCurrentUser(userId)
                .map(dto -> ResponseEntity.ok(ApiResponse.ok(dto)))
                .orElse(ResponseEntity.status(404).body(
                        ApiResponse.error("User not found", "NOT_FOUND")));
    	
    }
}