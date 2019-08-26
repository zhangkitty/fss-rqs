package com.znv.fssrqs.util.command.ssh;

import com.jcraft.jsch.*;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;
import java.util.Vector;

@Slf4j
public class SSHCommandExecutor {
    private String ipAddress;

    private String username;

    private String password;

    public static final int DEFAULT_SSH_PORT = 22;

    private Vector<String> stdout;

    public SSHCommandExecutor(final String ipAddress, final String username, final String password) {
        this.ipAddress = ipAddress;
        this.username = username;
        this.password = password;
        stdout = new Vector<String>();
    }

    public int execute(final String command) {
        int returnCode = 0;
        JSch jsch = new JSch();
        //MyUserInfo userInfo = new MyUserInfo();
        BufferedReader input = null;
        Session session = null;
        Channel channel = null;
        try {
            session = jsch.getSession(username, ipAddress, DEFAULT_SSH_PORT);
            session.setPassword(password);
            //1.思路1 ssh版本升级问题解决,添加加密算法支持，未解决
//            Properties properties = new Properties();
//            properties.put("kex", "diffie-hellman-group1-sha1");
//            session.setConfig(properties);
            //session.setUserInfo(userInfo);
            //2.思路2:升级jsch到0.1.52+
            Properties config = new Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            session.connect();

            // Create and connect channel.
            channel = session.openChannel("exec");
            ((ChannelExec) channel).setCommand(command);

            // channel.setInputStream(null);
            input = new BufferedReader(new InputStreamReader(channel.getInputStream()));

            channel.connect();
            // System.out.println("The remote command is: " + command);

            // Get the output of remote command.
            String line = null;
            int i = 1;
            while ((line = input.readLine()) != null) {
                stdout.add(line);
            }

            // Get the return code only after the channel is closed.
            if (channel.isClosed()) {
                returnCode = channel.getExitStatus();
            }
            // Disconnect the channel and session.
            channel.disconnect();
            session.disconnect();
        } catch (JSchException e) {
            log.error("", e);
        } catch (Exception e) {
            log.error("", e);
        } finally {
            try {
                if (input != null) {
                    input.close();
                }
            } catch (IOException e) {
                log.error("", e);
            }

            if (channel != null && channel.isConnected()) {
                channel.disconnect();
            }

            if (session != null && session.isConnected()) {
                session.disconnect();
            }
        }
        return returnCode;
    }

    public Vector<String> getStandardOutput() {
        return stdout;
    }

}
