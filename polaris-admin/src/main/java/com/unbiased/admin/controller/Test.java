package com.unbiased.admin.controller;

import com.unbiased.common.model.ResponseResult;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.shiro.subject.Subject;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * @author longjiang
 * @date 2020-01-17 11:50 上午
 * @description 测试
 **/

@RestController
public class Test {
    /**
     * 测试登录
     * @param
     * @return com.wang.model.common.ResponseBean
     * @author dolyw.com
     * @date 2018/8/30 16:18
     */
    @GetMapping("/article")
    public ResponseResult article() {
        Subject subject = SecurityUtils.getSubject();
        // 登录了返回true
        if (subject.isAuthenticated()) {
            return new ResponseResult(HttpStatus.OK.value(), "您已经登录了.", null);
        } else {
            return new ResponseResult(HttpStatus.OK.value(), "游客状态.", null);
        }
    }

    /**
     * 测试登录注解(@RequiresAuthentication和subject.isAuthenticated()返回true一个性质)
     * @param
     * @return com.wang.model.common.ResponseBean
     * @author dolyw.com
     * @date 2018/8/30 16:18
     */
    @GetMapping("/article2")
    @RequiresPermissions(logical = Logical.AND, value = {"user:view"})
    @RequiresRoles(value = {"admin"})
    public ResponseResult requireAuth() {
        return new ResponseResult(HttpStatus.OK.value(), "您已经登录了.", null);
    }
}
