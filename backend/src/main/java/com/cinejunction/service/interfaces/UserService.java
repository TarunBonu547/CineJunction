package com.cinejunction.service.interfaces;

import com.cinejunction.dto.request.ChangePasswordRequest;
import com.cinejunction.dto.request.UpdateProfileRequest;
import com.cinejunction.dto.response.UserProfileResponse;

public interface UserService {

    UserProfileResponse getCurrentUser();

    UserProfileResponse updateProfile(UpdateProfileRequest request);

    void changePassword(ChangePasswordRequest request);

    void deleteAccount();
}
