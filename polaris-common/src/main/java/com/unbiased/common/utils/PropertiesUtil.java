package com.unbiased.common.utils;

import com.unbiased.common.exception.CustomException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

/**
 * @author longjiang
 * @date 2020-01-15 5:22 下午
 * @description 读取配置文件工具类
 **/
@Slf4j
@Component
public class PropertiesUtil {
    private PropertiesUtil() {}

    /**
     * PROP
     */
    private static final Properties PROP = new Properties();

    /**
     * 读取配置文件
     * @param fileName
     * @return void
     * @author dolyw.com
     * @date 2018/8/31 17:29
     */
    public static void readProperties(String fileName) {
        InputStream in = null;
        try {
            in = PropertiesUtil.class.getResourceAsStream("/" + fileName);
            BufferedReader bf = new BufferedReader(new InputStreamReader(in));
            PROP.load(bf);
        } catch (IOException e) {
            log.error("PropertiesUtil工具类读取配置文件出现IOException异常:{}", e.getMessage());
            throw new CustomException("PropertiesUtil工具类读取配置文件出现IOException异常:" + e.getMessage());
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                log.error("PropertiesUtil工具类读取配置文件出现IOException异常:{}", e.getMessage());
                throw new CustomException("PropertiesUtil工具类读取配置文件出现IOException异常:" + e.getMessage());
            }
        }
    }

    /**
     * 根据key读取对应的value
     * @param key
     * @return java.lang.String
     * @author dolyw.com
     * @date 2018/8/31 17:29
     */
    public static String getProperty(String key){
        return PROP.getProperty(key);
    }
}
