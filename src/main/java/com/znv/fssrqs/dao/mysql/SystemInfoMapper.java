package com.znv.fssrqs.dao.mysql;

import com.znv.fssrqs.entity.mysql.SystemInfo;
import org.apache.ibatis.annotations.Param;

public interface SystemInfoMapper {
    void updateByExampleSelective(@Param("record") SystemInfo record);
    SystemInfo selectOne();
}