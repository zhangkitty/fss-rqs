package com.znv.fssrqs.service.personnel.management;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.znv.fssrqs.config.HdfsConfigManager;
import com.znv.fssrqs.config.HkSdkConfig;
import com.znv.fssrqs.constant.CommonConstant;
import com.znv.fssrqs.dao.mysql.HkPersonRelationMap;
import com.znv.fssrqs.dao.mysql.LibRelationMapper;
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
            ret.put("StatusString", "请求消息字段不正确: SubImageList");
            return ret;
        }

        String imageData = jsonImageArray.getJSONObject(0).getString("Data");
        String feature = FaceAIUnitUtils.getImageFeature(imageData);
        JSONObject parseObject = JSONObject.parseObject(feature);
        if (feature == null){
            ret.put("StatusString", "未获取到图片特征值！");
            return ret;
        }
        if (!"success".equals(parseObject.getString("result"))) {
            ret.put("StatusString", "获取图片特征值失败！");
            return ret;
        }

        byte[] imagedata1 = Base64Util.decode(imageData);
        byte[] feature1 = Base64Util.decode(parseObject.getString("feature"));
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
        obj.put("control_police_category", personObject.getIntValue("ControlPoliceCategory"));
        obj.put("control_person_tel", personObject.getString("ControlPersonTel"));
        obj.put("control_person_name", personObject.getString("ControlPersonName"));
        obj.put("belong_police_station", personObject.getString("BelongPoliceStation"));
        obj.put("card_type", personObject.getIntValue("CardType"));
        obj.put("source_id", personObject.getString("SourceID"));
        obj.put("alarm_level", personObject.getIntValue("PersonAlarmLevel"));
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
        String tablename = HdfsConfigManager.getTableName(CommonConstant.PhoenixProperties.PERSON_LIST_TABLE_NAME);
        insertData.put("table_name", tablename);
        insertData.put("data", personData);
        JSONObject hbaseRet = phoenixService.insert(insertData);
        if (hbaseRet == null) {
            ret.put("StatusString", "HBase操作失败！");
            return ret;
        }

        String fssPersonId = hbaseRet.getString("personId");
        if (hkSdkConfig.getIsSwitch()) {
            Map<String, Object> map = libRelationMapper.selectOne(personObject.getInteger("LibID"));
            if (map == null || map.isEmpty()) {
                ret.put("StatusString", "部分成功，未获取到海康库ID：" + personObject.getInteger("LibID"));
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
                String statusString = "部分成功，海康库新增失败!";
                if (hkResult != null
                        && hkResult.containsKey("msg")
                        && hkResult.containsKey("code")) {
                    statusString += "错误码：" + hkResult.getIntValue("code") + "，错误信息：" + hkResult.getString("msg");
                }
                ret.put("StatusString", statusString);
                return ret;
            }
        }
        ret.put("StatusCode", 0);
        ret.put("Id", fssPersonId);
        ret.put("StatusString", "ok");

        return ret;
    }

    /**
     * 单个修改人员
     * @param personObject
     * @return
     */
    public JSONObject updatePerson(JSONObject personObject) {
        JSONObject ret = new JSONObject();
        ret.put("RequestURL", "/VIID/Person");
        ret.put("StatusCode", 8);
        ret.put("StatusString", "非法JSON消息");
        if (!personObject.containsKey("PersonlibType")
                || !personObject.containsKey("Flag")
                || !personObject.containsKey("LibID")
                || !personObject.containsKey("PersonID")) {
            return ret;
        }
        JSONObject obj = new JSONObject();
        JSONArray jsonImageArray = personObject.getJSONArray("SubImageList");
        if (jsonImageArray != null
                && (jsonImageArray.getJSONObject(0) == null)
                && StringUtils.isEmpty(jsonImageArray.getJSONObject(0).getString("Data"))) {
            String imageData = jsonImageArray.getJSONObject(0).getString("Data");
            String feature = FaceAIUnitUtils.getImageFeature(imageData);
            JSONObject parseObject = JSONObject.parseObject(feature);
            if (feature == null){
                ret.put("StatusString", "未获取到图片特征值!");
                return ret;
            }
            if (!"success".equals(parseObject.getString("result"))) {
                ret.put("StatusString", "获取图片特征值失败！");
                return ret;
            }

            byte[] imagedata1 = Base64Util.decode(imageData);
            byte[] feature1 = Base64Util.decode(parseObject.getString("feature"));
            obj.put("person_img", imagedata1);
            obj.put("feature", feature1);
        }

        ret.put("StatusCode", 1);
        SimpleDateFormat formatter = new SimpleDateFormat(DataConvertUtils.DEFAULT_DATE_TIME_FORMAT);
        String now = formatter.format(Calendar.getInstance().getTime());
        obj.put("person_id", personObject.getString("PersonID"));
        if (personObject.containsKey("Name")) {
            obj.put("person_name", personObject.getString("Name"));
        }
        if (personObject.containsKey("Birth")) {
            obj.put("birth", personObject.getString("Birth"));
        }
        if (personObject.containsKey("EthicCode")) {
            obj.put("nation", CountryCodeUtil.ethnicCodeTransFromGB1400(personObject.getString("EthicCode")));
        }
        if (personObject.containsKey("NationalityCode")) {
            obj.put("country", CountryCodeUtil.countryCodeTransFromGB1400(personObject.getString("NationalityCode")));
        }
        if (personObject.containsKey("PositiveUrl")) {
            obj.put("positive_url", personObject.getString("PositiveUrl"));
        }
        if (personObject.containsKey("NegativeUrl")) {
            obj.put("negative_url", personObject.getString("NegativeUrl"));
        }
        if (personObject.containsKey("Addr")) {
            obj.put("addr", personObject.getString("Addr"));
        }
        if (personObject.containsKey("Tel")) {
            obj.put("tel", personObject.getString("Tel"));
        }
        if (personObject.containsKey("NatureResidence")) {
            obj.put("nature_residence", personObject.getString("NatureResidence"));
        }
        if (personObject.containsKey("RoomNumber")) {
            obj.put("room_number", personObject.getString("RoomNumber"));
        }
        if (personObject.containsKey("DoorOpen")) {
            obj.put("door_open", personObject.getIntValue("DoorOpen"));
        }
        if (personObject.containsKey("GenderCode")) {
            obj.put("sex", personObject.getIntValue("GenderCode"));
        }
        if (personObject.containsKey("ImageName")) {
            obj.put("image_name", personObject.getString("ImageName"));
        }
        if (personObject.containsKey("IDNumber")) {
            obj.put("card_id", personObject.getString("IDNumber"));
        }
        obj.put("flag", personObject.getIntValue("Flag"));
        if (personObject.containsKey("Comment")) {
            obj.put("comment", personObject.getString("Comment"));
        }
        if (personObject.containsKey("ControlStartTime")) {
            obj.put("control_start_time", personObject.getString("ControlStartTime"));
        }
        if (personObject.containsKey("ControlEndTime")) {
            obj.put("control_end_time", personObject.getString("ControlEndTime"));
        }
        obj.put("modify_time", now);
        if (personObject.containsKey("CommunityID")) {
            obj.put("community_id", personObject.getString("CommunityID"));
        }
        if (personObject.containsKey("CommunityName")) {
            obj.put("community_name", personObject.getString("CommunityName"));
        }
        if (personObject.containsKey("ControlCommunityID")) {
            obj.put("control_community_id", personObject.getString("ControlCommunityID"));
        }
        if (personObject.containsKey("ControlPersonID")) {
            obj.put("control_person_id", personObject.getString("ControlPersonID"));
        }
        if (personObject.containsKey("ControlEventID")) {
            obj.put("control_event_id", personObject.getString("ControlEventID"));
        }
        obj.put("personlib_type", personObject.getIntValue("PersonlibType"));
        if (personObject.containsKey("ControlPoliceCategory")) {
            obj.put("control_police_category", personObject.getIntValue("ControlPoliceCategory"));
        }
        if (personObject.containsKey("ControlPersonTel")) {
            obj.put("control_person_tel", personObject.getString("ControlPersonTel"));
        }
        if (personObject.containsKey("ControlPersonName")) {
            obj.put("control_person_name", personObject.getString("ControlPersonName"));
        }
        if (personObject.containsKey("BelongPoliceStation")) {
            obj.put("belong_police_station", personObject.getString("BelongPoliceStation"));
        }
        if (personObject.containsKey("CardType")) {
            obj.put("card_type", personObject.getIntValue("CardType"));
        }
        if (personObject.containsKey("PersonAlarmLevel")) {
            obj.put("alarm_level", personObject.getIntValue("PersonAlarmLevel"));
        }
        if (personObject.containsKey("SourceID")) {
            obj.put("source_id", personObject.getString("SourceID"));
        }
        if (personObject.containsKey("Description")) {
            obj.put("description", personObject.getString("Description"));
        }
        if (personObject.containsKey("LibID")) {
            obj.put("lib_id", personObject.getInteger("LibID"));
        }

        Iterator<Map.Entry<String, Object>> iterator = obj.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Object> e = iterator.next();
            if (e.getValue() == null) {
                iterator.remove();
            }
        }
        JSONObject personData = new JSONObject();
        personData.put("lib_id", personObject.getInteger("LibID"));
        personData.put("original_lib_id", personObject.getInteger("LibID"));
        personData.put("personlib_type", personObject.getInteger("PersonlibType"));
        personData.put("data", obj);

        JSONObject insertData = new JSONObject();
        insertData.put("id", CommonConstant.PhoenixProtocolId.QUERY_PERSON_LIST);
        String tablename = HdfsConfigManager.getTableName(CommonConstant.PhoenixProperties.PERSON_LIST_TABLE_NAME);
        insertData.put("table_name", tablename);
        insertData.put("data", personData);
        JSONObject hbaseRet = phoenixService.update(insertData);
        if (hbaseRet == null) {
            ret.put("StatusString", "HBase更新失败");
            return ret;
        }

        ret.put("StatusCode", 0);
        ret.put("Id", personObject.getString("PersonID"));
        ret.put("StatusString", "ok");
        return ret;
    }

    /**
     * 删除人员
     * @param libID
     * @param personID
     * @return
     */
    public JSONObject deletePerson(String libID, String personID) {
        JSONObject ret = new JSONObject();
        ret.put("RequestURL", "/VIID/Person");
        ret.put("StatusCode", 1);

        JSONObject data = new JSONObject();
        JSONObject deleteData = new JSONObject();
        JSONObject personData = new JSONObject();
        deleteData.put("id", CommonConstant.PhoenixProtocolId.QUERY_PERSON_LIST);
        String tableName = HdfsConfigManager.getTableName(CommonConstant.PhoenixProperties.PERSON_LIST_TABLE_NAME);
        data.put("person_id",personID);
        personData.put("data", data);
        personData.put("lib_id", libID);
        deleteData.put("data", personData);
        deleteData.put("table_name", tableName);
        phoenixService.delete(deleteData);

        ret.put("StatusCode", 0);
        ret.put("Id", personID);
        ret.put("StatusString", "ok");

        if (hkSdkConfig.getIsSwitch()) {
            Map<String, Object> map = hkPersonRelationMap.getByFssPersonId(personID);
            if (map == null || map.isEmpty()) {
                ret.put("StatusString", "部分成功，未获取到海康库人员ID：" + personID);
                return ret;
            }
            String hkPersonID = (String) map.get("hk_person_id");
            JSONObject hkResult = hkSDKService.delHkPerson(hkPersonID);
            if (hkResult != null
                    && hkResult.containsKey("code")) {
                Map<String, Object> mapDbParams = new HashMap<>();
                mapDbParams.put("fssPersonId", personID);
                hkPersonRelationMap.delete(mapDbParams);
            } else {
                String statusString = "部分成功，海康库未成功。";
                if (hkResult != null
                        && hkResult.containsKey("msg")
                        && hkResult.containsKey("code")) {
                    statusString += "错误码:" + hkResult.getIntValue("code") + ",错误信息：" + hkResult.getString("msg");
                }
                ret.put("StatusString", statusString);
                return ret;
            }
        }
        return ret;
    }
}