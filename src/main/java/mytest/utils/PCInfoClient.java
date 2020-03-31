package mytest.utils;

import com.alibaba.fastjson.JSON;
import com.sun.management.OperatingSystemMXBean;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 系统监控
 */
public class PCInfoClient {

    public static String getSataicInfo() {
        HashMap map = new HashMap();
        MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
        // 椎内存使用情况
        MemoryUsage memoryUsage = memoryMXBean.getHeapMemoryUsage();
        // 操作系统
        String osName = System.getProperty("os.name");
        // 磁盘使用情况
        File[] files = File.listRoots();
        List<Map> disk = new ArrayList<>();
        for (File file : files) {
            HashMap diskMap = new HashMap();
            String total = new DecimalFormat("#.#").format(file.getTotalSpace() * 1.0 / 1024 / 1024 / 1024)
                    + "G";
            String free = new DecimalFormat("#.#").format(file.getFreeSpace() * 1.0 / 1024 / 1024 / 1024) + "G";
            String path = file.getPath();
            diskMap.put("path", path);
            diskMap.put("total", total);
            diskMap.put("free", free);
            disk.add(diskMap);
        }
        //硬盘信息
        map.put("diskInfo", disk);

        map.put("sysName", osName);
        map.put("sysStart", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                .format(new Date(ManagementFactory.getRuntimeMXBean().getStartTime())));


        return JSON.toJSONString(map);
    }

    /**
     * CPU 信息
     */
    private static String getDynamicInfo() throws InterruptedException {
        OperatingSystemMXBean osmxb = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        // 总的物理内存
        String totalMemorySize = new DecimalFormat("#.##")
                .format(osmxb.getTotalPhysicalMemorySize() / 1024.0 / 1024 / 1024) + "G";
        // 剩余的物理内存
        String freePhysicalMemorySize = new DecimalFormat("#.##")
                .format(osmxb.getFreePhysicalMemorySize() / 1024.0 / 1024 / 1024) + "G";
        // 已使用的物理内存
        String usedMemory = new DecimalFormat("#.##").format(
                (osmxb.getTotalPhysicalMemorySize() - osmxb.getFreePhysicalMemorySize()) / 1024.0 / 1024 / 1024)
                + "G";

        SystemInfo systemInfo = new SystemInfo();
        CentralProcessor processor = systemInfo.getHardware().getProcessor();
        long[] prevTicks = processor.getSystemCpuLoadTicks();
        // 睡眠1s
        TimeUnit.SECONDS.sleep(1);
        long[] ticks = processor.getSystemCpuLoadTicks();
        long nice = ticks[CentralProcessor.TickType.NICE.getIndex()]
                - prevTicks[CentralProcessor.TickType.NICE.getIndex()];
        long irq = ticks[CentralProcessor.TickType.IRQ.getIndex()]
                - prevTicks[CentralProcessor.TickType.IRQ.getIndex()];
        long softirq = ticks[CentralProcessor.TickType.SOFTIRQ.getIndex()]
                - prevTicks[CentralProcessor.TickType.SOFTIRQ.getIndex()];
        long steal = ticks[CentralProcessor.TickType.STEAL.getIndex()]
                - prevTicks[CentralProcessor.TickType.STEAL.getIndex()];
        long cSys = ticks[CentralProcessor.TickType.SYSTEM.getIndex()]
                - prevTicks[CentralProcessor.TickType.SYSTEM.getIndex()];
        long user = ticks[CentralProcessor.TickType.USER.getIndex()]
                - prevTicks[CentralProcessor.TickType.USER.getIndex()];
        long iowait = ticks[CentralProcessor.TickType.IOWAIT.getIndex()]
                - prevTicks[CentralProcessor.TickType.IOWAIT.getIndex()];
        long idle = ticks[CentralProcessor.TickType.IDLE.getIndex()]
                - prevTicks[CentralProcessor.TickType.IDLE.getIndex()];
        long totalCpu = user + nice + cSys + idle + iowait + irq + softirq + steal;

        HashMap map = new HashMap();
        map.put("cpuCount", processor.getLogicalProcessorCount());
        map.put("cpuUsedByUser", new DecimalFormat("#.##%").format(user * 1.0 / totalCpu));
        map.put("cpuUsedBySys", new DecimalFormat("#.##%").format(cSys * 1.0 / totalCpu));
        map.put("cpuNotUsed", new DecimalFormat("#.##%").format(idle * 1.0 / totalCpu));
        map.put("totalMem", totalMemorySize);
        map.put("usedMem", usedMemory);
        return JSON.toJSONString(map);
    }

    public static void sendStaticToServer(){
        MyUtils.post(getSataicInfo(),"/control/saveStaticInfo");
    }

    public static void sendDynamicToServer() throws InterruptedException {
        MyUtils.post(getDynamicInfo(),"/control/saveDynamicInfo");
    }
}