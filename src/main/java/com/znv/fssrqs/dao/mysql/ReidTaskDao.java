package com.znv.fssrqs.dao.mysql;


import com.znv.fssrqs.controller.reid.params.QueryReidTaskParma;
import com.znv.fssrqs.entity.mysql.ReidTaskEntity;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

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
    @Select({"SELECT device_id DeviceID,device_name DeviceName FROM t_reid_task"})
    Map<String, Map<String, Object>> getAllDevices();
}
