package com.znv.fssrqs.dao.mysql;


import com.znv.fssrqs.entity.mysql.ReidTaskEntity;
import org.apache.ibatis.annotations.Options;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface ReidTaskDao {

    @Options(useGeneratedKeys = true)
    int sava(ReidTaskEntity reidTaskEntity);

    List<ReidTaskEntity> getAll();

    Integer update(ReidTaskEntity reidTaskEntity);

    Integer delete(List<Integer> list);
}
