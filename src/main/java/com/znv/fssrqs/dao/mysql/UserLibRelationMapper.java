package com.znv.fssrqs.dao.mysql;

import com.znv.fssrqs.entity.mysql.UserLibRelation;

import java.util.List;

/**
 * Created by dongzelong on  2019/6/4 14:46.
 *
 * @author dongzelong
 * @version 1.0
 * @Description TODO
 */
public interface UserLibRelationMapper {
    int insert(UserLibRelation record);

    int insertSelective(UserLibRelation record);

    List<UserLibRelation> queryUserLibByGroupId(UserLibRelation record);
}
