package com.znv.fssrqs.dao.mysql;

import com.znv.fssrqs.entity.mysql.InfoDeviceTypeEntity;
import com.znv.fssrqs.entity.mysql.InfoManufactureEntity;
import com.znv.fssrqs.entity.mysql.MUserEntity;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
@Qualifier("mysqlSqlSessionTemplate")
public interface MDictDao {
    List<InfoDeviceTypeEntity> getDeviceType(Map<String, Object> params);

    List<InfoManufactureEntity> getManufacture(Map<String, Object> params);
}
