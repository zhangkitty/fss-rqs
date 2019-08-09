package com.znv.fssrqs.dao.mysql;

import com.znv.fssrqs.controller.face.compare.n.n.QueryTaskParams;
import com.znv.fssrqs.entity.mysql.CompareTaskEntity;
import com.znv.fssrqs.param.face.compare.n.n.NToNCompareTaskParam;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Repository
public interface CompareTaskDao {

    List<CompareTaskEntity> findAllCompareTask();

    Integer update(CompareTaskEntity compareTaskEntity);

    Integer save(NToNCompareTaskParam nToNCompareTaskParam);

    List<CompareTaskEntity> query(QueryTaskParams queryTaskParams);

    Integer delete(String taskId);


}
