package com.znv.fssrqs.dao.mysql;

import com.znv.fssrqs.entity.mysql.UserGroup;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by dongzelong on  2019/6/3 10:16.
 *
 * @author dongzelong
 * @version 1.0
 * @Description TODO
 */

@Repository
public interface UserGroupMapper {
    int deleteByPrimaryKey(Integer userGroupId);

    int insert(UserGroup record);

    int insertSelective(UserGroup record);

    UserGroup selectByPrimaryKey(Integer userGroupId);

    int updateByPrimaryKeySelective(UserGroup record);

    int updateByPrimaryKey(UserGroup record);

    UserGroup queryUserGroupByUserId(UserGroup record);

    List<UserGroup> queryUserGroupByUpGroupId(UserGroup record);
}
