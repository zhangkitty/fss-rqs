package com.znv.fssrqs.util.hbase;

import java.io.Serializable;

/**
 * Created by dongzelong on  2019/8/15 18:50.
 *
 * @author dongzelong
 * @version 1.0
 * @Description 连接池管理
 */
public interface ConnectionPool<T> extends Serializable {
    /**
     * 获取连接对象
     */
    T getConnection();

    /**
     * 返回连接
     */
    void returnConnection(T conn);

    /**
     * 废弃连接
     */
    void invalidateConnection(T conn);
}
