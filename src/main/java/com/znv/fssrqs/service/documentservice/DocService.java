package com.znv.fssrqs.service.documentservice;

import com.alibaba.fastjson.JSONObject;
import com.znv.fssrqs.dao.mysql.MDeviceDao;
import com.znv.fssrqs.util.HttpUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * @author zhangcaochao
 * @Description TODO
 * @Date new Date() 下午3:49
 */

@Service
public class DocService {

    @Autowired
    private MDeviceDao mDeviceDao;

    public JSONObject getDocOverView(){
        Integer count = mDeviceDao.getCameralCount();

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("gte","now-1h/d");
        JSONObject jsonObject1 = new JSONObject();
        jsonObject1.put("fused_time",jsonObject);
        JSONObject jsonObject2 = new JSONObject();
        jsonObject2.put("range",jsonObject1);
        JSONObject jsonObject3 = new JSONObject();
        jsonObject3.put("query",jsonObject2);

        String result = "";
        String result1 = "";
        String result2 = "";
        try {
            //今日新增档案
            result = HttpUtils.sendPostData(jsonObject3.toJSONString(),"http://lv217.dct-znv.com:9200/fused_data_realtime/fused/_search");
            //档案汇聚总数
            result1 = HttpUtils.sendPostData("","http://lv217.dct-znv.com:9200/fused_data_realtime/fused/_search");
            //图片记录数量
            result2 = HttpUtils.sendPostData("","http://lv217.dct-znv.com:9200/fused_src_data_realtime/fused/_search");
        } catch (IOException e) {
            e.printStackTrace();
        }

        JSONObject resultJSON = new JSONObject();
        resultJSON.put("Today",JSONObject.parseObject(result).getJSONObject("hits").getString("total"));
        resultJSON.put("Doc",JSONObject.parseObject(result1).getJSONObject("hits").getString("total"));
        resultJSON.put("Picture",JSONObject.parseObject(result2).getJSONObject("hits").getString("total"));
        resultJSON.put("CameralCount",count);

        return resultJSON;
    }

    public JSONObject getDocCurve(){
        String json = "{\n" +
                "  \"aggs\": {\n" +
                "    \"month\": {\n" +
                "      \"date_histogram\": {\n" +
                "        \"field\": \"fused_time\",\n" +
                "        \"interval\": \"month\",\n" +
                "        \"format\": \"yyyy-MM-dd\"\n" +
                "      }\n" +
                "    },\n" +
                "    \"day\": {\n" +
                "      \"date_histogram\": {\n" +
                "        \"field\": \"fused_time\",\n" +
                "        \"interval\": \"day\",\n" +
                "        \"format\": \"yyyy-MM-dd\"\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "}";
        String reslut = "";
        try {
            reslut = HttpUtils.sendPostData(json,"http://lv217.dct-znv.com:9200/fused_data_realtime/fused/_search");
        } catch (IOException e) {
            e.printStackTrace();
        }

        return JSONObject.parseObject(reslut).getJSONObject("aggregations");
    }
}
