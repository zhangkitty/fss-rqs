package com.znv.fssrqs.dao.mysql;

import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.Map;

/**
 * Created by dongzelong on  2019/8/27 9:53.
 *
 * @author dongzelong
 * @version 1.0
 * @Description TODO
 */
@Mapper
public interface LibDao {
    @MapKey("LibID")
    @Select("SELECT lib_id LibID,lib_name `LibName` FROM t_scim_personlib")
    Map<String, Map<String, Object>> selectAllMap();
}
