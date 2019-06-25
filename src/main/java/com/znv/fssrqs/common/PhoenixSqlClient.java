package com.znv.fssrqs.common;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by dongzelong on  2019/6/24 9:48.
 *
 * @author dongzelong
 * @version 1.0
 * @Description TODO
 */
@Slf4j
public class PhoenixSqlClient {
    /**
     * 执行无需返回结果的sql语句
     *
     * @param sql         sql语句
     * @param phoenixConn phoenix连接
     * @throws Exception 异常
     */
    public void executeSql(String sql, Connection phoenixConn) throws Exception {
        phoenixConn.setAutoCommit(false);
        Statement stat = phoenixConn.createStatement();
        try {
            stat.execute(sql);
            phoenixConn.commit();
        } catch (Exception e) {
            phoenixConn.rollback();
            throw e;
        } finally {
            try {
                stat.close();
            } catch (Exception e) {
                throw new RuntimeException("", e);
            }
        }

    }

    /**
     * 添加数据
     *
     * @param data        数据信息
     * @param tableName   表名
     * @param phoenixConn phoenix连接
     * @return JSONObject
     */
    public static void insert(JSONObject data, String tableName, Connection phoenixConn) {
        JSONObject result = new JSONObject();
        StringBuilder insertSql = new StringBuilder();
        if (data.entrySet().size() == 0) {
            return;
        }

        StringBuffer prefixFieldSql = new StringBuffer();
        StringBuffer suffixFieldSql = new StringBuffer();
        insertSql.append("UPSERT INTO ").append(tableName).append("(");
        boolean isFirst = true;
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            String fieldKey = entry.getKey();
            if (isFirst) {
                prefixFieldSql.append(fieldKey);
                suffixFieldSql.append("?");
                isFirst = false;
            } else {
                prefixFieldSql.append("," + fieldKey);
                suffixFieldSql.append(",?");
            }
        }
        insertSql.append(prefixFieldSql);
        insertSql.append(") VALUES(");
        insertSql.append(suffixFieldSql);
        insertSql.append(")");
        PreparedStatement statement = null;
        try {
            phoenixConn.setAutoCommit(false);
            statement = phoenixConn.prepareStatement(insertSql.toString());
            int i = 1;
            for (Map.Entry<String, Object> entry : data.entrySet()) {
                statement.setObject(i, entry.getValue());
                i++;
            }
            statement.executeUpdate();
            phoenixConn.commit();
        } catch (SQLException e) {
            try {
                phoenixConn.rollback();
            } catch (SQLException e1) {
                log.error("", e1);
            }
            throw new RuntimeException("访问hbase数据库失败:", e);
        } finally {
            try {
                if (statement != null) {
                    statement.close();
                }
            } catch (SQLException e) {
                log.error("", e);
            }
        }
    }

    /**
     * 添加批量数据
     *
     * @param dataList    数据信息
     * @param tableName   表名
     * @param phoenixConn phoenix连接
     */
    public static JSONObject batchInsert(JSONArray dataList, String tableName, Connection phoenixConn) {
        JSONObject result = new JSONObject();
        // 增加批量输入结果集,默认全部为error
        JSONArray insertResult = new JSONArray(dataList.size());
        for (int idx = 0; idx < dataList.size(); idx++) {
            JSONObject obj = new JSONObject(dataList.size());
            obj.put("id", idx);
            obj.put("errorCode", "error");
            insertResult.add(idx, obj);
        }
        PreparedStatement stat = null;

        try {
            phoenixConn.setAutoCommit(false);
            // 预编译SQL语句，只编译一次
            JSONObject firstData = dataList.getJSONObject(0);
            StringBuilder insertSql = new StringBuilder();
            if (firstData.entrySet().size() == 0) {
                return result;
            }
            StringBuffer prefixFieldSql = new StringBuffer();
            StringBuffer suffixFieldSql = new StringBuffer();
            insertSql.append("UPSERT INTO ").append(tableName).append("(");
            boolean isFirst = true;
            for (Map.Entry<String, Object> entry : firstData.entrySet()) {
                String fieldKey = entry.getKey();
                if (!isFirst) {
                    prefixFieldSql.append("," + fieldKey);
                    suffixFieldSql.append(",?");
                } else {
                    prefixFieldSql.append(fieldKey);
                    suffixFieldSql.append("?");
                    isFirst = false;
                }
            }
            insertSql.append(prefixFieldSql);
            insertSql.append(") VALUES(");
            insertSql.append(suffixFieldSql);
            insertSql.append(")");
            stat = phoenixConn.prepareStatement(insertSql.toString());

            // 插入所有数据
            int len = dataList.size();
            int count = 0;
            int batchSize = 50;
            int lastKey = 0;
            for (int idx = 0; idx < len; idx++) {
                count++;
                JSONObject data = dataList.getJSONObject(idx);
                int i = 1;
                Iterator<Map.Entry<String, Object>> iterator = data.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry<String, Object> entry = iterator.next();
                    stat.setObject(i, entry.getValue());
                    i++;
                }
                stat.addBatch();

                if (count % batchSize == 0) { // 1000条数据提交一次
                    stat.executeBatch();
                    phoenixConn.commit();
                    lastKey = count;
                    int page = count / batchSize;
                    int startKey = batchSize * (page - 1);
                    int endKey = count;
                    for (int j = startKey; j < endKey; j++) {
                        insertResult.remove(j);
                        JSONObject obj = new JSONObject();
                        obj.put("id", j);
                        obj.put("errorCode", "success");
                        insertResult.add(j, obj);
                    }
                }
            }
            stat.executeBatch();
            phoenixConn.commit();
            if (lastKey != len) {
                for (int j = lastKey; j < len; j++) {
                    insertResult.remove(j);
                    JSONObject obj = new JSONObject();
                    obj.put("id", j);
                    obj.put("errorCode", "success");
                    insertResult.add(j, obj);
                }
            }
            result.put("data", insertResult);
            phoenixConn.setAutoCommit(true);
        } catch (SQLException e) {
            try {
                phoenixConn.rollback();
            } catch (SQLException e1) {
                log.error("", e1);
            }
            throw new RuntimeException("访问hbase数据库失败:", e);
        } finally {
            // 释放资源
            try {
                if (stat != null) {
                    stat.close();
                }
            } catch (SQLException e) {
                throw new RuntimeException("");
            }
        }
        return result;
    }

    /**
     * 查询数据
     *
     * @param sql 人的唯一标识
     * @return JSONObject
     * @throws Exception 异常,所有异常全部抛出，但是如果释放资源时出现异常则打印异常
     */
    public List<JSONObject> query(String sql, Connection phoenixConn) throws Exception {
        List<JSONObject> objList = new ArrayList<JSONObject>();
        PreparedStatement stat = null;
        ResultSet rs = null;
        try {
            stat = phoenixConn.prepareStatement(sql);
            rs = stat.executeQuery();
            while (rs.next()) {
                JSONObject record = new JSONObject();
                ResultSetMetaData rsMetaData = rs.getMetaData();
                int columnCount = rsMetaData.getColumnCount();
                for (int column = 0; column < columnCount; column++) {
                    String field = rsMetaData.getColumnLabel(column + 1);
                    record.put(field.toLowerCase(), rs.getObject(field));
                }
                objList.add(record);
            }
        } catch (SQLException e) {
            throw e;
        } finally {
            try {
                if (stat != null) {
                    stat.close();
                }

                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException e) {
                log.error(" query exception when close PreparedStatement !", e);
            }
        }

        return objList;
    }
}
