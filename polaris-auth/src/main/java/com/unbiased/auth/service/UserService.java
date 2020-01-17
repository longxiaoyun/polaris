package com.unbiased.auth.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.unbiased.auth.entity.User;

import java.util.Map;

/**
 * @author longjiang
 * @date 2020-01-15 7:03 下午
 * @description 用户service
 **/

public interface UserService extends IService<User> {

    /**
     * 获取用户信息
     * @return
     */
    User getUser();

    /**
     * 获取用户id
     * @return
     */
    Integer getUserId();

    /**
     * 获取token
     * @return
     */
    String getToken();

    /**
     * 获取当前登录用户的用户名
     * @return
     */
    String getUsername();

//    /**
//     * 判断账号是否已经存在
//     * @return
//     */
//    Map<String,Object> accountExists(String username, String phone, String email);

    /**
     * 根据用户名查找用户
     * @param username
     * @return
     */
    User getUserByUsername(String username);

    /**
     * 根据手机号查找用户
     * @param phone
     * @return
     */
    User getUserByPhone(String phone);

    /**
     * 根据邮箱地址查找用户
     * @param email
     * @return
     */
    User getUserByEmail(String email);


}
