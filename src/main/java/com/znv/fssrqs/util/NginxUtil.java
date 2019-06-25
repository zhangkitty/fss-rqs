package com.znv.fssrqs.util;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

import com.znv.fssrqs.config.FastDfsConfig;

/**
 * Created by dongzelong on  2019/6/25 15:01.
 *
 * @author dongzelong
 * @version 1.0
 * @Description TODO
 */
public class NginxUtil {
    public static class Nginx {
        private String ip;

        private int port;

        private boolean isOnline;

        public String getIp() {
            return ip;
        }

        public void setIp(String ip) {
            this.ip = ip;
        }

        public int getPort() {
            return port;
        }

        public void setPort(int port) {
            this.port = port;
        }

        public boolean isOnline() {
            return isOnline;
        }

        public void setOnline(boolean isOnline) {
            this.isOnline = isOnline;
        }

    }

    private List<Nginx> nginxList = new CopyOnWriteArrayList<>();
    private List<Nginx> nginxOffline = new CopyOnWriteArrayList<>();
    private Timer timer = new Timer();
    private static NginxUtil nginxUtil = new NginxUtil();

    public static NginxUtil getInstance() {
        return nginxUtil;
    }

    public Nginx getIddr() {
        int len = nginxList.size();
        Random r = new Random(System.currentTimeMillis());
        return nginxList.get(r.nextInt(len));
    }

    public NginxUtil() {
        List<String> nginxes = SpringContextUtil.getCtx().getBean(FastDfsConfig.class).getTrackers();
        for (String ipp : nginxes) {
            String ip = ipp.split(":")[0];
            Nginx nginx = new Nginx();
            nginx.setIp(ip);
            nginx.setPort(80);
            try {
                HttpUtils.sendGet(String.format("http://%s:%s/", nginx.getIp(), nginx.getPort()));
            } catch (Exception e) {
                nginx.setOnline(false);
                nginxOffline.add(nginx);
                continue;
            }
            this.nginxList.add(nginx);
        }
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                for (Nginx ir : NginxUtil.this.nginxList) {
                    try {
                        HttpUtils.sendGet(String.format("http://%s:%s/", ir.getIp(), ir.getPort()));
                    } catch (Exception e) {
                        ir.setOnline(false);
                        NginxUtil.this.nginxList.remove(ir);
                        nginxOffline.add(ir);
                        continue;
                    }
                }

                for (Nginx ir : nginxOffline) {
                    try {
                        HttpUtils.sendGet(String.format("http://%s:%s/", ir.getIp(), ir.getPort()));
                        NginxUtil.this.nginxList.add(ir);
                        ir.setOnline(true);
                        nginxOffline.remove(ir);
                    } catch (Exception e) {
                        continue;
                    }
                }
            }
        }, new Date(), 15000);
    }
}
