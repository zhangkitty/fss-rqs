package com.znv.fssrqs.service.personnel.management;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.znv.fssrqs.common.Consts;
import com.znv.fssrqs.config.EsBaseConfig;
import com.znv.fssrqs.config.HkSdkConfig;
import com.znv.fssrqs.config.PersonConfig;
import com.znv.fssrqs.constant.CommonConstant;
import com.znv.fssrqs.dao.mysql.HkPersonRelationMap;
import com.znv.fssrqs.dao.mysql.LibRelationMapper;
import com.znv.fssrqs.elasticsearch.ElasticSearchClient;
import com.znv.fssrqs.service.hbase.PhoenixService;
import com.znv.fssrqs.util.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 人员库服务类
 */
@Service
@Slf4j
public class VIIDPersonService {
    @Autowired
    private VIIDHKSDKService hkSDKService;

    @Autowired
    private HkSdkConfig hkSdkConfig;

    @Autowired
    private ElasticSearchClient elasticSearchClient;

    @Autowired
    private LibRelationMapper libRelationMapper;

    @Autowired
    private HkPersonRelationMap hkPersonRelationMap;

    @Autowired
    private PhoenixService phoenixService;

    /**
     * 单个新增人员
     * @param personObject
     * @return
     */
    public JSONObject addPerson(JSONObject personObject) {
        JSONObject ret = new JSONObject();
        ret.put("RequestURL", "/VIID/Person");
        ret.put("StatusCode", 8);
        ret.put("StatusString", "Invalid JSON Content");
        if (!personObject.containsKey("PersonlibType")
                || !personObject.containsKey("Flag")
                || !personObject.containsKey("LibID")) {
            return ret;
        }
        JSONArray jsonImageArray = personObject.getJSONArray("SubImageList");
        if (jsonImageArray == null
                || (jsonImageArray.getJSONObject(0) == null)
                || StringUtils.isEmpty(jsonImageArray.getJSONObject(0).getString("Data"))) {
            ret.put("StatusString", "Invalid JSON Content: SubImageList");
            return ret;
        }

        String imageData = jsonImageArray.getJSONObject(0).getString("Data");
        String feature = FaceAIUnitUtils.getImageFeature(imageData);
        JSONObject parseObject = JSONObject.parseObject(feature);
        if (feature == null){
            ret.put("StatusString", "Get image feature null!");
            return ret;
        }
        if (!Consts.FinalKeyCode.SUCCESS.equals(parseObject.getString(Consts.FinalKeyCode.RESULT))) {
            ret.put("StatusString", "Get image feature failed");
            return ret;
        }

        byte[] imagedata1 = Base64Util.decode(imageData);
        byte[] feature1 = Base64Util.decode(parseObject.getString(Consts.FinalKeyCode.FEATURE));
        SimpleDateFormat formatter = new SimpleDateFormat(DataConvertUtils.DEFAULT_DATE_TIME_FORMAT);
        String now = formatter.format(Calendar.getInstance().getTime());

        JSONObject obj = new JSONObject();

        obj.put("person_img", imagedata1);
        obj.put("feature", feature1);
        obj.put("person_name", personObject.getString("Name"));
        obj.put("birth", personObject.getString("Birth"));
        obj.put("nation", CountryCodeUtil.ethnicCodeTransFromGB1400(personObject.getString("EthicCode")));
        obj.put("country", CountryCodeUtil.countryCodeTransFromGB1400(personObject.getString("NationalityCode")));
        obj.put("positive_url", personObject.getString("PositiveUrl"));
        obj.put("negative_url", personObject.getString("NegativeUrl"));
        obj.put("addr", personObject.getString("Addr"));
        obj.put("tel", personObject.getString("Tel"));
        obj.put("nature_residence", personObject.getString("NatureResidence"));
        obj.put("room_number", personObject.getString("RoomNumber"));
        obj.put("door_open", personObject.getIntValue("DoorOpen"));//
        obj.put("sex", personObject.getIntValue("GenderCode"));
        obj.put("image_name", personObject.getString("ImageName"));
        obj.put("card_id", personObject.getString("IDNumber"));
        obj.put("flag", personObject.getIntValue("Flag"));
        obj.put("comment", personObject.getString("Comment"));
        obj.put("control_start_time", personObject.getString("ControlStartTime"));
        obj.put("control_end_time", personObject.getString("ControlEndTime"));
        obj.put("create_time", now);
        obj.put("modify_time", now);
        obj.put("community_id", personObject.getString("CommunityID"));
        obj.put("community_name", personObject.getString("CommunityName"));
        obj.put("control_community_id", personObject.getString("ControlCommunityID"));
        obj.put("control_person_id", personObject.getString("ControlPersonID"));
        obj.put("control_event_id", personObject.getString("ControlEventID"));
        obj.put("personlib_type", personObject.getIntValue("PersonlibType"));
        obj.put("control_police_category", personObject.getString("ControlPoliceCategory"));
        obj.put("control_person_tel", personObject.getString("ControlPersonTel"));
        obj.put("control_person_name", personObject.getString("ControlPersonName"));
        obj.put("belong_police_station", personObject.getString("BelongPoliceStation"));
        obj.put("card_type", personObject.getIntValue("CardType"));
        obj.put("description", personObject.getString("Description"));

        JSONArray data = new JSONArray();
        data.add(obj);
        JSONObject personData = new JSONObject();
        personData.put("count", 1);
        personData.put("lib_id", personObject.getInteger("LibID"));
        personData.put("personlib_type", personObject.getInteger("PersonlibType"));
        personData.put("data", data);

        JSONObject insertData = new JSONObject();
        insertData.put("id", CommonConstant.PhoenixProtocolId.QUERY_PERSON_LIST);
        String tablename = ConfigManager.getTableName(Consts.FinalKeyCode.TABLE_PERSONLIB_KEY);
        insertData.put(Consts.FinalKeyCode.TABLE_NAME, tablename);
        insertData.put("data", personData);
        JSONObject hbaseRet = phoenixService.insert(insertData);
        if (hbaseRet == null) {
            ret.put("StatusString", "Phoenix insert data failed");
            return ret;
        }

        String fssPersonId = hbaseRet.getString("personId");
        if (hkSdkConfig.isHkSwitch()) {
            Map<String, Object> map = libRelationMapper.selectOne(personObject.getInteger("LibID"));
            if (map == null || map.isEmpty()) {
                ret.put("StatusString", "add FSS ok, but add HaiKang failed! select lib relation failed: " + personObject.getInteger("LibID"));
                return ret;
            }
            String hkLibID = (String) map.get("hk_lib_id");
            JSONObject hkResult = hkSDKService.addHkPerson(personObject, hkLibID);
            if (hkResult != null
                    && hkResult.containsKey("data")
                    && hkResult.containsKey("code")
                    && 0 == hkResult.getIntValue("code")) {
                Map<String, Object> mapDbParams = new HashMap<>();
                mapDbParams.put("fssPersonId", fssPersonId);
                mapDbParams.put("hkPersonId", ((JSONObject)hkResult.get("data")).get("humanId"));
                hkPersonRelationMap.insert(mapDbParams);
            } else {
                ret.put("StatusString", "add FSS ok, but add HaiKang failed!");
                return ret;
            }
        }
        ret.put("StatusCode", 0);
        ret.put("Id", fssPersonId);
        ret.put("StatusString", "ok");

        return ret;
    }

}