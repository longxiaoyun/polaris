package com.unbiased.auth.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author longjiang
 * @date 2020-01-15 6:40 下午
 * @description 权限表
 **/
@Data
public class Permission implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;

    private String resourceName;

    private Integer parentId;

    private String resourceType;

    private String perms;

    private String resourceDescription;

    private String resourceCode;

    private String gmtUser;

    private Date gmtTime;

    private Date lastModifyTime;
}
