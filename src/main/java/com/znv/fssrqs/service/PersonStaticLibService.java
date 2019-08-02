package com.znv.fssrqs.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.PascalNameFilter;
import com.znv.fssrqs.dao.mysql.PersonLibMapper;
import com.znv.fssrqs.dao.mysql.UserLibRelationMapper;
import com.znv.fssrqs.entity.mysql.PersonLib;
import com.znv.fssrqs.entity.mysql.UserGroup;
import com.znv.fssrqs.entity.mysql.UserLibRelation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dongzelong on  2019/6/4 14:09.
 *
 * @author dongzelong
 * @version 1.0
 * @Description TODO
 */
@Service
public class PersonStaticLibService {
    @Autowired
    private UserGroupService userGroupService;
    @Autowired
    private PersonLibMapper personLibMapper;
    @Autowired
    private UserLibRelationMapper userLibRelationMapper;

    /**
     * 获取用户的名单库权限树
     *
     * @param params
     */
    public JSONArray getUserLibTreeByUserId(Map<String, Object> params) {
        String userId = String.valueOf(params.get("UserID"));
        JSONArray libArray = getBasicTreeArray();
        UserGroup userGroup = userGroupService.queryUserGroupByUserId(userId);
        if (userGroup != null) {
            // 静态库名支持模糊查询
            if (params.containsKey("LibName")) {
                String fuzzyLibName = "%";
                String libName = String.valueOf(params.get("LibName"));
                for (int i = 0; i < libName.length(); i++) {
                    fuzzyLibName = fuzzyLibName + libName.charAt(i) + "%";
                }
                params.put("LibName", fuzzyLibName);
            }

            int roleId = userGroup.getRoleID();
            if (roleId == 1) {
                List<PersonLib> personLibs = personLibMapper.query(params);
                if (personLibs != null) {
                    for (PersonLib pLib : personLibs) {
                        parseData(pLib, libArray);
                    }
                }
            } else {
                params.put("UserGroupID", userGroup.getUserGroupID());
                List<UserLibRelation> userLibs = userLibRelationMapper.queryUserLib(params);
                if (userLibs != null) {
                    for (UserLibRelation userLib : userLibs) {
                        parseData(userLib, libArray);
                    }
                }
            }
        }
        return libArray;
    }

    /**
     * @param jsonLib
     * @param jsonArray
     */
    private void parseData(Object jsonLib, JSONArray jsonArray) {
        String aa = JSON.toJSONString(jsonLib, new PascalNameFilter());
        JSONObject jsonObject = JSON.parseObject(aa);
        if (jsonLib == null) {
            return;
        }
        int libType = jsonObject.getInteger("PersonLibType");
        switch (libType) {
            case 0:
                jsonObject.put("PID", "-1");  //基础人员库
                break;
            case 1:
                jsonObject.put("PID", "-2");
                break;
        }
        jsonObject.put("IconSkin", "icon-personlib");
        jsonObject.put("LibInfo", String.format("%s%s%s%s", "库名：", jsonObject.getString("LibName"), "    库ID：", jsonObject.getString("LibID")));
        jsonArray.add(jsonObject);
    }

    private JSONArray getBasicTreeArray() {
        JSONArray libArr = new JSONArray();
        /*JSONObject temp1 = new JSONObject();
        temp1.put("CreatorID", "11000000000");
        temp1.put("Description", "");
        temp1.put("EventID", "");
        temp1.put("IconSkin", "icon-persons");
        temp1.put("LibID", "-1");
        temp1.put("LibName", "基础人员库");
        temp1.put("PID", "-3");
        temp1.put("PersonLibType", "0");
        temp1.put("LibAlarmLevel", "");
        temp1.put("RegionID", "");
        temp1.put("LibInfo", "库名：基础人员库    库ID：-1");
        libArr.add(temp1);

        JSONObject temp2 = new JSONObject();
        temp2.put("CreatorID", "11000000000");
        temp2.put("Description", "");
        temp2.put("EventID", "");
        temp2.put("IconSkin", "icon-persons");
        temp2.put("LibID", "-2");
        temp2.put("LibName", "布控人员库");
        temp2.put("PID", "-3");
        temp2.put("PersonLibType", "1");
        temp2.put("LibAlarmLevel", "");
        temp2.put("RegionID", "");
        temp2.put("LibInfo", "库名：布控人员库    库ID：-2");
        libArr.add(temp2);

        JSONObject temp3 = new JSONObject();
        temp3.put("CreatorID", "11000000000");
        temp3.put("Description", "");
        temp3.put("EventID", "");
        temp3.put("IconSkin", "icon-persons");
        temp3.put("LibID", "-3");
        temp3.put("LibName", "人员库");
        temp3.put("PID", "0");
        temp3.put("PersonLibType", "1");
        temp3.put("LibAlarmLevel", "");
        temp3.put("RegionID", "");
        temp3.put("LibInfo", "库名：人员库    库ID：-3");
        libArr.add(temp3);*/
        return libArr;
    }

    public PersonLib queryLibByLibId(Integer libId) {
        return personLibMapper.selectByPrimaryKey(libId);
    }

    public List<PersonLib> queryLibByLibType(String personLibType) {
        PersonLib record = new PersonLib();
        if (!StringUtils.isEmpty(personLibType)) {
            record.setPersonLibType(personLibType);
        }

        return personLibMapper.queryLibByLibType(record);
    }

    public HashMap<String, Object> deleteById(Integer libId) {
        return personLibMapper.deleteByLibId(libId);
    }

    public List<UserLibRelation> queryUserLibByGoupId(int userGroupId,String personLibType) {
        UserLibRelation record = new UserLibRelation();
        record.setUserGroupID(userGroupId);
        if (!StringUtils.isEmpty(personLibType)) {
            record.setPersonLibType(personLibType);
        }
        return userLibRelationMapper.queryUserLibByGroupId(record);
    }

    /**
     * @param personLib
     */
    public HashMap<String, Object> configLib(PersonLib personLib) {
        return personLibMapper.save(personLib);
    }
}
