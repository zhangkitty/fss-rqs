package com.znv.fssrqs.dao.hbase;

import com.znv.fssrqs.config.HdfsConfigManager;
import com.znv.fssrqs.constant.CommonConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by dongzelong on  2019/8/15 14:31.
 *
 * @author dongzelong
 * @version 1.0
 * @Description TODO
 */
@Repository
@Slf4j
public class PersonDao {
    @Autowired
    @Qualifier(value = "hbaseJdbcTemplate")
    private JdbcTemplate jdbcTemplate;

    /**
     * 更新人员标记为1表示删除,0-未删除
     *
     * @param libId
     * @param personId
     * @return
     */
    public boolean upsert(Integer libId, String personId, String isDel) {
        return jdbcTemplate.update(String.format("upsert into %s (LIB_ID,PERSON_ID,IS_DEL) values (%d,'%s','%s')", HdfsConfigManager.getTableName(CommonConstant.PhoenixProperties.PERSON_LIST_TABLE_NAME), libId, personId, isDel))>0;
    }

    /**
     * 普通库:0,重点库:1
     * alter+enter代码片段压制
     */
    @SuppressWarnings({"unchecked", "ConstantConditions"})
    public int select(Integer libId, String personId) {
        return jdbcTemplate.query("SELECT PERSONLIB_TYPE FROM "+HdfsConfigManager.getTableName(CommonConstant.PhoenixProperties.PERSON_LIST_TABLE_NAME)+" WHERE PERSON_ID = ? AND LIB_ID = ?", new PreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps) throws SQLException {
                ps.setString(1, personId);
                ps.setInt(2, libId);
            }
        }, new ResultSetExtractor<Integer>() {
            @Override
            public Integer extractData(ResultSet rs) throws SQLException, DataAccessException {
                while (rs.next()) {
                    return rs.getInt("PERSONLIB_TYPE");
                }
                return 0;
            }
        });
    }
}
