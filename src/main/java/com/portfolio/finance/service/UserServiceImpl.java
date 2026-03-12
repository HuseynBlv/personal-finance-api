package com.portfolio.finance.service;

import com.portfolio.finance.dto.CreateUserRequest;
import com.portfolio.finance.dto.UserResponse;
import com.portfolio.finance.entity.User;
import com.portfolio.finance.exception.ResourceAlreadyExistsException;
import com.portfolio.finance.repository.UserRepository;
import com.portfolio.finance.util.UserMapper;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getUsers() {
        return userRepository.findAll()
                .stream()
                .map(UserMapper::toUserResponse)
                .toList();
    }

    @Override
    @Transactional
    public UserResponse createUser(CreateUserRequest request) {
        userRepository.findByEmail(request.getEmail())
                .ifPresent(existing -> {
                    throw new ResourceAlreadyExistsException("User already exists with email: " + request.getEmail());
                });

        User user = User.builder()
                .email(request.getEmail())
                .password(request.getPassword())
                .build();

        return UserMapper.toUserResponse(userRepository.save(user));
    }
}
