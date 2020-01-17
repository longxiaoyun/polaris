package com.unbiased.auth.model;

import com.unbiased.auth.entity.User;
import lombok.Data;

/**
 * @author longjiang
 * @date 2020-01-16 1:58 下午
 * @description 用户model
 **/
@Data
public class UserModel {

    /**
     * 用户实体
     */
    private User user;

    /**
     * 极验参数challenge
     */
    private String challenge;

    /**
     * 极验参数validate
     */
    private String validate;

    /**
     * 极验参数seccode
     */
    private String seccode;

    /**
     *  极验服务器状态
     */
    private String gtServerStatus;
}
