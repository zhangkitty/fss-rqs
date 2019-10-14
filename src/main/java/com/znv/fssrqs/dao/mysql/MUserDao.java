package com.znv.fssrqs.dao.mysql;

import com.znv.fssrqs.entity.mysql.MUserEntity;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
@Qualifier("mysqlSqlSessionTemplate")
public interface MUserDao {
    List<MUserEntity> getUser(Map<String, Object> params);

    List<Map<String, Object>> upCfgUserLogin(Map<String, Object> params);

    void upCfgUserLogout(Map<String, Object> params);

    int upCfgSaveSystemLog(Map<String, Object> params);

    MUserEntity selectById(@Param("userId") String userId);

    Integer updateUserInfo(@Param("userId") String userId,@Param("password") String password);

    List<MUserEntity> selectUsersByUserGroupIds(@Param("userGroupIds") List<Integer> userGroupIds);
}
