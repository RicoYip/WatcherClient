package mytest;

import com.alibaba.fastjson.JSON;
import jpcap.PacketReceiver;
import jpcap.packet.*;
import mytest.bean.CommonPacket;
import mytest.utils.MyUtils;
import redis.clients.jedis.Jedis;

public class Receiver implements PacketReceiver {
    MyUtils myUtils = new MyUtils();
    static int count = 0;//计数，计算速率使用
    private Jedis jedis = MyUtils.getJedis();
    private String ip = MyUtils.getIp();
    private String packageType;
    private String jsonFromPackage;
    public void receivePacket(Packet packet){
        count++;
        if(packet instanceof ARPPacket){
            ARPPacket arpPacket = (ARPPacket) packet;
            packageType = "arp";
            jsonFromPackage = JSON.toJSONString(arpPacket);
        }else if(packet instanceof ICMPPacket){
            ICMPPacket icmpPacket = (ICMPPacket) packet;
            packageType = "icmp";
            jsonFromPackage = JSON.toJSONString(icmpPacket);
        }else if(packet instanceof TCPPacket){
            TCPPacket tcpPacket = (TCPPacket) packet;
            packageType = "tcp";
            jsonFromPackage = JSON.toJSONString(tcpPacket);
        }else if(packet instanceof UDPPacket){
            packageType = "udp";
            UDPPacket udpPacket = (UDPPacket) packet;
            jsonFromPackage = JSON.toJSONString(udpPacket);
        }else{
            return;
        }
        jedis.lpush("package",JSON.toJSONString(new CommonPacket(packageType,jsonFromPackage)));
    }
}
