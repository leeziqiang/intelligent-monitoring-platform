package com.vsst;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Date;
import java.text.SimpleDateFormat;

import org.json.JSONArray;
import org.json.JSONException;
import com.alibaba.fastjson.JSONObject;

import sun.misc.BASE64Decoder;


public class Server {
    public static final int PORT = 6788;//监听的端口号

    public static void main(String[] args) {
        System.out.println("服务器启动...\n");
        //  System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        Server server = new Server();
        server.init();
    }

    public void init() {
        try {
            ServerSocket serverSocket = new ServerSocket(PORT);
            while (true) {
                // 一旦有堵塞, 则表示服务器与客户端获得了连接
                Socket client = serverSocket.accept();
                // 处理这次连接
                new HandlerThread(client);
            }
        } catch (Exception e) {
            System.out.println("服务器异常: " + e.getMessage());
        }
    }

    private class HandlerThread implements Runnable {
        private Socket socket;
        public HandlerThread(Socket client) {
            socket = client;
            new Thread(this).start();
        }

        public void run() {

            try {
                // 读取客户端数据
                System.out.println("客户端数据已经连接");
                DataInputStream inputStream = null;
                DataOutputStream outputStream = null;
                String strInputstream ="";
                inputStream =new DataInputStream(socket.getInputStream());
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                byte[] by = new byte[2048];
                int n;
                while((n=inputStream.read(by))!=-1){
                    baos.write(by,0,n);
                }
                strInputstream = new String(baos.toByteArray());
//                System.out.println("接受到的数据长度为："+strInputstream);
                socket.shutdownInput();
//                inputStream.close();
                baos.close();


                // 处理客户端数据
                //将socket接受到的数据还原为JSONObject
                JSONObject json = JSONObject.parseObject(strInputstream);
                int op =Integer.parseInt((String)json.get("op"));
                System.out.println(op);
                switch(op){

                    //op为1 表示收到的客户端的数据为注册信息     op为2表示收到客户端的数据为检索信息

                    //当用户进行的操作是注册时
                    case 1: String imgStr = json.getString("img");
                        String name   = json.getString("name");
                        //isSuccess 表示是否注册成功
                        String isSuccess="1";
                        // System.out.println("imgStr:"+imgStr);
                        //用系统时间作为生成图片的名字   格式为yyyy-MM-dd-HH-mm-ss
                        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
                        String imgName = df.format(new Date());
                        GenerateImage(imgStr,"G:\\"+imgName+".zip");
                        //do something to process this image
                        //if success, return set isSuccess "1"
                        //else set "0"
                        System.out.println(name);
                        System.out.println("服务器接受数据完毕");

                        // 向客户端回复信息  --json对象//to be continued;
                        Map<String, Object> map = new HashMap<String, Object>();
                        map.put("isSuccess", isSuccess);
                        json = new JSONObject(map);
                        String jsonString = json.toString();
                        outputStream = new DataOutputStream(new BufferedOutputStream (socket.getOutputStream()));
                        outputStream.writeUTF(jsonString);
                        outputStream.flush();
                        outputStream.close();
                        System.out.println("注册完成");
                        break;
                }

                outputStream.close();
            } catch (Exception e) {
                System.out.println("服务器 run 异常: " + e.getMessage());
            } finally {
                if (socket != null) {
                    try {
                        socket.close();
                    } catch (Exception e) {
                        socket = null;
                        System.out.println("服务端 finally 异常:" + e.getMessage());
                    }
                }
            }
        }

    }
    public static boolean GenerateImage(String imgStr, String output)
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

}
