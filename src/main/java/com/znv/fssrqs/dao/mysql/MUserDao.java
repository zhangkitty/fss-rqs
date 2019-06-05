package com.znv.fssrqs.dao.mysql;

import com.znv.fssrqs.entity.mysql.MUserEntity;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Qualifier("mysqlSqlSessionTemplate")
public interface MUserDao {

    List<MUserEntity> findAll();
}
