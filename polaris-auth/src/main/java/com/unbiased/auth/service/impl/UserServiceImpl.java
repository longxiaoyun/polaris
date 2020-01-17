package com.unbiased.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.unbiased.auth.entity.User;
import com.unbiased.auth.mapper.UserMapper;
import com.unbiased.auth.service.UserService;
import com.unbiased.auth.utils.JwtUtil;
import com.unbiased.common.exception.CustomException;
import com.unbiased.common.exception.CustomUnauthorizedException;
import com.unbiased.common.model.Constant;
import com.unbiased.common.model.ResponseResult;
import org.apache.shiro.SecurityUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * @author longjiang
 * @date 2020-01-15 7:09 下午
 * @description 用户service实现
 **/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Resource
    private UserMapper userMapper;

    public UserServiceImpl() {
    }

    /**
     * 获取用户信息
     * @return
     */
    @Override
    public User getUser() {

        String token = SecurityUtils.getSubject().getPrincipal().toString();

        // 解密账号
        String username = JwtUtil.getClaim(token, Constant.USERNAME);
        User user = new User();
        user.setUsername(username);
        QueryWrapper<User> queryWrapper=new QueryWrapper<>(user);

        user = userMapper.selectOne(queryWrapper);

        if (user == null){
            throw new CustomException("该账号不存在(The account does not exist.)");
        }

        return user;
    }

    /**
     * 获取用户id
     * @return
     */
    @Override
    public Integer getUserId() {
        return getUser().getId();
    }


    /**
     * 获取token
     * @return
     */
    @Override
    public String getToken() {
        return SecurityUtils.getSubject().getPrincipal().toString();
    }

    /**
     * 获取当前登录用户的用户名
     * @return
     */
    @Override
    public String getUsername() {
        String token = SecurityUtils.getSubject().getPrincipal().toString();
        // 解密获得Account
        return JwtUtil.getClaim(token, Constant.USERNAME);
    }

    /**
     * 根据用户名查找用户
     * @param username
     * @return
     */
    @Override
    public User getUserByUsername(String username) {

        return userMapper.selectOne(new QueryWrapper<User>().lambda().eq(User::getUsername, username));

    }

    /**
     * 根据手机号查找用户
     * @param phone
     * @return
     */
    @Override
    public User getUserByPhone(String phone) {
        return userMapper.selectOne(new QueryWrapper<User>().lambda().eq(User::getPhone, phone));
    }

    /**
     * 根据邮箱地址查找用户
     * @param email
     * @return
     */
    @Override
    public User getUserByEmail(String email) {
        return userMapper.selectOne(new QueryWrapper<User>().lambda().eq(User::getEmail, email));
    }

}
