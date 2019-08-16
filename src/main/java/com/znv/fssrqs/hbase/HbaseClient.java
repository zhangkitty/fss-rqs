package com.znv.fssrqs.hbase;

import com.znv.fssrqs.config.HdfsConfigManager;
import com.znv.fssrqs.constant.CommonConstant;
import com.znv.fssrqs.util.hbase.HbaseConnectionPool;
import lombok.extern.slf4j.Slf4j;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.client.coprocessor.AggregationClient;
import org.apache.hadoop.hbase.client.coprocessor.DoubleColumnInterpreter;
import org.apache.hadoop.hbase.client.coprocessor.LongColumnInterpreter;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;

import static com.znv.fssrqs.constant.CommonConstant.HBase.*;
import static com.znv.fssrqs.constant.CommonConstant.Zookeeper.ZOOKEEPER_CLIENTPORT;
import static com.znv.fssrqs.constant.CommonConstant.Zookeeper.ZOOKEEPER_QUORUM;

/**
 * Created by dongzelong on  2019/8/15 18:13.
 *
 * @author dongzelong
 * @version 1.0
 * @Description TODO
 */
@Slf4j
@Component
public class HbaseClient {
    private static Connection connection;
    private static AggregationClient aggregationClient;
    private static HbaseConnectionPool hbaseConnectionPool = null;

    @PostConstruct()
    public void setUp() {
        Configuration configuration = HBaseConfiguration.create();
        //以下配置也可通过classpath目录下的hbase-site.xml和hbase-core.xml文件设置
        //优先检查hbase-site.xml，如果没有,则检查hbase-core.xml文件
        //与hbase/conf/hbase-site.xml中hbase.zookeeper.quorum配置的值相同
        configuration.set("hbase.zookeeper.quorum", HdfsConfigManager.getString(ZOOKEEPER_QUORUM));
        //与hbase/conf/hbase-site.xml中hbase.zookeeper.property.clientPort配置相同
        configuration.set("hbase.zookeeper.property.clientPort", HdfsConfigManager.getString(ZOOKEEPER_CLIENTPORT));
        //与hbase/conf/hbase-site.xml中zookeeper.znode.parent配置的值相同
        configuration.set("zookeeper.znode.parent", HdfsConfigManager.getString(HBASE_ZNODE_PARENT));
        configuration.set("hbase.client.retries.number", HdfsConfigManager.getString(HBASE_CLIENT_RETRIES_NUMBER));
        //用于统计时可适当提高超时时间
        configuration.set("hbase.rpc.timeout", HdfsConfigManager.getString(HBASE_RPC_TIMEOUT));
        configuration.set("hbase.client.operation.timeout", HdfsConfigManager.getString(HBASE_CLIENT_OPERATION_TIMEOUT));
        configuration.set("hbase.client.scanner.timeout.period", HdfsConfigManager.getString(HBASE_CLIENT_SCANNER_TIMEOUT));
        try {
            connection = ConnectionFactory.createConnection(configuration);
        } catch (IOException e) {
            log.error("create hbase connection failed:", e);
        }

        aggregationClient = new AggregationClient(configuration);
        hbaseConnectionPool = new HbaseConnectionPool(configuration);
        //添加协处理器判断
        this.checkOrAddCoprocessors();
    }

    public Connection getConnection() {
        return connection;
    }

    public static AggregationClient getAggregationClient() {
        return aggregationClient;
    }

    public static HbaseConnectionPool getHbaseConnectionPool() {
        return hbaseConnectionPool;
    }

    private static void checkOrAddCoprocessors() {
        String historyTablename = HdfsConfigManager.getTableName(CommonConstant.PhoenixProperties.HISTORY_DATA_TABLE_NAME);
        //添加协处理器实现
    }

    /**
     * @param coprocessorClass coprocessorName coprocessor class, sunch as AggregateImplementation.class
     * @param tablename
     * @throws IOException
     */
    private static void addCoprocessor(Class coprocessorClass, String tablename) throws IOException {
        String coprocessorClassName = coprocessorClass.getName();
        TableName tableName = TableName.valueOf(tablename);
        HBaseAdmin admin = new HBaseAdmin(connection);
        HTableDescriptor htd = admin.getTableDescriptor(tableName);
        // step1. 判断是否有加协处理器, 未加协处理器，则添加
        if (!htd.hasCoprocessor(coprocessorClassName)) {
            try {
                if (admin.isTableEnabled(tableName)) {
                    admin.disableTable(tableName);
                }
                htd.addCoprocessor(coprocessorClassName);
                admin.modifyTable(tableName, htd);
            } catch (IOException e) {
                log.error("", e);
            } finally {
                admin.enableTable(tableName);
                admin.close();
            }
        }
    }

    public static Connection getConnectionFromPool() {
        return hbaseConnectionPool.getConnection();
    }

    public static void returnPoolConnection(Connection conn) {
        hbaseConnectionPool.returnConnection(conn);
    }

    public static HTable getWriteHTable(String tableName) throws IOException {
        HTable table = (HTable) connection.getTable(TableName.valueOf(tableName));
        table.setAutoFlushTo(true);
        return table;
    }

    public static synchronized HTable getTable(String tableName) throws IOException {
        HTable table = (HTable) connection.getTable(TableName.valueOf(tableName));
        return table;
    }

    public static long aggregationRowCount(String tableName, Scan scan) throws Throwable {
        long rowCount = aggregationClient.rowCount(TableName.valueOf(tableName), new LongColumnInterpreter(), scan);
        return rowCount;
    }

    public static long aggregationSum(String tableName, Scan scan) throws Throwable {
        long rowSum = 0L;
        try {
            rowSum = aggregationClient.sum(TableName.valueOf(tableName), new LongColumnInterpreter(), scan);
        } catch (NullPointerException e) {
            rowSum = 0L;
        }
        return rowSum;
    }

    public static double aggregationEnergySum(String tableName, Scan scan) throws Throwable {
        double rowSum = 0.0;
        try {
            rowSum = aggregationClient.sum(TableName.valueOf(tableName), new DoubleColumnInterpreter(), scan);
        } catch (NullPointerException e) {
            rowSum = 0.0;
        }
        return rowSum;

    }

    public void closeConnection() throws IOException {
        if (connection != null) {
            connection.close();
        }

        if (aggregationClient != null) {
            aggregationClient.close();
        }

        if (hbaseConnectionPool != null) {
            hbaseConnectionPool.close();
        }
    }
}
