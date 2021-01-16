package com.vsst.common.response;
/**
* @aescription: 返回前台结果json数据打包的统一格式
* @author: Ziqiang Lee
* @date: 2020/12/24
*/
public interface CustomizeResultCode {
    /**
     * 获取错误状态码
     * @return 错误状态码
     */
    Integer getCode();

    /**
     * 获取错误信息
     * @return 错误信息
     */
    String getMessage();}
