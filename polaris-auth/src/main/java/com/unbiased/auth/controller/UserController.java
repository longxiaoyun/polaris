package com.unbiased.auth.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.unbiased.auth.entity.User;
import com.unbiased.auth.model.UserModel;
import com.unbiased.auth.service.UserService;
import com.unbiased.auth.utils.JedisUtil;
import com.unbiased.auth.utils.JwtUtil;
import com.unbiased.common.exception.CustomException;
import com.unbiased.common.exception.CustomUnauthorizedException;
import com.unbiased.common.model.Constant;
import com.unbiased.common.model.ResponseResult;
import com.unbiased.common.service.GeetestVerifyService;
import com.unbiased.common.utils.AesCipherUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.Date;


/**
 * @author longjiang
 * @date 2020-01-15 9:49 下午
 * @description 用户认证、权限相关
 **/
@Slf4j
@RestController
@RequestMapping("user")
@PropertySource("classpath:config.properties")
public class UserController {


    /**
     * token过期时间
     */
    @Value("${refreshTokenExpireTime}")
    private String refreshTokenExpireTime;


    /**
     * 使用构造器方式注入service
     */
    private final UserService userService;

    private final GeetestVerifyService geetestVerifyService;

    @Autowired
    public UserController(UserService userService,GeetestVerifyService geetestVerifyService) {
        this.userService = userService;
        this.geetestVerifyService=geetestVerifyService;
    }


    /**
     * 登录  手机号+密码方式
     * @param userModel
     * @param response
     * @param response
     * @return
     */
    @PostMapping("/login")
    public ResponseResult login(@RequestBody UserModel userModel, HttpServletResponse response) throws ServletException, IOException {

        // 判断极验验证码
        ResponseResult responseResult = geetestVerifyService.secondValidate(userModel.getChallenge(),userModel.getValidate(),userModel.getSeccode(),userModel.getGtServerStatus());
        if (responseResult.getCode() != 1) {
            log.error("极验验证失败");
            return responseResult;
        }

        User u1=new User();

        u1.setPhone(userModel.getUser().getPhone());
        QueryWrapper<User> queryWrapper = new QueryWrapper<>(u1);
        u1=userService.getOne(queryWrapper);

        if (u1 == null) {
            throw new CustomUnauthorizedException("该账号不存在.");
        }
        // 对密码进行加密
        String key = AesCipherUtil.deCrypto(u1.getPassword());
        // 密码加密时用 用户名+手机号+密码组合加密
        if (key.equals(userModel.getUser().getUsername() + userModel.getUser().getPhone() + userModel.getUser().getPassword())){
            // 清除可能存在的Shiro权限信息缓存
            if (JedisUtil.exists(Constant.PREFIX_SHIRO_CACHE + userModel.getUser().getUsername())) {
                JedisUtil.delKey(Constant.PREFIX_SHIRO_CACHE + userModel.getUser().getUsername());
            }

            // 设置RefreshToken，时间戳为当前时间戳，直接设置即可(不用先删后设，会覆盖已有的RefreshToken)
            String currentTimeMillis = String.valueOf(System.currentTimeMillis());
            JedisUtil.setObject(Constant.PREFIX_SHIRO_REFRESH_TOKEN + userModel.getUser().getUsername(), currentTimeMillis, Integer.parseInt(refreshTokenExpireTime));
            // 从Header中Authorization返回AccessToken，时间戳为当前时间戳
            String token = JwtUtil.sign(userModel.getUser().getUsername(), currentTimeMillis);
            response.setHeader("Authorization",token);
            response.setHeader("Access-Control-Expose-Headers","Authorization");

            return new ResponseResult(HttpStatus.OK.value(), "登陆成功.",null);

        }else {
            throw new CustomUnauthorizedException("账号或密码错误.");
        }


    }





    /**
     * 获取当前登录用户信息
     * @param
     * @return com.wang.model.common.ResponseBean
     * @author dolyw.com
     * @date 2019/3/15 11:51
     */
    @GetMapping("/user/info")
    @RequiresAuthentication
    public ResponseResult getUserInfo() {
        // 获取当前登录用户
        User user = userService.getUser();
        // 获取当前登录用户Id
        Integer id = userService.getUserId();
        // 获取当前登录用户Token
        String token = userService.getToken();
        // 获取当前登录用户的用户名
        String username = userService.getUsername();
        return new ResponseResult(HttpStatus.OK.value(), "您已经登录了.", user);
    }


    /**
     * 获取指定用户
     * @param id
     * @return java.util.Map<java.lang.String,java.lang.Object>
     * @author dolyw.com
     * @date 2018/8/30 10:42
     */
    @GetMapping("/{id}")
    @RequiresPermissions(logical = Logical.AND, value = {"user:view"})
    public ResponseResult findById(@PathVariable("id") Integer id) {
        User user = userService.getById(id);
        if (user == null) {
            throw new CustomException("查询失败.");
        }
        return new ResponseResult(HttpStatus.OK.value(), "查询成功.", user);
    }

    /**
     * 验证用户名是否已经被注册了,返回是否可以注册
     * @param username
     * @return
     */
    @GetMapping("/verify/u/{username}")
    public ResponseResult verifyUsername(@PathVariable(required = false,name = "username") String username) {
        User user = userService.getUserByUsername(username);
        if (user != null && StringUtils.isNotBlank(user.getPassword())) {
            return new ResponseResult(HttpStatus.OK.value(), "用户名已存在.", false);
        }else {
            return new ResponseResult(HttpStatus.OK.value(), "用户名可以注册.", true);
        }
    }

    /**
     * 验证邮箱地址是否已经存在,返回是否可以注册
     * @param email
     * @return
     */
    @GetMapping("/verify/e/{email}")
    public ResponseResult verifyEmail(@PathVariable(required = false,name = "email") String email){
        User user = userService.getUserByEmail(email);
        if (user != null && StringUtils.isNotBlank(user.getPassword())) {
            return new ResponseResult(HttpStatus.OK.value(), "邮箱地址已存在.", false);
        }else {
            return new ResponseResult(HttpStatus.OK.value(), "邮箱地址可以注册.", true);
        }
    }

    /**
     * 验证手机号是否已经存在,返回是否可以注册
     * @param phone
     * @return
     */
    @GetMapping("/verify/p/{phone}")
    public ResponseResult verifyPhone(@PathVariable(required = false,name = "phone") String phone){
        User user = userService.getUserByPhone(phone);
        if (user != null && StringUtils.isNotBlank(user.getPassword())) {
            return new ResponseResult(HttpStatus.OK.value(), "手机号已存在.", false);
        }else {
            return new ResponseResult(HttpStatus.OK.value(), "手机号可以注册.", true);
        }
    }


    /**
     * 新增用户
     * @param userModel
     * @return java.util.Map<java.lang.String,java.lang.Object>
     * @author dolyw.com
     * @date 2018/8/30 10:42
     */
    @PostMapping("/register")
    @ResponseBody
    public ResponseResult add(@RequestBody @Valid UserModel userModel) throws IOException {
        // 判断极验验证码
        ResponseResult responseResult = geetestVerifyService.secondValidate(userModel.getChallenge(),userModel.getValidate(),userModel.getSeccode(),userModel.getGtServerStatus());
        if (responseResult.getCode() != 1) {
            log.error("极验验证失败");
            return responseResult;
        }

        // 判断当前帐号是否存在(用户名、邮箱、手机号唯一)
//        Map<String,Object> exists = userService.accountExists(userModel.getUser().getUsername(),userModel.getUser().getPhone(),userModel.getUser().getEmail());
//        if ((boolean)exists.get("status")){
//
//        }
//        String username = userModel.getUser().getUsername();
//
//        User userExists=userService.getOne(new QueryWrapper<User>().lambda().eq(User::getUsername, username));
//
//        if (userExists != null && StringUtils.isNotBlank(userExists.getPassword())) {
//            throw new CustomUnauthorizedException("该帐号已存在.");
//        }


        userModel.getUser().setGmtTime(new Date());
        // 密码以帐号+密码的形式进行AES加密
        if (userModel.getUser().getPassword().length() > Constant.PASSWORD_MAX_LEN) {
            throw new CustomException("密码最多20位.");
        }


        String key = AesCipherUtil.enCrypto(userModel.getUser().getUsername() + userModel.getUser().getPhone() + userModel.getUser().getPassword());
        userModel.getUser().setPassword(key);

        boolean res = userService.save(userModel.getUser());
        if (!res) {
            throw new CustomException("新增失败.");
        }
        // 登录成功，返回用户信息
        return new ResponseResult(HttpStatus.OK.value(), "新增成功.", userModel.getUser());
    }

    /**
     * 更新用户
     * @param user
     * @return java.util.Map<java.lang.String,java.lang.Object>
     * @author dolyw.com
     * @date 2018/8/30 10:42
     */
    @PutMapping
    @RequiresPermissions(logical = Logical.AND, value = {"user:edit"})
    public ResponseResult update(@RequestBody @Valid User user) {
        // 查询数据库密码
        User userTemp = new User();
        userTemp.setUsername(user.getUsername());

        QueryWrapper<User> queryWrapper = new QueryWrapper<>(userTemp);
        userTemp = userService.getOne(queryWrapper);
        if (userTemp == null) {
            throw new CustomUnauthorizedException("该帐号不存在.");
        } else {
            user.setId(userTemp.getId());
        }
        // FIXME: 如果不一样就说明用户修改了密码，重新加密密码(这个处理不太好，但是没有想到好的处理方式)
        if (!userTemp.getPassword().equals(user.getPassword())) {
            // 密码以帐号+密码的形式进行AES加密
            if (userTemp.getPassword().length() > Constant.PASSWORD_MAX_LEN) {
                throw new CustomException("密码最多8位.");
            }
            String key = AesCipherUtil.enCrypto(user.getUsername() + user.getPassword());
            user.setPassword(key);
        }

        boolean res = userService.updateById(user);
        if (!res) {
            throw new CustomException("更新失败.");
        }
        return new ResponseResult(HttpStatus.OK.value(), "更新成功.", user);
    }

    /**
     * 删除用户
     * @param id
     * @return java.util.Map<java.lang.String,java.lang.Object>
     * @author dolyw.com
     * @date 2018/8/30 10:43
     */
    @DeleteMapping("/{id}")
    @RequiresPermissions(logical = Logical.AND, value = {"user:edit"})
    public ResponseResult delete(@PathVariable("id") Integer id) {
        boolean res = userService.removeById(id);
        if (!res) {
            throw new CustomException("删除失败，ID不存在.");
        }
        return new ResponseResult(HttpStatus.OK.value(), "删除成功.", null);
    }

    /**
     * 剔除在线用户
     * @param id
     * @return com.wang.model.common.ResponseBean
     * @author dolyw.com
     * @date 2018/9/6 10:20
     */
    @DeleteMapping("/delete/online/{id}")
    @RequiresPermissions(logical = Logical.AND, value = {"user:edit"})
    public ResponseResult deleteOnline(@PathVariable("id") Integer id) {
        User user = userService.getById(id);
        if (JedisUtil.exists(Constant.PREFIX_SHIRO_REFRESH_TOKEN + user.getUsername())) {
            if (JedisUtil.delKey(Constant.PREFIX_SHIRO_REFRESH_TOKEN + user.getUsername()) > 0) {
                return new ResponseResult(HttpStatus.OK.value(), "剔除成功.", null);
            }
        }
        throw new CustomException("剔除失败，Username不存在.");
    }





}
