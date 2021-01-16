package com.vsst;

import java.io.*;
import java.net.Socket;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import com.vsst.common.tcp.StreamUtil;
import sun.misc.BASE64Decoder;

import com.alibaba.fastjson.JSONObject;
import sun.misc.BASE64Encoder;

public class Client {
    public static final String IP_ADDR = "127.0.0.1";//服务器地址  这里要改成服务器的ip
    public static final int PORT = 6788;//服务器端口号


    public static int register(String name,String imgPath,int opNum) throws FileNotFoundException {
        BASE64Encoder encoder = new BASE64Encoder();
        String imgStr = getImgStr(imgPath);//是将图片的信息转化为base64编码
        int isRegSuccess = 0;
        while (true) {
            Socket socket = null;
            try {
                //创建一个流套接字并将其连接到指定主机上的指定端口号
                socket = new Socket(IP_ADDR, PORT);
                System.out.println("连接已经建立");
                //向服务器端发送数据
                Map<String,Object> map = new HashMap<String,Object>();
                map.put("name",name);
                map.put("img",imgStr);
                map.put("op",opNum+"");
                //将json转化为String类型
                JSONObject json = new JSONObject(map);
                String jsonString = "";
                jsonString = json.toString();
                //将String转化为byte[]
                //byte[] jsonByte = new byte[jsonString.length()+1];
                byte[] jsonByte = jsonString.getBytes();
                DataOutputStream outputStream = null;
                outputStream = new DataOutputStream(socket.getOutputStream());
                System.out.println("发的数据长度为:"+jsonByte.length);
                outputStream.write(jsonByte);
                outputStream.flush();
                System.out.println("传输数据完毕");
                socket.shutdownOutput();

                //读取服务器端数据
                DataInputStream inputStream = null;
                String strInputstream ="";
                inputStream = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
                strInputstream=inputStream.readUTF();
                System.out.println("输入信息为："+strInputstream);
//                JSONObject js = new JSONObject(strInputstream);
                JSONObject js = JSONObject.parseObject(strInputstream);
                System.out.println(js.get("isSuccess"));
                isRegSuccess=Integer.parseInt((String) js.get("isSuccess"));
                // 如接收到 "OK" 则断开连接
                if (js != null) {
                    System.out.println("客户端将关闭连接");
                    Thread.sleep(500);
                    break;
                }

            } catch (Exception e) {
                System.out.println("客户端异常:" + e.getMessage());
                break;
            } finally {
                if (socket != null) {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        socket = null;
                        System.out.println("客户端 finally 异常:" + e.getMessage());
                    }
                }
            }
        }
        return isRegSuccess;
    }
    /**
     * 将图片转换成Base64编码
     * @param imgFile 待处理图片
     * @return
     */
    public static String getImgStr(String imgFile) {
        // 将图片文件转化为字节数组字符串，并对其进行Base64编码处理

        InputStream in = null;
        byte[] data = null;
        // 读取图片字节数组
        try {
            in = new FileInputStream(imgFile);
            data = new byte[in.available()];
            in.read(data);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            StreamUtil.close(in);
        }
        return new String(Base64.getEncoder().encode(data));
    }

    public static void main(String[] args) throws FileNotFoundException {
        register("gongyunfei","G:/cat.jpg",1);//第三个参数为操作类型 服务器能够知道你在进行什么操作
    }
}
