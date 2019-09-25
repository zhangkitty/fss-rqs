package com.znv.fssrqs.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.CropImageFilter;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by dongzelong on  2019/8/19 14:06.
 *
 * @author dongzelong
 * @version 1.0
 * @Description TODO
 */
@Slf4j
public class DownloadFileByUrl {
    /**
     * 根据传入的url读取目标文件二进制数组
     *
     * @param fileUrl 文件url
     * @return 文件二进制数组
     */
    public static byte[] downLoadByUrl(String fileUrl) {
        CloseableHttpClient client = HttpClientPool.getInstance().getHttpClient();
        String[] arr = fileUrl.split("\\?");
        if (fileUrl.contains("http") && fileUrl.contains("/group")&&fileUrl.contains("/M")) {

        }else {
            fileUrl = String.format("%s?%s", arr[0], URLEncoder.encode(arr[1]));
        }

        HttpGet get = new HttpGet(fileUrl);
        get.setHeader("Connection", "keep-alive");
        get.setHeader("Content-Type", "charset=utf-8");
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        DataInputStream dis = null;
        try {
            CloseableHttpResponse chc = client.execute(get);
            dis = new DataInputStream(chc.getEntity().getContent());
            byte[] bits = new byte[512];
            int n = 0;
            while ((n = dis.read(bits)) != -1) {
                outStream.write(bits, 0, n);
            }
            byte[] data = outStream.toByteArray();
            return data;
        } catch (Exception e1) {
            log.error("", e1);
            return null;
        } finally {
            get.releaseConnection();
            if (dis != null) {
                try {
                    dis.close();
                } catch (IOException e) {
                    log.error("", e);
                }
            }
        }
    }

    public static String getBase64ImgByUrl(String fileUrl) {
        String base64Data = "";
        byte[] data = downLoadByUrl(fileUrl);
        if (data != null && data.length > 0) {
            base64Data = Base64.encodeBase64String(data);
        }
        return base64Data;

    }

    public static String getBase64ImgByUrl(String fileUrl, int x, int y, int w, int h, int srcWidth, int srcHeight) {
        String base64Data = "";
        byte[] data = cutImage(fileUrl, x, y, w, h, srcWidth, srcHeight);
        if (data != null && data.length > 0) {
            base64Data = Base64.encodeBase64String(data);
        }
        return base64Data;

    }

    /**
     * 图片切割
     *
     * @param imagePath 原图地址
     * @param x         目标切片坐标 X轴起点
     * @param y         目标切片坐标 Y轴起点
     * @param w         目标切片 宽度
     * @param h         目标切片 高度
     */
    public static byte[] cutImage(String imagePath, int x, int y, int w, int h, int srcWidth, int srcHeight) {
        try {
            Image img;
            ImageFilter cropFilter;
            // 读取源图像
            URL imgurl = new URL(imagePath);
            InputStream in = imgurl.openStream();
            BufferedImage bi = ImageIO.read(in);
            // 若原图大小大于切片大小，则进行切割
            if (srcWidth >= w && srcHeight >= h) {
                Image image = bi.getScaledInstance(srcWidth, srcHeight, Image.SCALE_DEFAULT);
                cropFilter = new CropImageFilter(x, y, w, h);
                img = Toolkit.getDefaultToolkit().createImage(new FilteredImageSource(image.getSource(), cropFilter));

                BufferedImage tag = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
                Graphics g = tag.getGraphics();
                g.drawImage(img, 0, 0, null); // 绘制缩小后的图
                g.dispose();
                // 输出为文件
                ByteArrayOutputStream bts = new ByteArrayOutputStream();
                ImageIO.write(tag, "png", bts);
                byte[] data = bts.toByteArray();

                return data;
            }
        } catch (IOException e) {
            log.error("", e);
        }
        return null;
    }
}
