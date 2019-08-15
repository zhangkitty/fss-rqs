package com.znv.fssrqs.dao.mysql;

import com.znv.fssrqs.entity.mysql.TCfgDevice;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
public interface DeviceTreeDao {

    @Select("SELECT * FROM t_cfg_device t WHERE t.device_kind = 4")
    List<TCfgDevice> getDeviceList();
}
