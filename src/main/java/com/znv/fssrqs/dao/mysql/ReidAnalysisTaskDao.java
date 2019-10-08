package com.znv.fssrqs.dao.mysql;

import org.apache.ibatis.annotations.*;
import org.apache.ibatis.mapping.StatementType;

import java.util.List;
import java.util.Map;

/**
 * Created by dongzelong on  2019/9/11 15:25.
 *
 * @author dongzelong
 * @version 1.0
 * @Description TODO
 */
@Mapper
public interface ReidAnalysisTaskDao {
    @MapKey("DeviceID")
    @Select({"SELECT t.`device_id` DeviceID,d.`device_name` DeviceName FROM t_reid_task t left join t_cfg_device d on t.`device_id`=d.`device_id`"})
    Map<String, Map<String, Object>> getAllDevices();

    @Select({
            "<script>",
            "SELECT device_id DeviceID FROM t_reid_task WHERE device_id IN ",
            "<foreach collection='deviceIds' item='deviceId' index='index' open='(' separator=',' close=')'>",
            "#{deviceId}",
            "</foreach>",
            "</script>"
    })
    List<String> getDevicesByDeviceIds(@Param("deviceIds") List<String> deviceId);

    @Select({"CALL up_scim_reidtask_query();"})
    @Options(statementType = StatementType.CALLABLE)
    List<Map<String, Object>> selectAll();
}
