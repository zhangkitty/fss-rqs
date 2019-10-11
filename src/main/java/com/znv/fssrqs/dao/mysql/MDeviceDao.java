package com.znv.fssrqs.dao.mysql;

import com.znv.fssrqs.entity.mysql.AnalysisUnitEntity;
import com.znv.fssrqs.entity.mysql.MBusEntity;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import scala.Int;

import java.util.List;
import java.util.Map;

@Repository
@Qualifier("mysqlSqlSessionTemplate")
public interface MDeviceDao {
    List<MBusEntity> getMBus();

    List<AnalysisUnitEntity> getStaticAnalysisUnit();

    List<AnalysisUnitEntity> getDynamicAnalysisUnit();

    Map getDeviceCount();

    List<String> getDeviceBatch(@Param("deviceIds") List<String> deviceIds);

    @Select({"SELECT COUNT(1) FROM (SELECT * FROM t_scim_facetask t group by t.camera_id) as result"})
    Integer getCameralCount();
}
