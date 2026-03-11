package com.distributed.userservice.service;

import com.distributed.userservice.entity.User;

public interface UserService {
    User register(String username, String password, String email, String phone);
    User login(String username, String password);
    User getUserById(Long userId);
}
