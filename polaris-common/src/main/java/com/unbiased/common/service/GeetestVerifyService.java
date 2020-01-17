package com.unbiased.common.service;

import com.unbiased.common.model.ResponseResult;

import java.io.IOException;

/**
 * @author longjiang
 * @date 2020-01-16 2:16 下午
 * @description 极验验证
 **/

public interface GeetestVerifyService {


    /**
     * 极验验证码初始化
     * @return
     */
//    ResponseResult geetestRegister();

    /**
     * 极验二次验证
     * @param geetestChallenge
     * @param geetestValidate
     * @param geetestSeccode
     * @return
     */
    ResponseResult secondValidate(String geetestChallenge, String geetestValidate, String geetestSeccode,String gtServerStatus) throws IOException;
}
