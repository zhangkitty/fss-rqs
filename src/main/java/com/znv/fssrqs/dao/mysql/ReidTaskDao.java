package com.znv.fssrqs.dao.mysql;


import com.znv.fssrqs.controller.reid.params.QueryReidTaskParma;
import com.znv.fssrqs.entity.mysql.ReidTaskEntity;
import org.apache.ibatis.annotations.Options;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReidTaskDao {

    @Options(useGeneratedKeys = true)
    int sava(ReidTaskEntity reidTaskEntity);

    List<ReidTaskEntity> getAll(QueryReidTaskParma queryReidTaskParma);

    Integer update(ReidTaskEntity reidTaskEntity);

    Integer delete(List<Integer> list);
}
