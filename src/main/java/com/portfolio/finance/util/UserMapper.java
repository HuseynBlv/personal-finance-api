package com.portfolio.finance.util;

import com.portfolio.finance.dto.UserResponse;
import com.portfolio.finance.entity.User;

public final class UserMapper {

    private UserMapper() {
    }

    public static UserResponse toUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .build();
    }
}
