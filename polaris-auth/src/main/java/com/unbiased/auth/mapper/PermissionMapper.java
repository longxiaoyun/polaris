package com.unbiased.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.unbiased.auth.entity.Permission;
import com.unbiased.auth.entity.Role;

import java.util.List;

/**
 * @author longjiang
 * @date 2020-01-15 6:59 下午
 * @description 权限mapper
 **/

public interface PermissionMapper extends BaseMapper<Permission> {

    /**
     * 根据role查询permission
     * @param role
     * @return
     */
    List<Permission> findPermissionByRole(Role role);
}
