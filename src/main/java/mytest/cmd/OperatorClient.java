package mytest.cmd;


import mytest.utils.MyUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;

/**
 *  命令操作客户端
 */
public class OperatorClient {

    private static final Logger logger = LoggerFactory.getLogger(OperatorClient.class);

    //流对象
    public static OutputStream outputStream;
    public static InputStream inputStream;


    public OperatorClient(){
        logger.info("命令行客户端开始监控...");
        startService();
    }

    public static void startService(){
        new Thread(()->{
            try {
                Socket socket = new Socket("127.0.0.1",7878);
                outputStream = new BufferedOutputStream(socket.getOutputStream());
                inputStream = new BufferedInputStream(socket.getInputStream());
                //一直读取
                while (true) {
                    String read = read();
                    if (read.equals("server ready...")){
                        System.out.println(read);
                    }else {
                        String execute = execute(read);
                        out(execute);
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }
    public static void out(String data) throws IOException {
        if(null == outputStream){
            throw new RuntimeException("流未建立");
        }
        byte[] _data = data.getBytes();
        outputStream.write(MyUtils.intToByteArray(_data.length));
        outputStream.write(_data);
        outputStream.flush();
    }

    public static String read() throws IOException {
        if(null == inputStream){
            throw new RuntimeException("流未建立");
        }
        byte[] length = new byte[4];
        inputStream.read(length);
        byte[] data = new byte[MyUtils.byteArrayToInt(length)];
        inputStream.read(data);
        return new String(data);
    }

    public static String execute(String cmd) throws IOException {
        Process process = Runtime.getRuntime().exec(cmd);
        return IOUtils.toString(process.getInputStream(),"gbk");
    }

    public static void main(String[] args) {
        startService();
    }


}
