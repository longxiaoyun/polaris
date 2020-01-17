package com.unbiased.auth.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author longjiang
 * @date 2020-01-15 6:37 下午
 * @description 角色实体类
 **/
@Data
public class Role implements Serializable {

    private static final long serialVersionUID = 1L;

    private int id;

    private String roleName;

    private String roleDescription;

    private boolean roleStatus;

    private Date gmtTime;

    private Date lastModifyTime;

    private String roleCode;


}
