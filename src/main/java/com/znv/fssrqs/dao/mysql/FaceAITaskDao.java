package com.znv.fssrqs.dao.mysql;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.mapping.StatementType;

import java.util.List;
import java.util.Map;

/**
 * Created by dongzelong on  2019/8/5 18:30.
 *
 * @author dongzelong
 * @version 1.0
 * @Description TODO
 */
@Mapper
public interface FaceAITaskDao {
    @Select({"CALL up_scim_facetask_query(NULL,NULL,NULL,NULL,NULL);"})
    @Options(statementType = StatementType.CALLABLE)
    List<Map<String, Object>> selectAll();
}
