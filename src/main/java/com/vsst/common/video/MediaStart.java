package com.vsst.common.video;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
/**
* @description: 实时监听捕获rtsp流
* @author: Ziqiang Lee
* @date: 2021/1/12
*/
//@Component
//@EnableAsync
//public class MediaStart {
//    @Autowired
//    MediaTransfer mediaTransfer;
//
//    @PostConstruct
//    public void init(){
//        //异步加载，因为初始化时执行，live里面是死循环监听rtsp,如果不异步操作，就会卡死在初始化阶段，项目就会起不来
//        mediaTransfer.live();
//    }
//}
