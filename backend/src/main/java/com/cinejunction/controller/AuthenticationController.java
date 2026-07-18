package com.cinejunction.controller;

import com.cinejunction.dto.request.LoginRequest;
import com.cinejunction.dto.request.RegisterRequest;
import com.cinejunction.dto.response.AuthenticationResponse;
import com.cinejunction.service.interfaces.AuthenticationService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    public AuthenticationResponse register(
            @Valid @RequestBody RegisterRequest request) {

        return authenticationService.register(request);
    }

    @PostMapping("/login")
    public AuthenticationResponse login(
            @Valid @RequestBody LoginRequest request) {

        return authenticationService.login(request);
    }
}