package com.znv.fssrqs.service;

import com.znv.fssrqs.dao.mysql.UserGroupDeviceRelationMapper;
import com.znv.fssrqs.entity.mysql.MapDeviceLocation;
import com.znv.fssrqs.entity.mysql.UserGroupDeviceRelation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by dongzelong on  2019/6/3 13:59.
 *
 * @author dongzelong
 * @version 1.0
 * @Description TODO
 */
@Service
public class UserGroupDeviceService {
    @Autowired
    private UserGroupDeviceRelationMapper userGroupDeviceRelationMapper;

    public List<UserGroupDeviceRelation> queryGroupDeviceByCon(int userGroupId) {
        return userGroupDeviceRelationMapper.queryGroupDeviceByCon(userGroupId, 4);
    }

    public List<UserGroupDeviceRelation> queryUserDeviceByUserGroupId(int userGroupId) {
        UserGroupDeviceRelation record = new UserGroupDeviceRelation();
        record.setUserGroupID(userGroupId);
        return userGroupDeviceRelationMapper.queryUserDeviceByGroupId(record);
    }

    public List<UserGroupDeviceRelation> queryAdminUserDevice() {
        return userGroupDeviceRelationMapper.queryAdminUserDevice();
    }

    public List<MapDeviceLocation> queryAdminMapDevice() {
        return userGroupDeviceRelationMapper.queryAdminMapDevice();
    }

    public List<MapDeviceLocation> queryNormalMapDevice(int userGroupId) {
        return userGroupDeviceRelationMapper.queryNormalMapDevice(userGroupId);
    }
}
