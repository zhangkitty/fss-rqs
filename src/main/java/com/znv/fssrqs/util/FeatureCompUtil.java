package com.znv.fssrqs.util;

import com.znv.fssrqs.constant.CommonConstant;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
public class FeatureCompUtil {
    /**
     * As an example
     *
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        FeatureCompUtil fc = new FeatureCompUtil();
        String f1 = "VU4AAAAAAAAMAgAALr/vPRVuwTuECL07hZ5tPPBTeb1mdnw9DTwDvgZjSr0PzhQ+uNXCvSYrFL09PF09pokrvKqnzb1uXY89hWO0PafOR70A93c92wfDPQlgzb2SClg8QExePuIA8b3SBhc+mLJfPRT4vbwRvRQ9ryqQPVqW1L0dLFM+SL6zPSjm+TxHNBY6jw1kvjZuvTwtaL28sZVIvcpQ6jxXJiI9qVy3vSVgdTvFiRO9RHEWvnX94jqjcI+9+hjYPRmDlL38lRs9M2vtPUzx4727nB89R19MPfbSsb3WwWi9nh7avJfI9D0LBn6+H5eMPTaqAz7mHbU9QKhaPQO8HD6Tmki+tXsEPdCfRr1nOo299gfnvb3TaT1HNlc+tnprvQO/cb1qwhS+Us/yPYEPCjwxnXY8/1+5PUbkrT3fCCC+rrVIPAds7LwbvG48VOCsPbfqAz6zJKu9udbkPdpwCr2zfEA95hhsvfudVDsRfgM+N3NAPf5wWbyqcCa867w1vVIO6Ty7nLA90f2ZvZaCHj2I66k83Xd0vaZLGb0Thse6vvKyvOrf0jwgzgE9XJZJPfbj87w9Bhg9iewrPmtqHD28bqY8Z3xpPRcfZzusy7q8K7GfPZ8o8jvs39+9fNUtPfM0cL0aj9k92J/vvWAd6T0YWUe+WIQKvktStLxiAAS8OiEJPCBS2Tw=";
        String f2 = "VU4AAAAAAAAMAgAAd6r4vWKkUzzcaXO9zoECPc5Ghr2ca2e+UKkivm/1vrwdyoG9RW0JPhuUxb0nIIc8WZOkPGGP6ryJgog8zI1AvR1gHz1E7ak9rx+TPWLfLr4RXNi9eGpYPR+0zb3dgrE8CFb3PcHGsb0i+yi8xw+mveyK1TsUSpM7oAR9vbpsybzxZWW98jwyvUuW3T392Rm++Cv3PR3H0z3Sf408Nd6/vJtFyj00aRw+TbkWPhvEJr78wg+8CHkaPXOQEz4q5XQ9JL6dPWV+mr2pSoy9cyRhPBBWGr0FiQ4+sRykPTKyzj1Z/ms+8CnPvTHQwr25oMO9ETz9vHPDZj6MohC+b2m4vGMppT3R1p29wY3lPYIE3b0/T5y9Q8SGvE72U70GP0s9QVHQPATkDD59koE7WuUWvuZFBTwBavE9zJc3vXUrKb4t+JU9Yeo/PSHPjj3Fhy69A1SEvWz4ED6/UZ08Y4ddPNK24bzBVaS9yTj6PTkikr0ExO09rH9EPEBGqT3E4+A8txmDvdk/K70m6cS9dwavPHZtRbzFh8k8phOSvfA2sD2kNze8gH6WvfCNMr1FOtM6hH6KuymG7D2kueM8jVLEveD10D2+xzW9xQQjPccpmr358Mo4LleQvSyeXbyta4I7uHmau3Ygn72cqRu+VYMsvnIDvT0PBO08Ab2rPVFolD0=";
        float sim = 0.0f;
        System.out.println("compare base64");
        sim = fc.Comp(f1, f2);
        System.out.println(String.format("sim=%f", sim));

        long beginTime = 0;
        long endTime = 0;

        int loop = 10000000;
        // compare binary
        java.util.Base64.Decoder base64decoder = java.util.Base64.getDecoder();
        byte[] bin1 = base64decoder.decode(f1);
        byte[] bin2 = base64decoder.decode(f2);

        System.out.println("compare binary");
        beginTime = System.currentTimeMillis();
        for (int i = 0; i < loop; i++) {
            sim = fc.Comp(bin1, bin2);
        }
        endTime = System.currentTimeMillis();
        System.out.println(String.format("sim=%f, time=%dms", sim, endTime - beginTime));

        // compare raw
        byte[] raw1 = base64decoder.decode(f1.substring(16));
        byte[] raw2 = base64decoder.decode(f2.substring(16));
        int len = raw1.length / 4;
        System.out.println("raw length:" + len);

        int offset = 0;
        int fraw1[] = new int[len];
        int fraw2[] = new int[len];
        for (int i = 0; i < len; i++) {
            fraw1[i] = fc.GetInt(raw1, offset);
            fraw2[i] = fc.GetInt(raw2, offset);
            offset += 4;
        }

        System.out.println("compare raw");
        beginTime = System.currentTimeMillis();
        for (int i = 0; i < loop; i++) {
            sim = fc.CompRawFeature(raw1, raw2);
        }
        endTime = System.currentTimeMillis();
        System.out.println(String.format("sim=%f, time=%dms", sim, endTime - beginTime));
    }

    /**
     * Compare two feature (support 253 model)
     *
     * @param f1 base64 encode feature
     * @param f2 base64 encode feature
     * @return similarity
     * @throws Exception
     */
    public float Comp(String f1, String f2) {
        if (f1.length() != f2.length()) {
            log.info("feature size unequal");
            return 0f;
        }

        byte[] bin1 = base64decoder.decode(f1);
        byte[] bin2 = base64decoder.decode(f2);

        return Normalize(Dot(bin1, bin2, 12));
    }

    /**
     * Compare two binary feature with 12 bytes head (support 253 model)
     *
     * @param f1 binary feature with 12 bytes head
     * @param f2 binary feature with 12 bytes head
     * @return similarity
     * @throws Exception
     */
    public float Comp(byte[] f1, byte[] f2) {
        int m1 = GetInt(f1, 0);
        int m2 = GetInt(f2, 0);
        int v1 = GetInt(f1, 4);
        int v2 = GetInt(f2, 4);
        int dim1 = GetInt(f1, 8);
        int dim2 = GetInt(f2, 8);

        if (v1 != v2) {
            // throw new Exception("version unmatch");
            log.info("version unmatch");
            return 0f;
        }

        if (0x4257aaee != m1) {
            dim1 = (dim1 - 12) / 4;
        }

        if (0x4257aaee != m2) {
            dim2 = (dim2 - 12) / 4;
        }

        if (dim1 != dim2) {
            // throw new Exception("feature dimension unmatch");
            log.info("feature dimension unmatch");
            return 0f;
        }

        // System.out.printf("m1=0x%x, m2=0x%x, v1=%d, v2=%d, dim1=%d, dim2=%d\n", m1, m2, v1, v2, dim1, dim2);

        return Normalize(Dot(f1, f2, 12));
    }

    /**
     * Compare two raw feature with no head (support 253 model)
     *
     * @param f1 binary raw feature with no head
     * @param f2 binary raw feature with no head
     * @return similarity
     * @throws Exception
     */
    public float CompRawFeature(byte[] f1, byte[] f2) {
        return Normalize(Dot(f1, f2, 0));
    }

    /**
     * Compare two raw feature with no head (support 253 model)
     *
     * @param f1 float raw feature with no head
     * @param f2 float raw feature with no head
     * @return similarity
     * @throws Exception
     */
    public float CompRawFeature(float[] f1, float[] f2) {
        return Normalize(Dot(f1, f2, 0));
    }

    /**
     * Compare two binary feature
     *
     * @param f1     binary feature
     * @param f2     binary feature
     * @param offset
     * @return similarity
     * @throws Exception
     */
    public float Comp(byte[] f1, byte[] f2, int offset) {
        return Normalize(Dot(f1, f2, offset));
    }

    /**
     * Compare two binary feature
     *
     * @param f1     float feature
     * @param f2     float feature
     * @param offset
     * @return similarity
     * @throws Exception
     */
    public float Comp(float[] f1, float[] f2, int offset) {
        return Normalize(Dot(f1, f2, offset));
    }

    public float Dot(byte[] f1, byte[] f2, int offset) {

        if (f1.length != f2.length) {
            log.debug("feature length unmatch");
            return 0f;
        }

        if (0 != (f1.length - offset) % 4) {
            log.debug("feature dimension is incompeleted");
            return 0f;
        }

        if (f1.length < offset) {
            // throw new Exception("feature length is too short");
            log.debug("feature length is too short");
            return 0f;
        }

        int dimCnt = (f1.length - offset) / 4;

        float dist = 0.0f;
        for (int i = 0; i < dimCnt; i++) {
            dist += Float.intBitsToFloat(GetInt(f1, offset)) * Float.intBitsToFloat(GetInt(f2, offset));
            offset += 4;
        }

        return dist;
    }

    public float Dot(float[] f1, float[] f2, int offset) {

        if (f1.length != f2.length) {
            // throw new Exception("feature length unmatch");
            log.debug("feature length unmatch");
            return 0f;
        }

        if (f1.length < offset) {
            // throw new Exception("feature length is too short");
            log.debug("feature length is too short");
            return 0f;
        }

        int dimCnt = (f1.length - offset);

        float dist = 0.0f;
        for (int i = offset; i < dimCnt; i++) {
            dist += f1[i] * f2[i];
        }

        return dist;
    }

    /**
     * Convert feature bytes to float array with no head
     *
     * @param bytes binary feature with 12 bytes head
     * @return float feature array
     */
    public float[] getFloatArray(byte[] bytes) {
        int offset = 12;
        if (0 != (bytes.length - offset) % 4) {
            // throw new Exception("feature dimension is incompeleted");
            log.debug("feature dimension is incompeleted");
            return new float[1];
        }

        if (bytes.length < offset) {
            // throw new Exception("feature length is too short");
            log.debug("feature length is too short");
            return new float[1];
        }

        int len = (bytes.length - offset) / 4;
        float feature[] = new float[len];
        for (int i = 0; i < len; i++) {
            feature[i] = Float.intBitsToFloat(GetInt(bytes, offset));
            offset += 4;
        }
        return feature;
    }

    public int GetInt(byte[] bytes, int offset) {
        // return (0xff & bytes[offset]) | (0xff00 & (bytes[offset + 1] << 8)) | (0xff0000 & (bytes[offset + 2] << 16))
        // | (0xff000000 & (bytes[offset + 3] << 24));
        return (0xff & bytes[offset]) | ((0xff & bytes[offset + 1]) << 8) | ((0xff & bytes[offset + 2]) << 16)
                | ((0xff & bytes[offset + 3]) << 24);
    }

    public float Normalize(float score) {
        float result = 0.0f;
        if (null == src_points || null == dst_points || src_points.length == 0 || dst_points.length == 0) {
            log.debug("src_points or dst_points is null");
            return result;
        } else {
            if (score <= src_points[0]) {
                return dst_points[0];
            } else if (score >= src_points[src_points.length - 1]) {
                return dst_points[dst_points.length - 1];
            }

            for (int i = 1; i < src_points.length; i++) {
                if (score < src_points[i]) {
                    result = dst_points[i - 1] + (score - src_points[i - 1]) * (dst_points[i] - dst_points[i - 1])
                            / (src_points[i] - src_points[i - 1]);
                    break;
                }
            }

        }
        return result;

    }

    public float reversalNormalize(float score) {
        float result = 0.0f;
        if (null == src_points || null == dst_points || src_points.length == 0 || dst_points.length == 0) {
            log.debug("src_points or dst_points is null");
            return result;
        } else {
            if (score <= dst_points[0]) {
                return src_points[0];
            } else if (score >= dst_points[dst_points.length - 1]) {
                return src_points[src_points.length - 1];
            }

            for (int i = 1; i < (dst_points.length); i++) {
                if (score < dst_points[i]) {
                    result = src_points[i - 1] + (score - dst_points[i - 1]) * (src_points[i] - src_points[i - 1])
                            / (dst_points[i] - dst_points[i - 1]);
                    break;
                }
            }
        }
        return result;
    }

    private java.util.Base64.Decoder base64decoder = java.util.Base64.getDecoder();
    //private float[] src_points = { 0.0f, 0.128612995148f, 0.236073002219f, 0.316282004118f, 0.382878988981f, 0.441266000271f, 0.490464001894f, 1.0f };
    // private float[] dst_points = { 0.0f, 0.40000000596f, 0.5f, 0.600000023842f, 0.699999988079f, 0.800000011921f, 0.899999976158f, 1.0f };
    private static float[] src_points;
    private static float[] dst_points;

    //int version = 24201;
    //private static float[] src_points = {-1.0f, 0.4f, 0.42f, 0.44f, 0.48f, 0.53f, 0.58f, 1.0f};
    //private static float[] dst_points = {0.0f, 0.4f, 0.5f, 0.6f, 0.7f, 0.85f, 0.95f, 1.0f,};
    public void setFeaturePoints(Map<String, float[]> featuresPoints) {
        if (featuresPoints.containsKey(CommonConstant.SenseTime.SENSETIME_FEATURE_SRC) && null != featuresPoints.get(CommonConstant.SenseTime.SENSETIME_FEATURE_SRC) && featuresPoints.get(CommonConstant.SenseTime.SENSETIME_FEATURE_SRC).length > 0) {
            src_points = featuresPoints.get(CommonConstant.SenseTime.SENSETIME_FEATURE_SRC);
        }
        if (featuresPoints.containsKey(CommonConstant.SenseTime.SENSETIME_FEATURE_DST) && null != featuresPoints.get(CommonConstant.SenseTime.SENSETIME_FEATURE_DST) && featuresPoints.get(CommonConstant.SenseTime.SENSETIME_FEATURE_DST).length > 0) {
            dst_points = featuresPoints.get(CommonConstant.SenseTime.SENSETIME_FEATURE_DST);
        }
    }

}
