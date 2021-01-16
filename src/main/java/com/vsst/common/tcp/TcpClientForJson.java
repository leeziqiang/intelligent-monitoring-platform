package com.vsst.common.tcp;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import sun.misc.BASE64Decoder;

import java.io.*;
import java.net.Socket;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * @aescription: 用来和Python服务器传送文件，此类为java客户端的代码
 * @author: Ziqiang Lee
 * @date: 2020/12/25
 */


@Data
public class TcpClientForJson {

    private String host;
    private int port;
    private String address;//远程访问后端文件时的网址
    private String receiveFilePath;//保存Python服务端回传来的图片的存储路径

//    //前端上传的文件保存路径
//    private String sendFilePath="G:\\javaCode\\intelligent-monitoring-platform\\" +
//            "src\\main\\resources\\static\\receiveFile"; //前端上传的文件保存路径
//    //java后端保存Python服务器传回的文件的路径
//    private String receiveFilePath="G:\\javaCode\\intelligent-monitoring-platform\\" +
//            "src\\main\\resources\\static\\sendFile";

    /**
     * TCP通信类的构造器，用于与python服务器进行TCP通信
     * @param host 服务端ip地址
     * @param port 服务端端口号
     */
    public TcpClientForJson(String host, int port, String address,String receiveFilePath) {
        this.host = host;
        this.port = port;
        this.address = address;
        this.receiveFilePath = receiveFilePath;
    }

    /**
     * int型数据转为4个字节的byte数组，采用大端方式
     * @param num 需要转换的int型数据
     * @return 返回byte[4]数组
     */
    public static byte[] intToBytearray(int num) {
        return new byte[] {(byte)((num >> 24) & 0xFF), (byte)((num >> 16) & 0xFF),
                (byte)((num >> 8) & 0xFF), (byte)(num & 0xFF)};
    }

    /**
     * byte[4]型字节数据转为int型数据，采用大端方式
     * @param arr byte[4]数组
     * @return int型数据
     */
    public static int byteArrayToInt(byte[] arr) {
        return (arr[0] & 0xFF) << 24 | (arr[1] & 0xFF) << 16 |
                (arr[2] & 0xFF) << 8 | (arr[3] & 0xFF);
    }

    /**
     * 建立socket连接
     * @param host 连接的ip
     * @param port 连接的端口号
     * @return 返回建立连接的socket
     * @throws IOException 无法建立连接，直接抛出IO异常
     */
    public Socket getSocket(String host,int port) throws IOException {
        return new Socket(host, port);
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
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new String(Base64.getEncoder().encode(data));
    }

    /**
     * 向服务器发送图片
     * @param url
     * @param socketOutputStream
     * @throws IOException
     */

    public void sendImg(String url,DataOutputStream socketOutputStream) throws IOException {
        String imgStr = getImgStr(url);
        HashMap<String, Object> map = new HashMap<>();

        String[] urlArray = url.split("\\\\");
        String imgName = urlArray[urlArray.length-1];
        map.put("imgName",imgName);
        map.put("imgData",imgStr);
        JSONObject jsonObject = new JSONObject(map);
        String jsonStr = jsonObject.toString();
        socketOutputStream.write(intToBytearray(jsonStr.length()));
        socketOutputStream.flush();
        socketOutputStream.write(jsonStr.getBytes());
        socketOutputStream.flush();
    }

    /**
     * 判断从服务器端是否接受到所上传的图片
     * @param socketInputStream
     * @param socket
     * @return
     * @throws IOException
     */
    public Boolean isReceiveImg(DataInputStream socketInputStream,Socket socket) throws IOException {
        byte[] buffer = new byte[1024];
        int len=0;
        //接受服务端发送回来的接受成功标志位，如果服务端接受完成，则会项客户端发送ok标志
        if(!socket.isInputShutdown()&&(len=socketInputStream.read(buffer))>0){
            String s = new String(buffer,0,len);
            if ("ok".equalsIgnoreCase(s)){
                return true;
            }else {
                return false;
            }
        }
        return true;
    }
    public static boolean generateImage(String imgStr, String output)
    {//对字节数组字符串进行Base64解码并生成图片
        if (imgStr == null) //图像数据为空
            return false;
        BASE64Decoder decoder = new BASE64Decoder();
        try
        {
            //Base64解码
            byte[] b = decoder.decodeBuffer(imgStr);
            for(int i=0;i<b.length;++i)
            {
                if(b[i]<0)
                {//调整异常数据
                    b[i]+=256;
                }
            }
            //生成jpeg图片
            OutputStream out = new FileOutputStream(output);
            out.write(b);
            out.flush();
            out.close();
            return true;
        }
        catch (Exception e)
        {
            return false;
        }
    }

    /**
     * 向已连接的服务器发送一张图片，并返回检测结果，返回图片默认地址为 saveFromServerFilePath
     * 图片名随机产生
     * @param url 上传图片的路径
     * @return 检测完服务器传回图片的保存地址,远程访问后端的地址+文件路径
     */
    public Map<String, Object> send2ReceiveImg(String url){
        /*
        定义保存和读取磁盘文件的输入输入出流
        定义用于socket通信的输入输出流
         */


        DataInputStream socketInputStream = null;
        DataOutputStream socketOutputStream = null;
        ByteArrayOutputStream byteArrayOutputStream=null;

//        //定义读取字节缓存的字节数组，大小定为1kb
//        byte[] buffer = new byte[1024];
        //定义TCp通信的socket
        Socket socket=null;
        try {
            socket = getSocket(host, port);

            socketInputStream=new DataInputStream(socket.getInputStream());
            socketOutputStream = new DataOutputStream(socket.getOutputStream());

            //发送图片给服务端
            sendImg(url,socketOutputStream);
            //接受服务端接受完成的确认信息
            if(isReceiveImg(socketInputStream,socket)){
                byte[] header = new byte[4];
                /*读取服务端的表头*/
                while (!socket.isInputShutdown()&&socketInputStream.read(header)==4){
                    int fileSize = byteArrayToInt(header);
                    System.out.println(fileSize);
                    if (fileSize < 1) {
                        //表头信息错误,跳出while，最终返回null
                        break;
                    }
                    byteArrayOutputStream =new ByteArrayOutputStream();


                    int len =0;
                    byte[] buffer = new byte[1024];
                    while(fileSize > 0 && (len = socketInputStream.read(buffer)) > 0) {
                        fileSize -= len;
                        byteArrayOutputStream.write(buffer, 0, len);
                    }
                    String s = byteArrayOutputStream.toString();

                    JSONObject jsonObject = JSONObject.parseObject(s);
                    String imgName = jsonObject.getString("imgName");
                    String imgStr = jsonObject.getString("imgData");
                    JSONArray imgInfo = jsonObject.getJSONArray("imgInfo");
                    int length = imgName.split("\\.").length;
                    String receiveFileName=System.currentTimeMillis()+"_"+new Random().nextInt(999999)+"."+imgName.split("\\.")[length-1];
                    /*接受服务端的表体*/
                    String saveFilePath = receiveFilePath+receiveFileName;
                    generateImage(imgStr,saveFilePath);
                    HashMap<String, Object> resultMap = new HashMap<>();
                    resultMap.put("imgName",imgName);
                    resultMap.put("imgUrl",address+receiveFileName);
                    resultMap.put("imgInfo",imgInfo);
                    return resultMap;
                }
            }else {
                return null;
            }
            return null;
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            StreamUtil.close(byteArrayOutputStream);
            StreamUtil.close(socketInputStream);
            StreamUtil.close(socketOutputStream);
            StreamUtil.close(socket);
        }
        return null;
    }
}
