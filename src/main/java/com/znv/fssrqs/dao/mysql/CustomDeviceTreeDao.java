package com.znv.fssrqs.dao.mysql;

import com.znv.fssrqs.entity.mysql.CrumbCustomTreeEntity;
import com.znv.fssrqs.entity.mysql.CustomDeviceEntity;
import com.znv.fssrqs.entity.mysql.CustomUserGroupEntity;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Mapper
public interface CustomDeviceTreeDao {

    @Select("SELECT * FROM t_fss_customtree_node")
    List<CustomDeviceEntity> getAllCustomDeviceList();

    @Select("SELECT tree_id,GROUP_CONCAT(node_id) as node_ids FROM t_fss_customtree_node GROUP BY tree_id")
    List<Map<String,String >> getCustomDeviceByGroup();

    Integer updateBatch(List<CustomDeviceEntity> list);

    Integer insertBatch(List<CustomDeviceEntity> list);

    @Select("select * from t_fss_customtree")
    List<CustomUserGroupEntity> getAllCustomUserGroup();


    Integer saveCustomUserGroup(CustomUserGroupEntity customUserGroupEntity);

    Integer deleteCustomUserGroup(Integer treeId);

    @Select("SELECT * FROM t_fss_crumb_customtree t where t.is_del is null or t.is_del != 1")
    List<CrumbCustomTreeEntity> getAllCrumbList();

    Integer batchUpdateCrumbList(List<CrumbCustomTreeEntity> list);

    Integer batchInsertCrumbList(List<CrumbCustomTreeEntity> list);
}
