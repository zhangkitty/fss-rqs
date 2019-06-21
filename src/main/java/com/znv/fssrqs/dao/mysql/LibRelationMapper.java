package com.znv.fssrqs.dao.mysql;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.Map;

/**
 * Created by dongzelong on  2019/6/12 14:58.
 *
 * @author dongzelong
 * @version 1.0
 * @Description TODO
 */
@Mapper
@Repository
public interface LibRelationMapper {
    @Insert("INSERT INTO fss_hk_lib_relation(fss_lib_id,hk_lib_id) VALUES(#{fssLibId},#{hkLibId})")
    int insert(Map<String, Object> map);

    /**
     * 人脸静态库类型1（重点库），0（基础库） 海康脸谱静态库类型2（黑名单），3（静态库）
     *
     * @param map
     * @return
     */
    @Select("SELECT a.hk_lib_id 'hkLibId',b.lib_name 'listLibName',if(b.personlib_type=1,2,3) 'typeId',b.description 'describe' FROM fss_hk_lib_relation a join t_scim_personlib b on a.fss_lib_id = b.lib_id WHERE a.fss_lib_id = #{fssLibId}")
    Map<String, Object> getOne(Map<String, Object> map);

    @Select("SELECT * FROM fss_hk_lib_relation WHERE fss_lib_id=#{libId}")
    Map<String, Object> selectOne(Integer libId);

    @Delete("DELETE FROM fss_hk_lib_relation WHERE fss_lib_id = #{fssLibId}")
    int delete(Map<String, Object> map);
}
