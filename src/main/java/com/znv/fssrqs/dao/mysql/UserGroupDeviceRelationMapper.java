package com.znv.fssrqs.dao.mysql;

import com.znv.fssrqs.entity.mysql.MapDeviceLocation;
import com.znv.fssrqs.entity.mysql.UserGroupDeviceRelation;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by dongzelong on  2019/6/3 14:00.
 *
 * @author dongzelong
 * @version 1.0
 * @Description TODO
 */
@Repository
public interface UserGroupDeviceRelationMapper {
    int insert(UserGroupDeviceRelation record);

    int insertSelective(UserGroupDeviceRelation record);

    List<UserGroupDeviceRelation> queryGroupDeviceByCon(int userGroupId, int deviceKind);

    List<UserGroupDeviceRelation> queryUserDeviceByGroupId(UserGroupDeviceRelation record);

    List<UserGroupDeviceRelation> queryAdminUserDevice();

    List<MapDeviceLocation> queryAdminMapDevice();

    List<MapDeviceLocation> queryNormalMapDevice(int userGroupId);
}
