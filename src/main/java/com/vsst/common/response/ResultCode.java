package com.vsst.common.response;
/**
* @aescription: 实现返回数据的异常状态码
* @author: Ziqiang Lee
* @date: 2020/12/24
*/
public enum ResultCode implements CustomizeResultCode{
    /**
     * 20000 代表成功
     */
    SUCCESS(2000,"成功！"),
    /**
     * 20001 代表失败
     */
    ERROR(2001,"失败"),
    /**
     * 3005 代表密码不正确
     */
    ARITHMETIC_EXCEPTION(3004,"算数异常，请重新尝试！"),
    /**
     * 3005 代表密码不正确
     */
    PASS_NOT_CORRECT(3005,"密码不正确，请重新尝试！"),
    /**
     * 3006 代表未登录
     */
    NOT_LOGIN(3006,"尚未登录"),
    /**
     * 2004 代表用户不存在！有人侵入数据库删除了
     */
    USER_NOT_FOUND_EXCEPTION(2004,"用户不存在！有人侵入数据库删除了"),
    /**
     * 2005 代表用户不存在！有人侵入数据库删除了
     */
    DEPARTMENT_NOT_FOUND_EXCEPTION(2005,"未查询到部门信息"),
    /**
     * 2005 代表没有找到这一条历史信息！有人侵入数据库删除了
     */
    INTRODUCTION_NOT_FOUND(205,"没有找到这一条历史信息！有人侵入数据库删除了"),
    /**
     * 500 代表服务器出问题了
     */
    INTERNAL_SERVER_ERROR(500,"服务器冒烟了"),
    /**
     * 404 代表请求的页面找不到了
     */
    PAGE_NOT_FOUND(404,"请求的页面找不到了"),
    /**
     * 2001 代表未知异常，请联系管理员！
     */
    UNKNOW_SERVER_ERROR(20001,"未知异常，请联系管理员！")
    ;
    private Integer code;
    private String message;


    ResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
    @Override
    public Integer getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
