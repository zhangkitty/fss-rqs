/**
 * <pre>
 * 标  题: Sim.java.
 * 版权所有: 版权所有(C)2001-2017
 * 公   司: 深圳中兴力维技术有限公司
 * 内容摘要: // 简要描述本文件的内容，包括主要模块、函数及其功能的说明
 * 其他说明: // 其它内容的说明
 * 完成日期: 2017年9月21日
 * </pre>
 * <pre>
 * 修改记录1:
 *    修改日期：
 *    版 本 号：
 *    修 改 人：
 *    修改内容：
 * </pre>
 *
 * @version 1.6.0
 * @author ZhuHongxia
 */

package com.znv.fssrqs.util;

import com.alibaba.fastjson.JSONObject;
import com.znv.fssrqs.config.HdfsConfigManager;
import com.znv.fssrqs.constant.CommonConstant;
import org.apache.commons.codec.binary.Base64;

/**
 * @author ZhuHongxia
 */
public class SimUtil {
    private static float[] src_points = null;
    private static float[] dst_points = null;

    public static void init() {
        String srcPoints = PropertiesUtil.get(HdfsConfigManager.getProperties(), CommonConstant.SenseTime.SENSETIME_FEATURE_SRC);
        src_points = parseFloatArray(srcPoints);
        String dstPoints = PropertiesUtil.get(HdfsConfigManager.getProperties(), CommonConstant.SenseTime.SENSETIME_FEATURE_DST);
        dst_points = parseFloatArray(dstPoints);
    }

    public static float[] parseFloatArray(String tmpPoints) {
        tmpPoints = tmpPoints.substring(1, tmpPoints.length() - 1);
        String[] points = tmpPoints.split(",");
        float[] floatPoints = new float[points.length];
        for (int i = 0; i < points.length; i++) {
            floatPoints[i] = Float.parseFloat(points[i].trim());
        }
        return floatPoints;
    }

    public static JSONObject computeSimByFeature(String feature1, String feature2) {
        float data = 0.0f;
        JSONObject result = new JSONObject();
        try {
            data = SimUtil.Comp(feature1, feature2);
            result.put("errorCode", 100000);
            result.put("data", data);
        } catch (Exception e) {
            result.put("errorCode", 150001); // feature1长度不等于feature2
        }
        return result;

    }

    public static float Comp(String f1, String f2) throws Exception {
        if (f1.length() != f2.length()) {
            throw new Exception("feature size unequal");
        }

        byte[] bin1 = Base64.decodeBase64(f1);
        byte[] bin2 = Base64.decodeBase64(f2);
        return Nomalize(Dot(bin1, bin2, 12));
    }

    /**
     * @param dot
     * @return
     */
    private static float Nomalize(float score) {
        // TODO Auto-generated method stub
        if (score <= src_points[0]) {
            return dst_points[0];
        } else if (score >= src_points[src_points.length - 1]) {
            return dst_points[dst_points.length - 1];
        }

        float result = 0.0f;

        for (int i = 1; i < src_points.length; i++) {
            if (score < src_points[i]) {
                result = dst_points[i - 1] + (score - src_points[i - 1]) * (dst_points[i] - dst_points[i - 1])
                        / (src_points[i] - src_points[i - 1]);
                break;
            }
        }

        return result;
    }

    /**
     * @param bin1
     * @param bin2
     * @param i
     * @return
     * @throws Exception
     */
    private static float Dot(byte[] f1, byte[] f2, int offset) throws Exception {
        // TODO Auto-generated method stub
        if (f1.length != f2.length) {
            throw new Exception("feature length unmatch");
        }
        if (0 != (f1.length - offset) % 4) {
            throw new Exception("feature dimension is incompeleted");
        }

        if (f1.length < offset) {
            throw new Exception("feature length is too short");
        }
        int dimCnt = (f1.length - offset) / 4;
        if (0 > dimCnt) {
            throw new Exception("");
        }
        float dist = 0.0f;
        for (int i = 0; i < dimCnt; i++) {
            dist += Float.intBitsToFloat(GetInt(f1, offset)) * Float.intBitsToFloat(GetInt(f2, offset));
            offset += 4;
        }
        return dist;
    }

    /**
     * @param f1
     * @param offset
     * @return
     */
    private static int GetInt(byte[] bytes, int offset) {
        // TODO Auto-generated method stub
        return (0xff & bytes[offset]) | ((0xff & bytes[offset + 1]) << 8) | ((0xff & bytes[offset + 2]) << 16)
                | ((0xff & bytes[offset + 3]) << 24);
    }

}
