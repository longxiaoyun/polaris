package com.unbiased.common.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.unbiased.common.config.GeetestConfig;
import com.unbiased.common.config.GeetestLib;
import com.unbiased.common.model.ResponseResult;
import com.unbiased.common.service.GeetestVerifyService;
import com.unbiased.common.utils.AesCipherUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;

/**
 * @author longjiang
 * @date 2020-01-16 2:19 下午
 * @description 极验验证码服务
 **/
@Slf4j
@Service
public class GeetestVerifyServiceImpl implements GeetestVerifyService {

    /**
     * 极验验证码初始化
     * @return
     */
//    @Override
//    public ResponseResult geetestRegister() {
//
//        GeetestLib gtSdk = new GeetestLib(GeetestConfig.getGeetestId(), GeetestConfig.getGeetestKey(),GeetestConfig.isnewfailback());
//
//        //自定义参数,可选择添加
//        HashMap<String, String> param = new HashMap<String, String>();
//        /**
//         * 网站用户id
//         */
//        param.put("user_id", "domain");
//        /**
//         * web:电脑上的浏览器；h5:手机上的浏览器，包括移动应用内完全内置的web_view；native：通过原生SDK植入APP应用的方式
//         */
//        param.put("client_type", "web");
//        /**
//         * 传输用户请求验证时所携带的IP
//         */
//        param.put("ip_address", "127.0.0.1");
//
//        //进行验证预处理
//        int gtServerStatus = gtSdk.preProcess(param);
//
//        String resStr = gtSdk.getResponseStr();
//
//        JSONObject jsonObject = JSON.parseObject(resStr);
//        jsonObject.put(gtSdk.gtServerStatusSessionKey, AesCipherUtil.enCrypto(Integer.toString(gtServerStatus)));
//
//        String dataStr=jsonObject.toJSONString();
//
//        return new ResponseResult(HttpStatus.OK.value(), "极验初始化接口成功.",dataStr);
//
//
//    }

    /**
     * 极验二次验证
     * @param challenge
     * @param validate
     * @param seccode
     * @return
     */
    @Override
    public ResponseResult secondValidate(String challenge, String validate, String seccode,String gtServerStatus) throws UnsupportedEncodingException {
        GeetestLib gtSdk = new GeetestLib(GeetestConfig.getGeetestId(), GeetestConfig.getGeetestKey(),GeetestConfig.isnewfailback());

        // aes解密服务器状态
        String gtServerStatusCodeTmp= AesCipherUtil.deCrypto(gtServerStatus);

        int gtServerStatusCode=Integer.parseInt(gtServerStatusCodeTmp);

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

        int gtResult = 0;

        if (gtServerStatusCode == 1) {
            //gt-server正常，向gt-server进行二次验证
            gtResult = gtSdk.enhencedValidateRequest(challenge, validate, seccode, param);
            log.debug("极验验证结果:"+gtResult);

        } else {
            // gt-server非正常情况下，进行failback模式验证
            log.error("failback:use your own server captcha validate");
            gtResult = gtSdk.failbackValidateRequest(challenge, validate, seccode);
            log.debug("极验failback模式验证结果:"+gtResult);
        }


        if (gtResult == 1) {
            // 验证成功
            JSONObject data = new JSONObject();
            try {
                data.put("status", "success");
                data.put("version", gtSdk.getVersionInfo());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return new ResponseResult(1, "极验二次验证成功.",data.toJSONString());
        }
        else {
            // 验证失败
            JSONObject data = new JSONObject();
            try {
                data.put("status", "fail");
                data.put("version", gtSdk.getVersionInfo());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return new ResponseResult(0, "极验二次验证失败.",data.toJSONString());
        }
    }
}
