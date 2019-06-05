package com.znv.fssrqs.dao.hbase;

import com.znv.fssrqs.entity.hbase.HUserEntity;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Qualifier("hbaseSqlSessionTemplate")
public interface HUserDao {

    List<HUserEntity> findAll();
}
