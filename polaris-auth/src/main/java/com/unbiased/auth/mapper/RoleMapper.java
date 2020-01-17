package com.unbiased.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.unbiased.auth.entity.Role;
import com.unbiased.auth.entity.User;

import java.util.List;

/**
 * @author longjiang
 * @date 2020-01-15 6:59 下午
 * @description 角色mapper
 **/

public interface RoleMapper extends BaseMapper<Role> {

    /**
     * 根据user查询role
     * @param userDto
     * @return
     */
    List<Role> findRoleByUser(User userDto);
}
