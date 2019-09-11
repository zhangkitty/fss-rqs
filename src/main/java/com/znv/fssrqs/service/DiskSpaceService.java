package com.znv.fssrqs.service;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.znv.fssrqs.config.HdfsConfigManager;
import com.znv.fssrqs.util.command.ssh.SSHContext;
import com.znv.fssrqs.vo.DiskInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.DependsOn;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Pattern;

/**
 * Created by dongzelong on  2019/8/22 18:25.
 *
 * @author dongzelong
 * @version 1.0
 * @Description 获取大数据机器磁盘空间定时器
 */
@Slf4j
@Service
@DependsOn("hdfsConfigManager")
public class DiskSpaceService {
    public final String DEFAULT_IP = "0.0.0.0";
    private JSONObject diskCount = new JSONObject();
    @Value("${ssh.username}")
    private String username;
    @Value("${ssh.password}")
    private String password;
    private ReentrantLock lock = new ReentrantLock();

    private String[] zkpArr = null;

    @PostConstruct
    public void init() {
        String zookeepers = HdfsConfigManager.getString("zookeeper.quorum");
        zkpArr = zookeepers.split(",");
    }

    public void doCalculation() {
        if (lock.tryLock()) {
            JSONObject diskCountJsonObject = new JSONObject();
            DiskInfo totalDiskInfo = new DiskInfo();
            totalDiskInfo.setTotal(0);
            totalDiskInfo.setUsed(0);
            totalDiskInfo.setAvailable(0);
            List<DiskInfo> diskInfos = Lists.newArrayList();
            for (int i = 0; i < zkpArr.length; i++) {
                SSHContext.getInstance().set(zkpArr[i], username, password);
                Vector<String> sshResult = SSHContext.getInstance().execute("df -m");
                if (sshResult.size() > 0) {
                    calculation(sshResult, SSHContext.getInstance().getIp(), diskCountJsonObject, totalDiskInfo, diskInfos);
                }
            }
            totalDiskInfo.setPartitionName("总磁盘容量");
            totalDiskInfo.setIP(DEFAULT_IP);
            totalDiskInfo.setUtilization(String.valueOf(String.format("%.2f", (totalDiskInfo.getUsed() * 0.1D * 10) / totalDiskInfo.getTotal() * 100)) + "%");
            diskCountJsonObject.put("Total", totalDiskInfo);
            diskCountJsonObject.put("List", diskInfos);
            diskCount = diskCountJsonObject;
        }
    }

    public JSONObject getDiskCountMap() {
        return (JSONObject) diskCount.clone();
    }

    /**
     * 计算结果
     *
     * @param sshResult
     */
    private void calculation(Vector<String> sshResult, String ip, JSONObject diskCountJsonObject, DiskInfo totalDiskInfo, List<DiskInfo> diskInfos) {
        int line = 0;
        for (String lineStr : sshResult) {
            line++;
            if (line == 1) {//表头略过
                continue;
            }

            final String[] strArray = lineStr.trim().split("\\s+");
            if (strArray.length == 1) {//忽略
                continue;
            }

            if (!strArray[0].startsWith("/dev/sd") && !isInteger(strArray[0])) {
                continue;
            }

            //home分区和引导系统分区忽略
            if (strArray[strArray.length - 1].equals("/home") || strArray[strArray.length - 1].equals("/boot")) {
                continue;
            }

            DiskInfo diskInfo = new DiskInfo();
            diskInfos.add(diskInfo);
            diskInfo.setIP(ip);
            int columnNo = 0;
            if (isInteger(strArray[0])) {//根分区
                columnNo += 1;
                diskInfo.setPartitionName("系统盘根分区(/)");
            } else {
                diskInfo.setPartitionName("数据盘分区(" + strArray[0] + ")");
            }

            for (String str : strArray) {
                ++columnNo;
                switch (columnNo) {
                    case 2: {
                        long size = Long.parseLong(str);
                        diskInfo.setTotal(size);
                        totalDiskInfo.setTotal(totalDiskInfo.getTotal() + size);
                        break;
                    }
                    case 3: {
                        long size = Long.parseLong(str);
                        diskInfo.setUsed(size);
                        totalDiskInfo.setUsed(totalDiskInfo.getUsed() + size);
                        break;
                    }
                    case 4: {
                        long size = Long.parseLong(str);
                        diskInfo.setAvailable(Long.parseLong(str));
                        totalDiskInfo.setAvailable(totalDiskInfo.getAvailable() + size);
                        break;
                    }
                    case 5: {
                        diskInfo.setUtilization(str);
                        break;
                    }
                    default:
                        break;
                }
            }
        }
    }

    /*
     * 判断是否为整数
     * @param str 传入的字符串
     * @return 是整数返回true,否则返回false
     */
    private boolean isInteger(String str) {
        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
        return pattern.matcher(str).matches();
    }

    /**
     * 启动定时任务.
     */
    @Scheduled(fixedRate = 2000)
    public void startTimer() {
        doCalculation();
    }
}
