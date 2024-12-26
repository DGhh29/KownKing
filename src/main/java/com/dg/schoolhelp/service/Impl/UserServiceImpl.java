package com.dg.schoolhelp.service.Impl;

import com.dg.schoolhelp.entity.User;
import com.dg.schoolhelp.mapper.UserMapper;
import com.dg.schoolhelp.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *  服务实现类
 *
 * @author DG
 * @since 2024-09-23
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public void SignUp(User user) {
        if (userMapper.selectUserByOpenId(user.getUserOpenid())== null) super.save(user);
    }

    @Override
    public User Login(String userOpenId) {
        return userMapper.selectUserByOpenId(userOpenId);
    }
}
