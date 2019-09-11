package com.znv.fssrqs.dao.mysql;


import com.znv.fssrqs.controller.reid.params.QueryReidTaskParma;
import com.znv.fssrqs.entity.mysql.ReidTaskEntity;
import org.apache.ibatis.annotations.*;
import java.util.List;
import java.util.Map;

@Mapper
public interface ReidTaskDao {

    @Options(useGeneratedKeys = true)
    int sava(ReidTaskEntity reidTaskEntity);

    List<ReidTaskEntity> getAll(QueryReidTaskParma queryReidTaskParma);

    Integer update(ReidTaskEntity reidTaskEntity);

    Integer delete(List<Integer> list);

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
    List<String> getDevicesByDeviceIds(@Param("deviceIds") List<String> deviceIds);
}
