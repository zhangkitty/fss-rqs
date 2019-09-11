package com.znv.fssrqs.dao.mysql;

import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

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
    @Select({"SELECT device_id DeviceID,device_name DeviceName FROM t_reid_task"})
    Map<String, Map<String, Object>> getAllDevices();
}
