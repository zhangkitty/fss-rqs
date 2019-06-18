package com.znv.fssrqs.service;

import com.znv.fssrqs.dao.mysql.UserGroupMapper;
import com.znv.fssrqs.entity.mysql.UserGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by dongzelong on  2019/6/3 10:18.
 *
 * @author dongzelong
 * @version 1.0
 * @Description TODO
 */
@Service
public class UserGroupService {

    @Autowired
    private UserGroupMapper userGroupMapper;

    public UserGroup queryUserGroupByUserId(String userId) {
        UserGroup record = new UserGroup();
        record.setUserID(userId);
        return userGroupMapper.queryUserGroupByUserId(record);
    }

    public List<UserGroup> queryUserGroupByUpGroupId(int upUserGroupId) {
        UserGroup record = new UserGroup();
        record.setUpUserGroupID(upUserGroupId);
        return userGroupMapper.queryUserGroupByUpGroupId(record);
    }
}
