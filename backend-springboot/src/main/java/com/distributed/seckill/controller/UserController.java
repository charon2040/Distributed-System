package com.distributed.seckill.controller;

import com.distributed.seckill.dto.LoginRequest;
import com.distributed.seckill.dto.RegisterRequest;
import com.distributed.seckill.model.User;
import com.distributed.seckill.service.UserService;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
/**
 * 用户接口：注册与登录。
 */
public class UserController {
  private final UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
  }

  @PostMapping("/register")
  public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
    // 基础参数校验，避免空指针与脏数据
    if (request == null || request.getUsername() == null || request.getPassword() == null) {
      return ResponseEntity.badRequest().body(Map.of("message", "INVALID_INPUT"));
    }
    User user = userService.register(request.getUsername().trim(), request.getPassword());
    if (user == null) {
      return ResponseEntity.status(409).body(Map.of("message", "USER_EXISTS"));
    }
    return ResponseEntity.status(201).body(Map.of("id", user.getId(), "username", user.getUsername()));
  }

  @PostMapping("/login")
  public ResponseEntity<?> login(@RequestBody LoginRequest request) {
    // 登录参数校验
    if (request == null || request.getUsername() == null || request.getPassword() == null) {
      return ResponseEntity.badRequest().body(Map.of("message", "INVALID_INPUT"));
    }
    User user = userService.login(request.getUsername().trim(), request.getPassword());
    if (user == null) {
      return ResponseEntity.status(401).body(Map.of("message", "INVALID_CREDENTIALS"));
    }
    return ResponseEntity.ok(Map.of("id", user.getId(), "username", user.getUsername()));
  }
}
