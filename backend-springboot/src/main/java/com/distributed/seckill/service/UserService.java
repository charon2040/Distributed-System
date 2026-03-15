package com.distributed.seckill.service;

import com.distributed.seckill.mapper.UserMapper;
import com.distributed.seckill.model.User;
import com.distributed.seckill.util.HashUtil;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

@Service
public class UserService {
  private final UserMapper userMapper;

  public UserService(UserMapper userMapper) {
    this.userMapper = userMapper;
  }

  public User register(String username, String password) {
    User user = new User();
    user.setUsername(username);
    user.setPasswordHash(HashUtil.sha256(password));
    try {
      userMapper.insert(user);
      return user;
    } catch (DuplicateKeyException ex) {
      return null;
    }
  }

  public User login(String username, String password) {
    User user = userMapper.findByUsername(username);
    if (user == null) {
      return null;
    }
    String hashed = HashUtil.sha256(password);
    if (!hashed.equals(user.getPasswordHash())) {
      return null;
    }
    return user;
  }
}
