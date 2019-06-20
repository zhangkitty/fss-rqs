package com.znv.fssrqs.util;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.Arrays;

/**
 * 同步读写，对涉及到修改与读写的文件需要用到此类中的方法
 * 
 * @author 徐凯华
 */
@Slf4j
public class SynReaderAndWriter {

    /**
     * 读取文件
     * 
     * @param fileName
     * @return
     */
    public static synchronized byte[] read(String fileName) {
        byte[] bits = new byte[0];
        InputStream in = null;
        try {
            File file = new File(fileName);
            in = new FileInputStream(file);
            byte[] buf = new byte[512 * 4];
            int b = 0;
            while ((b = in.read(buf)) != -1) {
                bits = Arrays.copyOf(bits, bits.length + b);
                System.arraycopy(buf, 0, bits, bits.length - b, b);
            }
        } catch (Exception e) {
            log.error("", e);
            return null;
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    log.error("", e);
                }
            }
        }
        return bits;
    }

    /**
     * 写文件
     * 
     * @param bits 字节流
     * @param fileName 文件名（带路径）
     */
    public static synchronized void write(byte[] bits, String fileName) {
        try (FileOutputStream out = new FileOutputStream(fileName);) {
            out.write(bits);
            out.flush();
        } catch (Exception e) {
            log.error("", e);
        }
    }
}
