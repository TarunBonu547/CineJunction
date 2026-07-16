package com.cinejunction.service.impl;

import com.cinejunction.dto.request.RegisterRequest;
import com.cinejunction.dto.response.AuthenticationResponse;
import com.cinejunction.entity.User;
import com.cinejunction.enums.Role;
import com.cinejunction.repository.UserRepository;
import com.cinejunction.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public AuthenticationResponse register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists");
        }

        User user = User.builder()
                .fullName(request.getFullName())
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .enabled(true)
                .build();

        userRepository.save(user);

        return AuthenticationResponse.builder()
                .message("User registered successfully")
                .token(null)
                .build();
    }
}