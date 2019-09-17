package com.znv.fssrqs.dao.mysql;


import com.znv.fssrqs.controller.reid.params.QueryReidTaskParma;
import com.znv.fssrqs.entity.mysql.ReidTaskEntity;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReidTaskDao {

    @Options(useGeneratedKeys = true)
    int sava(ReidTaskEntity reidTaskEntity);

    @Select({"select * from t_reid_task where task_id=#{task_id}"})
    ReidTaskEntity getOne(@Param("task_id") Integer taskId);

    List<ReidTaskEntity> getAll(QueryReidTaskParma queryReidTaskParma);

    Integer update(ReidTaskEntity reidTaskEntity);

    Integer delete(List<Integer> list);
}
