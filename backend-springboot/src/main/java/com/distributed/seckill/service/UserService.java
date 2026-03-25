package com.distributed.seckill.service;

import com.distributed.seckill.mapper.UserMapper;
import com.distributed.seckill.model.User;
import com.distributed.seckill.util.HashUtil;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
/**
 * 用户服务，包含注册与登录校验逻辑。
 */
public class UserService {
  private final UserMapper userMapper;

  public UserService(UserMapper userMapper) {
    this.userMapper = userMapper;
  }

  @Transactional
  public User register(String username, String password) {
    // 注册时对密码做哈希存储，避免明文落库
    User user = new User();
    user.setUsername(username);
    user.setPasswordHash(HashUtil.sha256(password));
    try {
      userMapper.insert(user);
      return user;
    } catch (DuplicateKeyException ex) {
      // 用户名冲突时返回 null，由控制层转为 409
      return null;
    }
  }

  @Transactional(readOnly = true)
  public User login(String username, String password) {
    // 先按用户名查库，再比对密码哈希
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
