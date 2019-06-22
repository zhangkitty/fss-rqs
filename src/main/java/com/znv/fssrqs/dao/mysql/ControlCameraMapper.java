package com.znv.fssrqs.dao.mysql;


import com.znv.fssrqs.entity.mysql.TCfgDevice;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface ControlCameraMapper {
    List<Map<String, Object>> up_fss_deploy_camera(@Param("id") String id,@Param("title") String title,@Param("cameraIds") String cameraIds,@Param("libId") String libId,
                                                   @Param("startTime") String startTime,@Param("endTime") String endTime,@Param("libCountLimit") int libCountLimit,@Param("cameraCountLimit") int cameraCountLimit);

    TCfgDevice listDeviceById(@Param("deviceId") String deviceId);

    Integer up_fss_undeploy_camera(List<String> list);

    List<String> listDeviceIdInArray(List<String> list);
}
