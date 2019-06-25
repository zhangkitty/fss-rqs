package com.znv.fssrqs.client;


import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class NginxServerUrl {
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

    private List<Nginx> iaddrs = new CopyOnWriteArrayList<>();

    private List<Nginx> iaddrsUnLine = new CopyOnWriteArrayList<>();

    private Timer timer = new Timer();

    private static NginxServerUrl nginxServerUrl = new NginxServerUrl();

    public static NginxServerUrl getNginxServerUrl() {
        return nginxServerUrl;
    }

    public Nginx getIddr() {
        int len = iaddrs.size();
        Random r = new Random(System.currentTimeMillis());
        return iaddrs.get(r.nextInt(len));
    }
}
