package com.dg.schoolhelp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dg.schoolhelp.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 *  Mapper 接口
 *
 * @author DG
 * @since 2024-09-23
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

    @Select("SELECT * FROM user WHERE user_openid = #{openId}")
    User selectUserByOpenId(String openId);
}
