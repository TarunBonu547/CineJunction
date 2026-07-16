package com.cinejunction.controller;

import com.cinejunction.dto.request.ChangePasswordRequest;
import com.cinejunction.dto.request.LoginRequest;
import com.cinejunction.dto.request.RegisterRequest;
import com.cinejunction.dto.response.ApiResponse;
import com.cinejunction.dto.response.AuthenticationResponse;
import com.cinejunction.dto.response.UserResponse;
import com.cinejunction.service.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthenticationResponse>> register(
            @Valid @RequestBody RegisterRequest request) {

        AuthenticationResponse response = authenticationService.register(request);
        return ResponseEntity.ok(ApiResponse.<AuthenticationResponse>builder()
                .success(true)
                .message("User registered successfully")
                .data(response)
                .build());
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthenticationResponse>> login(
            @Valid @RequestBody LoginRequest request) {

        AuthenticationResponse response = authenticationService.login(request);
        return ResponseEntity.ok(ApiResponse.<AuthenticationResponse>builder()
                .success(true)
                .message("Login successful")
                .data(response)
                .build());
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> getCurrentUser() {

        UserResponse response = authenticationService.getCurrentUser();
        return ResponseEntity.ok(ApiResponse.<UserResponse>builder()
                .success(true)
                .message("User details retrieved successfully")
                .data(response)
                .build());
    }

    @PutMapping("/change-password")
    public ResponseEntity<ApiResponse<String>> changePassword(
            @Valid @RequestBody ChangePasswordRequest request) {

        authenticationService.changePassword(request);
        return ResponseEntity.ok(ApiResponse.<String>builder()
                .success(true)
                .message("Password changed successfully")
                .build());
    }
}