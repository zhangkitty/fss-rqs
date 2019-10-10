package com.znv.fssrqs.service.reid;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.znv.fssrqs.constant.CommonConstant;
import com.znv.fssrqs.dao.mysql.MDeviceDao;
import com.znv.fssrqs.elasticsearch.ElasticSearchClient;
import com.znv.fssrqs.service.reid.dto.ReidTraceParams;
import com.znv.fssrqs.util.HttpUtils;
import net.bytebuddy.asm.Advice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author zhangcaochao
 * @Description TODO
 * @Date new Date() 上午11:22
 */


@Service
public class ReidTraceService {

    @Autowired
    private ElasticSearchClient elasticSearchClient;

    @Autowired
    private MDeviceDao mDeviceDao;

    public ArrayList getTrace(String fusedId){

        JSONObject fused = new JSONObject();
        fused.put("fused_id",fusedId);

        JSONObject term = new JSONObject();
        term.put("term",fused);

        JSONObject filter = new JSONObject();
        filter.put("filter",term);

        JSONObject bool = new JSONObject();
        bool.put("bool",filter);

        JSONObject query = new JSONObject();
        query.put("query",bool);

        StringBuffer sb = new StringBuffer();
        sb.append("http://")
                .append(elasticSearchClient.getHost())
                .append(":")
                .append(elasticSearchClient.getPort())
                .append("/fused_src_data_realtime/fused/_search");

        System.out.println(query.toJSONString());
        String str="";
        try {
            str = HttpUtils.sendPostData(query.toJSONString(),sb.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        JSONObject result = JSONObject.parseObject(str);
        Integer integer = result.getJSONObject("hits").getInteger("total");
        query.put("from",0);
        query.put("size",integer);

        try {
            str = HttpUtils.sendPostData(query.toJSONString(),sb.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        result = JSONObject.parseObject(str);
        ArrayList arrayList  = result.getJSONObject("hits").getJSONArray("hits").stream().map(v->((JSONObject)v).getJSONObject("_source")).collect(Collectors.toCollection(ArrayList::new));
        ArrayList<String> arrayList1 = (ArrayList<String>) arrayList.stream().map(v->((JSONObject)v).getString("camera_id")).collect(Collectors.toList());
        List<String> list = mDeviceDao.getDeviceBatch(arrayList1);
        return arrayList;

    }

    public static <T> Predicate<T> distinctByKey(Function<? super T, Object> keyExtractor) {
        Map<Object, Boolean> seen = new ConcurrentHashMap<>();
        return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }



}
