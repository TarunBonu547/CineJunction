package com.cinejunction.service.impl;

import com.cinejunction.dto.request.ChangePasswordRequest;
import com.cinejunction.dto.request.UpdateProfileRequest;
import com.cinejunction.dto.response.UserProfileResponse;
import com.cinejunction.entity.User;
import com.cinejunction.exception.EmailAlreadyExistsException;
import com.cinejunction.exception.InvalidPasswordException;
import com.cinejunction.exception.ResourceNotFoundException;
import com.cinejunction.repository.UserRepository;
import com.cinejunction.service.interfaces.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserProfileResponse getCurrentUser() {
        String email = getCurrentUserEmail();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
        return mapToUserProfileResponse(user);
    }

    @Override
    public UserProfileResponse updateProfile(UpdateProfileRequest request) {
        String email = getCurrentUserEmail();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

        String trimmedEmail = request.getEmail().trim();
        String trimmedUsername = request.getUsername().trim();

        if (!user.getEmail().equals(trimmedEmail) && userRepository.existsByEmail(trimmedEmail)) {
            throw new EmailAlreadyExistsException("Email already exists: " + trimmedEmail);
        }

        if (!user.getUsername().equals(trimmedUsername) && userRepository.existsByUsername(trimmedUsername)) {
            throw new EmailAlreadyExistsException("Username already exists: " + trimmedUsername);
        }

        user.setEmail(trimmedEmail);
        user.setUsername(trimmedUsername);
        userRepository.save(user);
        return mapToUserProfileResponse(user);
    }

    @Override
    public void changePassword(ChangePasswordRequest request) {
        String email = getCurrentUserEmail();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new InvalidPasswordException("Current password is incorrect");
        }

        if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
            throw new InvalidPasswordException("New password cannot be the same as the current password");
        }

        String encodedNewPassword = passwordEncoder.encode(request.getNewPassword());
        user.setPassword(encodedNewPassword);
        userRepository.save(user);
    }

    @Override
    public void deleteAccount() {
        String email = getCurrentUserEmail();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
        userRepository.delete(user);
    }

    private String getCurrentUserEmail() {
        org.springframework.security.core.userdetails.UserDetails userDetails = (org.springframework.security.core.userdetails.UserDetails)
                org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userDetails.getUsername();
    }

    private UserProfileResponse mapToUserProfileResponse(User user) {
        return UserProfileResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
