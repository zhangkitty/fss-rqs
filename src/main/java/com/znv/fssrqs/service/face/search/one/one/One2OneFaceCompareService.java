package com.znv.fssrqs.service.face.search.one.one;

import com.alibaba.fastjson.JSONObject;
import com.znv.fssrqs.util.Base64Util;
import com.znv.fssrqs.util.FaceAIUnitUtils;
import com.znv.fssrqs.util.SimUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

/**
 * @author zhangcaochao
 * @Description TODO
 * @Date new Date() 上午9:08
 */

@Service
public class One2OneFaceCompareService {

    public JSONObject getFaceAttr(String imageData) {
        JSONObject result = new JSONObject();
        result = (JSONObject) JSONObject.parseObject(FaceAIUnitUtils.getAttribute(imageData)).get("data");
        if(result==null){
            return null;
        }
        if((Integer)result.get("gender")==1){
            result.put("gender",2);
            return result;
        }
        if((Integer)result.get("gender")==0){
            result.put("gender",1);
            return result;
        }
        if((Integer)result.get("gender")==2){
            result.put("gender",0);
            return result;
        }
        return result;
    }

    public JSONObject getCompareValue(String imageData1, String imageData2) {
        JSONObject json = new JSONObject();

        String feature1 = (String) JSONObject.parseObject(FaceAIUnitUtils.getImageFeature(imageData1)).get("feature");
        String feature2 = (String) JSONObject.parseObject(FaceAIUnitUtils.getImageFeature(imageData2)).get("feature");

        float sim = 0;
        try {
            sim = SimUtil.Comp(feature1, feature2);
            JSONObject data = new JSONObject();
            data.put("Sim", sim);
            json.put("Data", data);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return json;

    }
}
