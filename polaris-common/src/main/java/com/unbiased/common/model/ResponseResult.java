package com.unbiased.common.model;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author longjiang
 * @date 2020-01-15 5:09 下午
 * @description 统一返回格式
 **/
@Data
@AllArgsConstructor
public class ResponseResult {
    /**
     * HTTP状态码
     */
    private Integer code;

    /**
     * 返回信息
     */
    private String msg;

    /**
     * 返回的数据
     */
    private Object data;

//    public ResponseResult(int code, String msg, Object data) {
//        this.code = code;
//        this.msg = msg;
//        this.data = data;
//    }

}
