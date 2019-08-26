package com.znv.fssrqs.util.command.ssh;

import com.jcraft.jsch.*;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Properties;
import java.util.Vector;

import static com.znv.fssrqs.util.command.ssh.SSHCommandExecutor.DEFAULT_SSH_PORT;

/**
 * Created by dongzelong on  2019/8/22 18:31.
 *
 * @author dongzelong
 * @version 1.0
 * @Description TODO
 */
@Slf4j
public class SSHContext {
    private String ip;
    private String userName;
    private String userPwd;
    private Session session;

    private static class SingletonHolder {
        private static SSHContext instance = new SSHContext();
    }

    public static SSHContext getInstance() {
        return SingletonHolder.instance;
    }

    public void set(String ip, String userName, String userPwd) {
        this.ip = ip;
        this.userName = userName;
        this.userPwd = userPwd;
    }

    public SSHContext(String ip, String userName, String userPwd) {
        this.ip = ip;
        this.userName = userName;
        this.userPwd = userPwd;
    }

    public SSHContext() {
    }

    /**
     * 远程登录linux的主机
     *
     * @return 登录成功返回true，否则返回false
     * @author
     */
    public Boolean login() {
        boolean flag = false;
        try {
            JSch jsch = new JSch();
            Session session = jsch.getSession(userName, ip, DEFAULT_SSH_PORT);
            session.setPassword(userPwd);
            Properties config = new Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            session.connect();
            //连接成功后,获取对应IP
            this.session = session;
            flag = true;
        } catch (Exception e) {
            log.error("login linux server failed", e);
        }
        return flag;
    }

    /**
     * 远程执行shell脚本或者命令
     *
     * @param cmd 即将执行的命令
     * @return 命令执行完后返回的结果值
     */
    public Vector<String> execute(String cmd) {
        Vector<String> stdout = new Vector<String>();
        Channel channel = null;
        try {
            if (login()) {
                channel = session.openChannel("exec");
                ((ChannelExec) channel).setCommand(cmd);
                BufferedReader input = new BufferedReader(new InputStreamReader(channel.getInputStream()));
                channel.connect();
                String line = null;
                while ((line = input.readLine()) != null) {
                    stdout.add(line);
                }

                if (channel.isClosed()) {
                    channel.getExitStatus();
                }
                channel.disconnect();
                getRealIp("cat /etc/hosts");
                session.disconnect();
            }
        } catch (Exception e) {
            log.error("do shell script failed", e);
        } finally {
            if (channel != null && channel.isConnected()) {
                channel.disconnect();
            }
            if (session.isConnected()) {
                session.disconnect();
            }
        }
        return stdout;
    }

    public void getRealIp(String cmd) {
        Channel channel = null;
        try {
            channel = session.openChannel("exec");
            ((ChannelExec) channel).setCommand(cmd);
            BufferedReader input = new BufferedReader(new InputStreamReader(channel.getInputStream()));
            channel.connect();
            String line = null;
            outer:
            while ((line = input.readLine()) != null) {
                if (!line.isEmpty()) {
                    final String[] columns = line.split("\\s+");
                    if (columns.length != 1) {
                        for (String column : columns) {
                            if (ip.equals(column)) {
                                ip = columns[0];
                                break outer;
                            }
                        }
                    }
                }
            }

            if (channel.isClosed()) {
                channel.getExitStatus();
            }
            channel.disconnect();
        } catch (Exception e) {
            log.error("", e);
        } finally {
            if (channel != null && channel.isConnected()) {
                channel.disconnect();
            }
        }
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
}
