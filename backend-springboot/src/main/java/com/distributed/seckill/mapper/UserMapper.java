package com.distributed.seckill.mapper;

import com.distributed.seckill.model.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

@Mapper
/**
 * 用户表访问接口。
 */
public interface UserMapper {
  // 新增用户并回填主键
  @Insert("INSERT INTO users (username, password_hash) VALUES (#{username}, #{passwordHash})")
  @Options(useGeneratedKeys = true, keyProperty = "id")
  int insert(User user);

  // 按用户名查询用户
  @Select("SELECT id, username, password_hash FROM users WHERE username = #{username}")
  User findByUsername(String username);
}
