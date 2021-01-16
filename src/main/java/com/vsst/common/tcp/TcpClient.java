package com.vsst.common.tcp;

import lombok.Data;

import java.io.*;
import java.net.Socket;
import java.util.Random;

/**
* @aescription: 用来和Python服务器传送文件，此类为java客户端的代码
* @author: Ziqiang Lee
* @date: 2020/12/25
*/


@Data
public class TcpClient {

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
    public TcpClient(String host, int port, String address,String receiveFilePath) {
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
     * 向服务器发送图片
     * @param fileBufferedInputStream
     * @param socketBufferedOutputStream
     * @throws IOException
     */
    public void sendImg(BufferedInputStream fileBufferedInputStream,BufferedOutputStream socketBufferedOutputStream) throws IOException {
        //定义读取字节缓存的字节数组，大小定为1kb
        byte[] buffer = new byte[1024];
        //获取输入文件大小（单位：b）
        int fileSize = fileBufferedInputStream.available();
        /*发送表头给服务端，表头为4个字节的byte[]数据，存储所发文件的大小*/
        socketBufferedOutputStream.write(intToBytearray(fileSize));

        //每次传输文件时，实际buffer中传输数据的长度
        int buffLength=0;
        /*发送表体给服务端*/
        while ((buffLength = fileBufferedInputStream.read(buffer)) > 0) {
            fileSize-= buffLength;
            socketBufferedOutputStream.write(buffer, 0, buffLength);
            socketBufferedOutputStream.flush();
        }
        /*客户端发送文件结束标志“ok”给服务端*/
        socketBufferedOutputStream.write("ok".getBytes());
        socketBufferedOutputStream.flush();
    }

    /**
     * 判断从服务器端是否接受到所上传的图片
     * @param socketBufferedInputStream
     * @param socket
     * @return
     * @throws IOException
     */
    public Boolean isReceiveImg(BufferedInputStream socketBufferedInputStream,Socket socket) throws IOException {
        byte[] buffer = new byte[1024];
        int len=0;
        //接受服务端发送回来的接受成功标志位，如果服务端接受完成，则会项客户端发送ok标志
        if(!socket.isInputShutdown()&&(len=socketBufferedInputStream.read(buffer))>0){
            String s = new String(buffer,0,len);
            if ("ok".equalsIgnoreCase(s)){
                return true;
            }else {
                return false;
            }
        }
        return true;
    }

    /**
     * 向已连接的服务器发送一张图片，并返回检测结果，返回图片默认地址为 saveFromServerFilePath
     * 图片名随机产生
     * @param url 上传图片的路径
     * @return 检测完服务器传回图片的保存地址,远程访问后端的地址+文件路径
     */
    public String send2ReceiveImg(String url){
        /*
        定义保存和读取磁盘文件的输入输入出流
        定义用于socket通信的输入输出流
         */
        BufferedInputStream fileBufferedInputStream = null;
        BufferedOutputStream fileBufferedOutputStream = null;
        BufferedInputStream socketBufferedInputStream = null;
        BufferedOutputStream socketBufferedOutputStream = null;

//        //定义读取字节缓存的字节数组，大小定为1kb
//        byte[] buffer = new byte[1024];
        //定义TCp通信的socket
        Socket socket=null;
        try {
            socket = getSocket(host, port);

            socketBufferedInputStream=new BufferedInputStream(socket.getInputStream());
            socketBufferedOutputStream = new BufferedOutputStream(socket.getOutputStream());
            //传输给服务端的文件输入流，从磁盘读文件
            fileBufferedInputStream = new BufferedInputStream(new FileInputStream(new File(url)));

            //发送图片给服务端
            sendImg(fileBufferedInputStream,socketBufferedOutputStream);
            //接受服务端接受完成的确认信息
            if(isReceiveImg(socketBufferedInputStream,socket)){
                byte[] header = new byte[4];
                /*读取服务端的表头*/
                while (!socket.isInputShutdown()&&socketBufferedInputStream.read(header)==4){
                    int fileSize = byteArrayToInt(header);
                    System.out.println(fileSize);
                    if (fileSize < 1) {
                        //表头信息错误,跳出while，最终返回null
                        break;
                    }
                    String receiveFileName="vsst_"+System.currentTimeMillis()+"_"+new Random().nextInt(999999)+".jpg";
                    /*接受服务端的表体*/
                    String saveFilePath = receiveFilePath+receiveFileName;
                    fileBufferedOutputStream = new BufferedOutputStream(new FileOutputStream(new File(saveFilePath)));
                    int len =0;
                    byte[] buffer = new byte[1024];
                    while(fileSize > 0 && (len = socketBufferedInputStream.read(buffer)) > 0) {
                        fileSize -= len;
                        fileBufferedOutputStream.write(buffer, 0, len);
                        fileBufferedOutputStream.flush();
                    }
//                    socket.shutdownOutput();
//                    socket.shutdownInput();
//                    return "http://localhost:8081/images/"+receiveFileName;
                     return address+receiveFileName;
                }
            }else {
                return null;
            }
            return null;
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            StreamUtil.close(fileBufferedInputStream);
            StreamUtil.close(fileBufferedOutputStream);
            StreamUtil.close(socketBufferedInputStream);
            StreamUtil.close(socketBufferedOutputStream);
            StreamUtil.close(socket);
        }
        return null;
    }
}
