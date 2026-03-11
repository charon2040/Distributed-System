package com.distributed.userservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.distributed.userservice.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
