package com.znv.fssrqs.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dongzelong on  2019/6/17 17:04.
 *
 * @author dongzelong
 * @version 1.0
 * @Description TODO
 */
public class HardWare {
    private static final String[] GET_WINDOWS_CPU_SERINUMBER = new String[]{"wmic", "CPU", "GET", "ProcessorId"};
    private static final String[] GET_WINDOWS_BASEBOARD_SERINUMBER = new String[]{"wmic", "BASEBOARD", "get", "SerialNumber"};
    private static final String[] GET_NET_MAC = new String[]{"wmic", "NIC", "get", "MACADDRESS"};
    private static final String[] GET_LINUX_CPU_SERINUMBER = new String[]{"sh", "-c", "dmidecode -t 4  grep ID"};
    private static final String[] GET_LINUX_BASEBORDER_SERIAL = new String[]{"sh", "-c", "dmidecode -s system-serial-number"};
    private static final String[] GET_LINUX_NET_MAC = new String[]{"sh", "-c", "ifconfig -a"};
    private static String GET_CPU_SERIALS = "GetCPUSerials";
    private static String GET_BASE_BORDER_SERIALS = "GetBaseBoarderSerials";
    private static String GET_MAC = "GetMac";

    public HardWare() {
    }

    private static HardInfo getWindowsHardInfo() throws Exception {
        HardInfo info = new HardInfo();
        String cpuSerinumber = "";
        List<String> lst = null;
        Process proc = Runtime.getRuntime().exec(GET_WINDOWS_CPU_SERINUMBER);
        Map<String, List<String>> map = getWindowsInfo(proc);
        lst = (List) map.values().iterator().next();
        if (!lst.isEmpty()) {
            cpuSerinumber = (String) ((List) map.values().iterator().next()).get(0);
        }

        info.setCpuSerinumber(cpuSerinumber);
        String borderSerial = "";
        proc = Runtime.getRuntime().exec(GET_WINDOWS_BASEBOARD_SERINUMBER);
        map = getWindowsInfo(proc);
        lst = (List) map.values().iterator().next();
        if (!lst.isEmpty()) {
            borderSerial = (String) ((List) map.values().iterator().next()).get(0);
        }

        info.setBaseBorderSerinumber(borderSerial);
        String netMac = "";
        proc = Runtime.getRuntime().exec(GET_NET_MAC);
        map = getWindowsInfo(proc);
        lst = (List) map.values().iterator().next();
        if (!lst.isEmpty()) {
            netMac = (String) ((List) map.values().iterator().next()).get(0);
        }

        info.setNetSerinumber(netMac);
        return info;
    }

    private static HardInfo getLinuxHardInfo() throws Exception {
        HardInfo info = new HardInfo();
        Process proc = Runtime.getRuntime().exec(GET_LINUX_CPU_SERINUMBER);
        info.setCpuSerinumber(getLinuxInfo(proc, GET_CPU_SERIALS));
        proc = Runtime.getRuntime().exec(GET_LINUX_BASEBORDER_SERIAL);
        info.setBaseBorderSerinumber(getLinuxInfo(proc, GET_BASE_BORDER_SERIALS));
        proc = Runtime.getRuntime().exec(GET_LINUX_NET_MAC);
        info.setNetSerinumber(getLinuxInfo(proc, GET_MAC));
        return info;
    }

    public static String getLinuxInfo(Process proc, String cmd) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(proc.getInputStream(), "utf-8"));
        String line = null;
        boolean var4 = false;

        while ((line = br.readLine()) != null) {
            if (!"".equals(line.trim())) {
                if (GET_CPU_SERIALS.equals(cmd) && line.contains("ID")) {
                    String[] str = line.split(":");
                    return str[1];
                }

                if (GET_BASE_BORDER_SERIALS.equals(cmd)) {
                    return line;
                }

                if (GET_MAC.equals(cmd)) {
                    return line;
                }
            }
        }

        return null;
    }

    private static Map<String, List<String>> getWindowsInfo(Process proc) throws Exception {
        Map<String, List<String>> map = new HashMap();
        BufferedReader br = new BufferedReader(new InputStreamReader(proc.getInputStream(), "utf-8"));
        String line = null;
        int var4 = 0;

        while ((line = br.readLine()) != null) {
            if (!"".equals(line.trim())) {
                if (var4++ == 0) {
                    map.put(line, new ArrayList());
                } else {
                    ((List) map.values().iterator().next()).add(line);
                }
            }
        }

        return map;
    }

    public static HardInfo getHardInfo() throws Exception {
        return isWindows() ? getWindowsHardInfo() : getLinuxHardInfo();
    }

    public static void main(String[] args) throws Exception {
        HardInfo info = getHardInfo();
        System.out.println(info.getBaseBorderSerinumber());
        System.out.println(info.getCpuSerinumber());
        System.out.println(info.getNetSerinumber());
    }

    private static boolean isWindows() {
        String os = System.getProperty("os.name");
        return os.toLowerCase().startsWith("win");
    }
}
