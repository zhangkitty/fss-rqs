package com.znv.fssrqs.dao.mysql;


import com.znv.fssrqs.entity.mysql.FaceTaskEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface FaceTaskDao {

    @Select("SELECT * FROM t_scim_facetask ")
    List<FaceTaskEntity> getAllFaceTask();
}
