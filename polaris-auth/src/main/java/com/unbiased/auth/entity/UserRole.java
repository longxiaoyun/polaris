package com.unbiased.auth.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author longjiang
 * @date 2020-01-15 6:49 下午
 * @description 用户 - 角色
 **/
@Data
public class UserRole implements Serializable {

    private static final long serialVersionUID = 1L;

    private int id;

    private int userId;

    private int roleId;

    private Date gmtTime;
}
