package com.znv.fssrqs.controller;

import com.alibaba.fastjson.JSONObject;
import com.znv.fssrqs.dao.mysql.MUserDao;
import com.znv.fssrqs.entity.mysql.MUserEntity;
import com.znv.fssrqs.entity.mysql.UserGroup;
import com.znv.fssrqs.enums.ErrorCodeEnum;
import com.znv.fssrqs.exception.BusinessException;
import com.znv.fssrqs.service.UserGroupService;
import com.znv.fssrqs.util.FastJsonUtils;
import com.znv.fssrqs.util.LocalUserUtil;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by dongzelong on  2019/10/11 19:49.
 *
 * @author dongzelong
 * @version 1.0
 * @Description TODO
 */
@RestController
public class UserGroupController {
    @Autowired
    private UserGroupService userGroupService;
    @Resource
    private MUserDao mUserDao;

    /**
     * 查询用户组以及用户
     *
     * @return
     */
    @GetMapping("/user/group/users")
    public JSONObject getUserGroupUsers() {
        JSONObject user = LocalUserUtil.getLocalUser();
        if (user == null || !user.containsKey("UserId")) {
            throw new BusinessException(ErrorCodeEnum.UNAUTHED_NOT_LOGIN);
        }
        String userId = user.getString("UserId");
        final List<UserGroup> userGroups = userGroupService.selectGroupByUserId(userId);
        UserGroup selfGroup = userGroups.get(0);
        Integer upId = selfGroup.getUpUserGroupID();
        UserGroup parentGroup = new UserGroup();
        if (upId == 0) {
            parentGroup.setUpUserGroupID(-1);
            parentGroup.setUserGroupName("超级管理员");
        } else {
            parentGroup = userGroupService.selectByPrimaryKey(upId);
        }
        userGroups.add(parentGroup);

        final List<Integer> userGroupIds = userGroups.parallelStream().map(object -> {
            if (Objects.isNull(object.getUserGroupID())) {
                return -1;
            } else {
                return object.getUserGroupID();
            }
        }).collect(Collectors.toList());
        final List<MUserEntity> mUserEntities = mUserDao.selectUsersByUserGroupIds(userGroupIds);
        return FastJsonUtils.JsonBuilder.ok().property("UserGroups",userGroups).property("Users",mUserEntities).json();
    }
}
