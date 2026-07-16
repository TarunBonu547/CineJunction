package com.cinejunction.controller;

import com.cinejunction.dto.request.RegisterRequest;
import com.cinejunction.dto.response.AuthenticationResponse;
import com.cinejunction.service.AuthenticationService;
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
}