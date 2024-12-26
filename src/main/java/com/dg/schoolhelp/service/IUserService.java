package com.dg.schoolhelp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dg.schoolhelp.entity.User;

/**
 *  服务类
 *
 * @author DG
 * @since 2024-09-23
 */
public interface IUserService extends IService<User> {
    void SignUp(User user);

    User Login(String userOpenId);

}
