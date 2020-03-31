package mytest;

import jpcap.JpcapCaptor;
import jpcap.JpcapSender;
import jpcap.NetworkInterface;
import jpcap.packet.ARPPacket;
import jpcap.packet.EthernetPacket;
import mytest.utils.MyUtils;

public class ArpDemo {
    public static void main(String[] args) throws Exception{

        NetworkInterface[] devices = JpcapCaptor.getDeviceList();

        byte[] desMac = new byte[]{(byte)0x3c,(byte)0x52,(byte)0x82,(byte)0x59,(byte)0x8a,(byte)0xcf};

        for(NetworkInterface n : devices)
        {
            System.out.println(n.name + "     |     " + n.description);
        }
        NetworkInterface device = devices[3];
        String ip = devices[3].addresses[0].address.toString();
        System.out.println("ip:" + ip);

        ARPPacket arpPacket = new ARPPacket();
        arpPacket.hardtype = ARPPacket.HARDTYPE_ETHER;
        arpPacket.prototype = ARPPacket.PROTOTYPE_IP;
        arpPacket.hlen = 6;
        arpPacket.plen = 4;
        arpPacket.operation = ARPPacket.ARP_REPLY;
        arpPacket.sender_hardaddr =  device.mac_address;
        arpPacket.sender_protoaddr = MyUtils.IPv4toByteArray("192.168.31.108");
        arpPacket.target_protoaddr = MyUtils.IPv4toByteArray("192.168.31.224");
        arpPacket.target_hardaddr = desMac;

        EthernetPacket ethernetPacket = new EthernetPacket();
        ethernetPacket.dst_mac = desMac;
        ethernetPacket.src_mac = device.mac_address;
        ethernetPacket.frametype = EthernetPacket.ETHERTYPE_ARP;

        arpPacket.datalink = ethernetPacket;

        JpcapSender.openDevice(device).sendPacket(arpPacket);
    }
}
