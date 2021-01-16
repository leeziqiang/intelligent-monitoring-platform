package com.vsst.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
* @description: 存放图片的实体，目前只用于视频的websocket传输
* @author: Ziqiang Lee
* @date: 2021/1/12
*/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class Image {
    private byte[] imageByte;
}

