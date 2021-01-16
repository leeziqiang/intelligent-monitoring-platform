package com.vsst.entity;




import cn.hutool.core.codec.Base64;

import com.alibaba.fastjson.JSON;
import com.vsst.common.response.Result;
import org.apache.commons.lang3.ArrayUtils;
import sun.security.util.ArrayUtil;

import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;



/**
* @description: 用于websocket的图像编码
* @author: Ziqiang Lee
* @date: 2021/1/12
*/
public class ImageEncoder implements Encoder.Text<Image> {
    @Override
    public String encode(Image image) throws EncodeException {
        if (image!=null && !ArrayUtils.isEmpty(image.getImageByte())){
            String base64Image = Base64.encode(image.getImageByte());
            return JSON.toJSONString(Result.ok().data("videoFrame",base64Image).message("获取视频帧成功"));
        }
        return JSON.toJSONString(Result.error().message("获取视频帧失败"));
    }

    @Override
    public void init(EndpointConfig endpointConfig) {

    }

    @Override
    public void destroy() {

    }
}
