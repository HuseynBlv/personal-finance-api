package com.portfolio.finance.service;

import com.portfolio.finance.dto.CreateUserRequest;
import com.portfolio.finance.dto.UserResponse;
import java.util.List;

public interface UserService {

    List<UserResponse> getUsers();

    UserResponse createUser(CreateUserRequest request);
}
