package com.znv.fssrqs.util.hbase;

import org.apache.commons.pool2.PooledObjectFactory;

import java.io.Serializable;

/**
 * Created by dongzelong on  2019/8/15 18:48.
 *
 * @author dongzelong
 * @version 1.0
 * @Description 创建连接工厂管理
 */
public interface ConnectionFactory<T> extends PooledObjectFactory<T>, Serializable {
    T createConnection() throws Exception;
}
