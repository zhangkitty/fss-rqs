package com.znv.fssrqs.dao.mysql;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * Created by dongzelong on  2019/8/2 12:01.
 *
 * @author dongzelong
 * @version 1.0
 * @Description TODO
 */
@Mapper
public interface DeviceDeptRelationDao {
    @Select("select device_id DeviceID,dept_id DeptID from t_scim_dept_device")
    List<Map<String, Object>> selectAll();
}
