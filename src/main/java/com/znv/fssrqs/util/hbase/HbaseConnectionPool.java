package com.znv.fssrqs.util.hbase;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.client.Connection;

import java.util.Properties;

/**
 * Created by dongzelong on  2019/8/15 19:05.
 *
 * @author dongzelong
 * @version 1.0
 * @Description TODO
 */
public class HbaseConnectionPool extends PoolBase<Connection> implements ConnectionPool<Connection> {
    private static final long serialVersionUID = -9126420905798370243L;
    private Configuration configuration;

    public HbaseConnectionPool() {
        this(HbaseConfig.DEFAULT_HOST, HbaseConfig.DEFAULT_PORT);
    }


    /**
     * @param host 地址
     * @param port 端口
     */
    public HbaseConnectionPool(final String host, final String port) {
        this(new PoolConfig(), host, port, HbaseConfig.DEFAULT_MASTER, HbaseConfig.DEFAULT_ROOTDIR);
    }

    /**
     * @param host    地址
     * @param port    端口
     * @param master  hbase主机
     * @param rootdir hdfs目录
     */
    public HbaseConnectionPool(final String host, final String port, final String master, final String rootdir) {
        this(new PoolConfig(), host, port, master, rootdir);
    }

    /**
     * @param hadoopConfiguration hbase配置
     */
    public HbaseConnectionPool(final Configuration hadoopConfiguration) {
        this(new PoolConfig(), hadoopConfiguration);
    }

    /**
     * @param poolConfig 池配置
     * @param host       地址
     * @param port       端口
     */
    public HbaseConnectionPool(final PoolConfig poolConfig, final String host, final String port) {
        this(poolConfig, host, port, HbaseConfig.DEFAULT_MASTER, HbaseConfig.DEFAULT_ROOTDIR);
    }

    /**
     * @param poolConfig          池配置
     * @param hadoopConfiguration hbase配置
     */
    public HbaseConnectionPool(final PoolConfig poolConfig, final Configuration hadoopConfiguration) {
        // 默认设置开启8个 maxTotal = 8
        super(poolConfig, new HbaseConnectionFactory(hadoopConfiguration));
    }

    /**
     * @param poolConfig 池配置
     * @param host       地址
     * @param port       端口
     * @param master     hbase主机
     * @param rootdir    hdfs目录
     */
    public HbaseConnectionPool(final PoolConfig poolConfig, final String host, final String port, final String master, final String rootdir) {
        super(poolConfig, new HbaseConnectionFactory(host, port, master, rootdir));
    }

    /**
     * @param poolConfig 池配置
     * @param properties 参数配置
     * @since 1.2.1
     */
    public HbaseConnectionPool(final PoolConfig poolConfig, final Properties properties) {
        super(poolConfig, new HbaseConnectionFactory(properties));
    }

    @Override
    public Connection getConnection() {
        return super.getResource();
    }

    @Override
    public void returnConnection(Connection conn) {
        super.returnResource(conn);
    }

    @Override
    public void invalidateConnection(Connection conn) {
        super.invalidateResource(conn);
    }
}
