package com.znv.fssrqs.dao.mysql;

import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.Map;

/**
 * Created by dongzelong on  2019/8/14 18:34.
 *
 * @author dongzelong
 * @version 1.0
 * @Description TODO
 */
@Mapper
public interface EventDao {
    @MapKey("ID")
    @Select("SELECT fevent_id ID,fevent_name `Name`,fevent_desc `Desc`,up_fevent_id ParentID FROM t_scim_faceevent")
    Map<String, Map<String, Object>> selectAllMap();
}
