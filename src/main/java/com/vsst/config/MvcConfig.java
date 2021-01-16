package com.vsst.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
/**
* @aescription: 用于解决传输图片时将后端硬盘中图片地址映射给前端访问，此项操作需要将后端端口通过frp映射出去
* @author: Ziqiang Lee
* @date: 2021/1/12
*/
@Configuration
public class MvcConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/images/**").addResourceLocations("file:G:/VSST/receiveFile/");
    }
}
