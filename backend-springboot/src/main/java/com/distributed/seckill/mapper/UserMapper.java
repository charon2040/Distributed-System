package com.distributed.seckill.mapper;

import com.distributed.seckill.model.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {
  @Insert("INSERT INTO users (username, password_hash) VALUES (#{username}, #{passwordHash})")
  @Options(useGeneratedKeys = true, keyProperty = "id")
  int insert(User user);

  @Select("SELECT id, username, password_hash FROM users WHERE username = #{username}")
  User findByUsername(String username);
}
