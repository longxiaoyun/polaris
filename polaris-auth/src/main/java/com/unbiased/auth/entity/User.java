package com.unbiased.auth.entity;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * @author longjiang
 * @date 2020-01-15 6:03 下午
 * @description 用户实体类
 **/
@Data
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    private int id;

    @NotNull(message = "用户名不能为空")
    private String username;

    @NotNull(message = "密码不能为空")
    private String password;

    private String idCardType;

    private String idCard;

    private String realName;

    @Email
    private String email;

    private String sex;

    private String province;

    private String city;

    private String district;

    private String address;

    private Date signUpTime;

    private String birthday;

    /**
     * 账号状态，默认为true，可用
     */
    private boolean accountStatus = true;

    private String signIp;

    private Date lastSignInTime;

    private String intro;

    private String userType;

    @NotNull(message = "手机号不能为空")
    private String phone;

    private Date gmtTime;

}
