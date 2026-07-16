package com.cinejunction.service;

import com.cinejunction.dto.request.ChangePasswordRequest;
import com.cinejunction.dto.request.LoginRequest;
import com.cinejunction.dto.request.RegisterRequest;
import com.cinejunction.dto.response.AuthenticationResponse;
import com.cinejunction.dto.response.UserResponse;

public interface AuthenticationService {

    AuthenticationResponse register(RegisterRequest request);
    AuthenticationResponse login(LoginRequest request);
    UserResponse getCurrentUser();
    void changePassword(ChangePasswordRequest request);
}