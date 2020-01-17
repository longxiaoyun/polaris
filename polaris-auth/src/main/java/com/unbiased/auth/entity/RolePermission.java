package com.unbiased.auth.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author longjiang
 * @date 2020-01-15 6:47 下午
 * @description 角色-权限
 **/
@Data
public class RolePermission implements Serializable {

    private static final long serialVersionUID = 1L;

    private int id;

    private int roleId;

    private int permissionId;

    private Date gmtTime;
}
