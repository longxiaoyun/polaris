package com.unbiased.common.config;

/**
 * @author longjiang
 * @date 2020-01-16 1:22 下午
 * @description geetest配置
 **/

public class GeetestConfig {
    /**
     * 填入自己的captcha_id和private_key
     */
    private static final String GEETEST_ID = "xxxx";
    private static final String GEETEST_KEY = "xxxx";
    private static final boolean NEWFAILBACK = true;

    public static String getGeetestId() {
        return GEETEST_ID;
    }

    public static String getGeetestKey() {
        return GEETEST_KEY;
    }

    public static boolean isnewfailback() {
        return NEWFAILBACK;
    }
}
