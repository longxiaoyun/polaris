package com.unbiased.common.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.unbiased.common.config.GeetestConfig;
import com.unbiased.common.config.GeetestLib;
import com.unbiased.common.model.ResponseResult;
import com.unbiased.common.service.GeetestVerifyService;
import com.unbiased.common.utils.AesCipherUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;


/**
 * @author longjiang
 * @date 2020-01-16 1:10 下午
 * @description 极验验证码
 **/
@Slf4j
@RestController
@RequestMapping("gt")
public class GeetestController {

    private final GeetestVerifyService geetestVerifyService;

    @Autowired
    public GeetestController(GeetestVerifyService geetestVerifyService) {
        this.geetestVerifyService = geetestVerifyService;
    }

    /**
     * 极验初始化
     * @return
     */
    @GetMapping("/register")
    @ResponseBody
    protected ResponseResult register() {
        GeetestLib gtSdk = new GeetestLib(GeetestConfig.getGeetestId(), GeetestConfig.getGeetestKey(),GeetestConfig.isnewfailback());

        //自定义参数,可选择添加
        HashMap<String, String> param = new HashMap<String, String>();
        /**
         * 网站用户id
         */
        param.put("user_id", "domain");
        /**
         * web:电脑上的浏览器；h5:手机上的浏览器，包括移动应用内完全内置的web_view；native：通过原生SDK植入APP应用的方式
         */
        param.put("client_type", "web");
        /**
         * 传输用户请求验证时所携带的IP
         */
        param.put("ip_address", "127.0.0.1");

        //进行验证预处理
        int gtServerStatus = gtSdk.preProcess(param);

        String resStr = gtSdk.getResponseStr();

        JSONObject jsonObject = JSON.parseObject(resStr);
        jsonObject.put(gtSdk.gtServerStatusSessionKey, AesCipherUtil.enCrypto(Integer.toString(gtServerStatus)));

        String dataStr=jsonObject.toJSONString();

        return new ResponseResult(HttpStatus.OK.value(), "极验初始化接口成功.",dataStr);
    }

}
