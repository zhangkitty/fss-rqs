package com.znv.fssrqs.dao.mysql;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public interface HkPersonRelationMap {
    @Insert("INSERT INTO fss_hk_person_relation(fss_person_id,hk_person_id) VALUES(#{fssPersonId},#{hkPersonId})")
    int insert(Map<String, Object> map);

    @Select("SELECT hk_person_id FROM fss_hk_person_relation WHERE fss_person_id=#{fssPersonId}")
    Map<String, Object> getByFssPersonId(String fssPersonId);

    @Select("SELECT fss_person_id FROM fss_hk_person_relation WHERE hk_person_id=#{hkPersonId}")
    Map<String, Object> getByHkPersonId(String hkPersonId);

    @Delete("DELETE FROM fss_hk_person_relation WHERE fss_person_id = #{fssPersonId}")
    int delete(Map<String, Object> map);
}
