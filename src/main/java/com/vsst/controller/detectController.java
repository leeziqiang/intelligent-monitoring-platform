package com.vsst.controller;


import com.vsst.common.video.MediaTransfer;
import com.vsst.entity.Connect;

import com.vsst.common.response.Result;

import com.vsst.common.tcp.TcpClientForJson;
import com.vsst.common.video.WebSocketServer;
import com.vsst.utils.FileUploadUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;


@RestController
@EnableAsync
public class detectController {
    @Autowired
    private Connect connect;


    @Autowired
    private MediaTransfer mediaTransfer;

    @PostMapping("/upload")
    public Result upload(@RequestParam("file") MultipartFile multipartFile) throws Exception {
        System.out.println("访问");
        //前端上传图片的保存在后端的地址
        String url = FileUploadUtil.upload(multipartFile, connect.getSendFile()).replace("\\\\", "/");

//        File img = new File(url);
//
//        TcpToPython tcpToPython = new TcpToPython("202.115.53.145", 6788);
//
//        String imgdetecturl = tcpToPython.tcpClient(img);
        TcpClientForJson tcpClient = new TcpClientForJson(connect.getHost(), connect.getPort(), connect.getAddress(), connect.getReceiveFile());
        Map<String, Object> jsonMap = tcpClient.send2ReceiveImg(url);
        if (jsonMap != null) {
            System.out.println("传输成功！");
            return Result.ok().data("imgData", jsonMap);
        } else {
            return Result.error().code(505).message("未得到Python服务端传回的图片！！！");
        }
    }
    @GetMapping("videoFromServer")
    public Result videoDetect(){
        System.out.println("调用后端摄像头进行检测");
        mediaTransfer.live();
        return Result.ok().message("调用后端摄像头成功");
    }
}
