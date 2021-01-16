package com.vsst.common.tcp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;

/**
* @aescription: 工具类，流关闭
* @author: Ziqiang Lee
* @date: 2020/12/24
*/
public class StreamUtil {
    public static final Logger logger = LoggerFactory.getLogger(StreamUtil.class);
    public static void close(Closeable stream){
        if(stream ==null){
            return;
        }
        try {
            stream.close();
        }catch (Exception e){
            logger.error("errors on close {}",stream.getClass().getName(),e);
        }
    }
}
