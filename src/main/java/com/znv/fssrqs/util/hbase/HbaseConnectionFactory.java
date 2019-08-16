package com.znv.fssrqs.util.hbase;

import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.client.Connection;

import java.util.Map;
import java.util.Properties;

/**
 * Created by dongzelong on  2019/8/15 19:01.
 *
 * @author dongzelong
 * @version 1.0
 * @Description TODO
 */
public class HbaseConnectionFactory implements ConnectionFactory<Connection> {
    private static final long serialVersionUID = 4024923894283696465L;
    private final Configuration configuration;

    public HbaseConnectionFactory(final Configuration configuration) {
        this.configuration = configuration;
    }

    /**
     * @param host    zookeeper地址
     * @param port    zookeeper端口
     * @param master  hbase主机
     * @param rootdir hdfs数据目录
     */
    public HbaseConnectionFactory(final String host, final String port, final String master, final String rootdir) {
        this.configuration = new Configuration();
        if (host == null) {
            throw new ConnectionException("[" + HbaseConfig.ZOOKEEPER_QUORUM_PROPERTY + "] is required !");
        }

        this.configuration.set(HbaseConfig.ZOOKEEPER_QUORUM_PROPERTY, host);
        if (port == null) {
            throw new ConnectionException("[" + HbaseConfig.ZOOKEEPER_CLIENTPORT_PROPERTY + "] is required !");
        }

        this.configuration.set(HbaseConfig.ZOOKEEPER_CLIENTPORT_PROPERTY, port);
        if (master != null) {
            this.configuration.set(HbaseConfig.MASTER_PROPERTY, master);
        }

        if (rootdir != null) {
            this.configuration.set(HbaseConfig.ROOTDIR_PROPERTY, rootdir);
        }
    }

    /**
     * @param properties 参数配置
     * @since 1.2.1
     */
    public HbaseConnectionFactory(final Properties properties) {
        this.configuration = new Configuration();
        for (Map.Entry<Object, Object> entry : properties.entrySet()) {
            this.configuration.set((String) entry.getKey(), (String) entry.getValue());
        }
    }


    @Override
    public PooledObject<Connection> makeObject() throws Exception {
        Connection connection = this.createConnection();
        return new DefaultPooledObject<Connection>(connection);
    }

    @Override
    public void destroyObject(PooledObject<Connection> p) throws Exception {
        Connection connection = p.getObject();
        if (connection != null) {
            connection.close();
        }
    }

    @Override
    public boolean validateObject(PooledObject<Connection> p) {
        Connection connection = p.getObject();
        if (connection != null) {
            return ((!connection.isAborted()) && (!connection.isClosed()));
        }
        return false;
    }

    @Override
    public void activateObject(PooledObject<Connection> p) throws Exception {

    }

    @Override
    public void passivateObject(PooledObject<Connection> p) throws Exception {
    }

    @Override
    public Connection createConnection() throws Exception {
        Connection connection = org.apache.hadoop.hbase.client.ConnectionFactory.createConnection(configuration);
        return connection;
    }
}
