package com.unbiased.auth.config.shiro;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.unbiased.auth.config.shiro.jwt.JwtToken;
import com.unbiased.auth.entity.Permission;
import com.unbiased.auth.entity.Role;
import com.unbiased.auth.entity.User;
import com.unbiased.auth.mapper.PermissionMapper;
import com.unbiased.auth.mapper.RoleMapper;
import com.unbiased.auth.mapper.UserMapper;
import com.unbiased.auth.utils.JedisUtil;
import com.unbiased.auth.utils.JwtUtil;
import com.unbiased.common.model.Constant;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;


@Service
public class MyRealm extends AuthorizingRealm {

    @Resource
    private UserMapper userMapper;

    @Resource
    private RoleMapper roleMapper;

    @Resource
    private PermissionMapper permissionMapper;

    @Autowired
    public MyRealm(UserMapper userMapper, RoleMapper roleMapper, PermissionMapper permissionMapper) {
        this.userMapper = userMapper;
        this.roleMapper = roleMapper;
        this.permissionMapper = permissionMapper;
    }

    public MyRealm() {
    }

    /**
     * 大坑，必须重写此方法，不然Shiro会报错
     */
    @Override
    public boolean supports(AuthenticationToken authenticationToken) {
        return authenticationToken instanceof JwtToken;
    }

    /**
     * 只有当需要检测用户权限的时候才会调用此方法，例如checkRole,checkPermission之类的
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();
        String username = JwtUtil.getClaim(principalCollection.toString(), Constant.USERNAME);
        User user = new User();
        user.setUsername(username);
        // 查询用户角色
        List<Role> roles = roleMapper.findRoleByUser(user);
        for (Role role : roles) {
            if (role != null) {
                // 添加角色
                simpleAuthorizationInfo.addRole(role.getRoleCode());
                // 根据用户角色查询权限
                List<Permission> permissionDtos = permissionMapper.findPermissionByRole(role);
                for (Permission permissionDto : permissionDtos) {
                    if (permissionDto != null) {
                        // 添加权限
                        simpleAuthorizationInfo.addStringPermission(permissionDto.getResourceCode());
                    }
                }
            }
        }
        return simpleAuthorizationInfo;
    }

    /**
     * 默认使用此方法进行用户名正确与否验证，错误抛出异常即可。
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        String token = (String) authenticationToken.getCredentials();
        // 解密获得account，用于和数据库进行对比
        String username = JwtUtil.getClaim(token, Constant.USERNAME);
        // 帐号为空
        if (StringUtils.isBlank(username)) {
            throw new AuthenticationException("Token中帐号为空(The account in Token is empty.)");
        }
        // 查询用户是否存在
        User user = new User();
        user.setUsername(username);

        QueryWrapper<User> qryWrapper = new QueryWrapper<>(user);

        user = userMapper.selectOne(qryWrapper);
        if (user == null) {
            throw new AuthenticationException("该帐号不存在(The account does not exist.)");
        }
        // 开始认证，要AccessToken认证通过，且Redis中存在RefreshToken，且两个Token时间戳一致
        if (JwtUtil.verify(token) && JedisUtil.exists(Constant.PREFIX_SHIRO_REFRESH_TOKEN + username)) {
            // 获取RefreshToken的时间戳
            String currentTimeMillisRedis = JedisUtil.getObject(Constant.PREFIX_SHIRO_REFRESH_TOKEN + username).toString();
            // 获取AccessToken时间戳，与RefreshToken的时间戳对比
            if (JwtUtil.getClaim(token, Constant.CURRENT_TIME_MILLIS).equals(currentTimeMillisRedis)) {
                return new SimpleAuthenticationInfo(token, token, "userRealm");
            }
        }
        throw new AuthenticationException("Token已过期(Token expired or incorrect.)");
    }
}
