package com.fzu.crowdsense.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fzu.crowdsense.model.entity.User;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @Entity User
 */
public interface UserMapper extends BaseMapper<User> {

    @Select("select * from user")
    public List<User> findAll();

}




