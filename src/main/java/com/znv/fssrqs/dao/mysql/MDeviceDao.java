package com.znv.fssrqs.dao.mysql;

import com.znv.fssrqs.entity.mysql.AnalysisUnitEntity;
import com.znv.fssrqs.entity.mysql.MBusEntity;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
@Qualifier("mysqlSqlSessionTemplate")
public interface MDeviceDao {
    List<MBusEntity> getMBus();

    List<AnalysisUnitEntity> getStaticAnalysisUnit();

    List<AnalysisUnitEntity> getDynamicAnalysisUnit();

    Map getDeviceCount();
}
