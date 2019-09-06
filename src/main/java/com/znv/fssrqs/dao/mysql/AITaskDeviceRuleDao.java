package com.znv.fssrqs.dao.mysql;

import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.Map;

/**
 * Created by dongzelong on  2019/9/6 16:11.
 *
 * @author dongzelong
 * @version 1.0
 * @Description TODO
 */
@Mapper
public interface AITaskDeviceRuleDao {
    @MapKey("CameraID")
    @Select("SELECT t_scim_facetask.camera_id CameraID,t_cfg_device.device_name CameraName FROM t_scim_facetask LEFT JOIN t_cfg_device ON t_scim_facetask.`camera_id`=t_cfg_device.`device_id`;")
    Map<String, Map<String, Object>> selectAllTaskCameras();
}
