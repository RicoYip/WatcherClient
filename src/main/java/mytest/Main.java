package mytest;

import jpcap.JpcapCaptor;
import jpcap.NetworkInterface;
import jpcap.NetworkInterfaceAddress;
import mytest.cmd.OperatorClient;
import mytest.screen.ScreenShotClient;
import mytest.utils.MyUtils;
import mytest.utils.PCInfoClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    public static String url = "localhost:8080";
    private static Jedis jedis = MyUtils.getJedis();

    public static void main(String[] args) throws IOException {

        NetworkInterface[] deviceList = JpcapCaptor.getDeviceList();
        for(NetworkInterface  networkInterface : deviceList){
            System.out.println(networkInterface.description);
            for(NetworkInterfaceAddress address : networkInterface.addresses){
                System.out.println(address.address + "," + address.subnet);
            }
        }
        //开启服务
        startService();
        //注册回调
        JpcapCaptor captor = JpcapCaptor.openDevice(deviceList[5], 65535, true, 20);
        captor.loopPacket(-1,new Receiver());
    }

    private static void sendPCInfo(){
        new Thread(()->{
            try {
                while (true) {
                    PCInfoClient.sendDynamicToServer();
                    TimeUnit.SECONDS.sleep(3);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
        PCInfoClient.sendStaticToServer();
    }
    private static void startService() {
        //命令行客户端开启
        new OperatorClient();

        try {
            //屏幕监控开启
            new ScreenShotClient();
        } catch (Exception e) {
            logger.error("屏幕监控出现错误...");
            e.printStackTrace();
        }
        //发送宿主机信息
        sendPCInfo();
        //计算网卡速率
        new Thread(()->{
            while(true) {
                int start = Receiver.count;
                try {
                    Thread.sleep(1000);
                    int end = Receiver.count;
                    jedis.set("packetspeed",(end-start)+"");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }).start();
        /*
        //实时监控
        new Thread(()->{
            Socket socket = null;
            try {
                socket = new Socket("127.0.0.1",7979);
            } catch (IOException e) {
                e.printStackTrace();
            }
            while (true) {
                //截图
                Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
                try {
                    //保存截图放在本地
                    BufferedImage screenCapture = new Robot().createScreenCapture(new Rectangle(dimension.width, dimension.height));
                    String dirPath = (String) MyUtils.getProperties().get("tmp");
                    File tmpFileParent = new File(dirPath);
                    if(!tmpFileParent.exists())tmpFileParent.mkdirs();
                    File file = new File(tmpFileParent,"tmp.jpg");
                    FileOutputStream outputStream = new FileOutputStream(file);
                    ImageIO.write(screenCapture, "jpeg", outputStream);
                    //发送数据
                    FileInputStream fis = new FileInputStream(file);
                    byte[] bytes = IOUtils.toByteArray(fis);//图片二进制
                    OutputStream socketOutputStream = socket.getOutputStream();

                    socketOutputStream.write(MyUtils.intToByteArray(bytes.length));
                    socketOutputStream.write(bytes);

                    outputStream.close();
                    System.out.println("send...." + bytes.length);
                    TimeUnit.SECONDS.sleep(3);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
        */
    }
}
