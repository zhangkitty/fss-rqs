package com.znv.fssrqs.dao.mysql;

import com.znv.fssrqs.entity.mysql.ReidUnitEntity;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
@Qualifier("mysqlSqlSessionTemplate")
public interface MReidDao {
    List<ReidUnitEntity> getReidUnit(Map<String, Object> params);

    Integer getReidUnitCount(Map<String, Object> params);

    Integer upReidAnalysisUnitSave(Map<String, Object> params);

    Integer upReidAnalysisUnitDelete(Map<String, Object> params);

    void updateReidLoginState(Map<String, Object> params);
}
