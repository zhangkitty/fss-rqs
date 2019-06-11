package com.znv.fssrqs.service.personnel.management;

import com.znv.fssrqs.elasticsearch.ElasticSearchClient;
import net.sf.json.JSONObject;
import net.sf.json.JSONArray;
import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.entity.NStringEntity;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;

/**
 * @author zhangcaochao
 * @Description 人员列表查询
 * @Date 2019.06.10 下午3:07
 */

@Service
public class PersonListService {

    @Autowired
    private ElasticSearchClient elasticSearchClient;

    public JSONObject getPersonList() throws IOException {
        JSONObject params = new JSONObject();
        JSONObject paramsWithTempId = new JSONObject();
        params.put("sort_order2","desc");
        params.put("start_time","2010-02-22 00:00:00");
        params.put("end_time","2020-03-30 11:11:17");
        params.put("is_calcSim",false);
        params.put("sort_order1","desc");
        params.put("sort_field2","person_id");
        params.put("sort_field1","modify_time");
        params.put("is_del","0");
        params.put("from",0);
        params.put("size",10);
        params.put("lib_aggregation",true);
        paramsWithTempId.put("params",params);
        paramsWithTempId.put("id","template_person_list_search");
        HttpEntity httpEntity = new NStringEntity(paramsWithTempId.toString()
                ,ContentType.APPLICATION_JSON);
        Response response = elasticSearchClient.getInstance().getRestClient().performRequest("GET","/_search/template",Collections.emptyMap(),httpEntity);
        net.sf.json.JSONObject result = net.sf.json.JSONObject.fromObject(EntityUtils.toString(response.getEntity()));
        Integer Total = (Integer)result.getJSONObject("hits").get("total");
        JSONObject PersonlibTypes = new JSONObject();
        JSONObject LibIds  = new JSONObject();
        JSONArray PersonList = new JSONArray();
        result.getJSONObject("aggregations").getJSONObject("personlib_types").getJSONArray("buckets").forEach(v->{
            ((JSONArray)((JSONObject)((JSONObject)v).get("lib_ids")).get("buckets")).forEach(t->LibIds.put(((JSONObject)t).get("key").toString(),((JSONObject)t).get("doc_count")));
            PersonlibTypes.put(((JSONObject)v).get("key").toString(),((JSONObject)v).get("doc_count"));
        });
        result.getJSONObject("hits").getJSONArray("hits").forEach(v->{
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("LibID",((JSONObject)((JSONObject)v).get("_source")).get("lib_id"));
            jsonObject.put("PersonID",((JSONObject)((JSONObject)v).get("_source")).get("person_id"));
            jsonObject.put("Name",((JSONObject)((JSONObject)v).get("_source")).get("person_name"));
            jsonObject.put("Birth",((JSONObject)((JSONObject)v).get("_source")).get("birth"));
            jsonObject.put("EthicCode",((JSONObject)((JSONObject)v).get("_source")).get("nation"));
            jsonObject.put("NationalityCode",((JSONObject)((JSONObject)v).get("_source")).get("country"));
            jsonObject.put("PositiveUrl",((JSONObject)((JSONObject)v).get("_source")).get("positive_url"));
            jsonObject.put("NegativeUrl",((JSONObject)((JSONObject)v).get("_source")).get("negative_url"));
            jsonObject.put("Addr",((JSONObject)((JSONObject)v).get("_source")).get("addr"));
            jsonObject.put("Tel",((JSONObject)((JSONObject)v).get("_source")).get("tel"));
            jsonObject.put("NatureResidence",((JSONObject)((JSONObject)v).get("_source")).get("nature_residence"));
            jsonObject.put("RoomNumber",((JSONObject)((JSONObject)v).get("_source")).get("room_number"));
            jsonObject.put("DoorOpen",((JSONObject)((JSONObject)v).get("_source")).get("door_open"));
            jsonObject.put("GenderCode",((JSONObject)((JSONObject)v).get("_source")).get("sex"));
            jsonObject.put("ImageName",((JSONObject)((JSONObject)v).get("_source")).get("image_name"));
            jsonObject.put("Feature",((JSONObject)((JSONObject)v).get("_source")).get("feature"));
            jsonObject.put("IDNumber",((JSONObject)((JSONObject)v).get("_source")).get("card_id"));
            jsonObject.put("Flag",((JSONObject)((JSONObject)v).get("_source")).get("flag"));
            jsonObject.put("Comment",((JSONObject)((JSONObject)v).get("_source")).get("comment"));
            jsonObject.put("ControlStartTime",((JSONObject)((JSONObject)v).get("_source")).get("control_start_time"));
            jsonObject.put("ControlEndTime",((JSONObject)((JSONObject)v).get("_source")).get("control_end_time"));
            jsonObject.put("IsDel",((JSONObject)((JSONObject)v).get("_source")).get("is_del"));
            jsonObject.put("CreateTime",((JSONObject)((JSONObject)v).get("_source")).get("create_time"));
            jsonObject.put("ModifyTime",((JSONObject)((JSONObject)v).get("_source")).get("modify_time"));
            jsonObject.put("CommunityId",((JSONObject)((JSONObject)v).get("_source")).get("community_id"));
            jsonObject.put("CommunityName",((JSONObject)((JSONObject)v).get("_source")).get("community_name"));
            jsonObject.put("ControlCommunityId",((JSONObject)((JSONObject)v).get("_source")).get("control_community_id"));
            jsonObject.put("ControlPersonId",((JSONObject)((JSONObject)v).get("_source")).get("control_person_id"));
            jsonObject.put("ControlEventId",((JSONObject)((JSONObject)v).get("_source")).get("control_event_id"));
            jsonObject.put("ImageId",((JSONObject)((JSONObject)v).get("_source")).get("image_id"));
            jsonObject.put("PersonlibType",((JSONObject)((JSONObject)v).get("_source")).get("personlib_type"));
            jsonObject.put("ControlPoliceCategory",((JSONObject)((JSONObject)v).get("_source")).get("control_police_category"));
            jsonObject.put("ControlPersonTel",((JSONObject)((JSONObject)v).get("_source")).get("control_person_tel"));
            jsonObject.put("ControlPersonName",((JSONObject)((JSONObject)v).get("_source")).get("control_person_name"));
            jsonObject.put("BelongPoliceStation",((JSONObject)((JSONObject)v).get("_source")).get("belong_police_station"));
            jsonObject.put("CardType",((JSONObject)((JSONObject)v).get("_source")).get("card_type"));
            jsonObject.put("Description",((JSONObject)((JSONObject)v).get("_source")).get("description"));
            jsonObject.put("InfoKind",((JSONObject)((JSONObject)v).get("_source")).get("info_kind"));
            jsonObject.put("SourceID",((JSONObject)((JSONObject)v).get("_source")).get("source_id"));
            PersonList.add(jsonObject);
        });

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("PersonList",PersonList);
        jsonObject.put("Total",Total);
        jsonObject.put("PersonlibTypes",PersonlibTypes);
        jsonObject.put("LibIds",LibIds);

        return jsonObject;
    }
}
