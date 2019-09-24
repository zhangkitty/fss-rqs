package com.znv.fssrqs.dao.mysql;

import com.znv.fssrqs.entity.mysql.ReidAnalysisUnitEntity;
import lombok.Data;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.List;

@Repository
public interface ReidAnalysisUnitDao {


    @Select({"SELECT * from t_reid_analysis_unit"})
    List<ReidAnalysisUnitEntity> findAll();

    @Select({"SELECT * from t_reid_analysis_unit t WHERE t.device_id = #{deviceId}"})
    ReidAnalysisUnitEntity findOne(@Param("deviceId") String deviceId);
}
