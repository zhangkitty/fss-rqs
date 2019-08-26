package com.znv.fssrqs.util.hbase;

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.filter.Filter;
import org.apache.hadoop.hbase.filter.FilterList;
import org.apache.hadoop.hbase.filter.PageFilter;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;
import java.util.Iterator;

/**
 * Created by dongzelong on  2019/8/26 14:21.
 *
 * @author dongzelong
 * @version 1.0
 * @Description TODO
 */
public class HbaseUtil {
    private static Connection connection;

    public HbaseUtil(Connection connection) {
        this.connection = connection;
    }

    public static ResultScanner getPageData(int pageIndex, int pageSize) throws IOException {
        if (pageSize < 1 && pageSize > 15) {
            pageSize = 5;
        }

        String startRow = getCurrentPageStartRow(pageIndex, pageSize);
        return getPageData(startRow, pageSize);
    }


    public static String getCurrentPageStartRow(int pageIndex, int pageSize) throws IOException {
        if (pageIndex <= 1) {
            return null;
        } else {
            //从第二页开始的所有数据
            String startRow = null;
            for (int i = 1; i <= pageIndex; i++) {
                //第几页循环，就是获取第几页的数据
                final ResultScanner pageData = getPageData(startRow, pageSize);
                Iterator<Result> iterator = pageData.iterator();
                Result result = null;
                while (iterator.hasNext()) {
                    result = iterator.next();
                }
                //让最后一个rowKey往后移动一点位置，但是又不会等于下一页的startRow
                final String endRowStr = new String(result.getRow());
                final byte[] add = Bytes.add(endRowStr.getBytes(), new byte[]{0x00});
                String nextPageStartRowStr = Bytes.toString(add);
                startRow = nextPageStartRowStr;
            }
            return startRow;
        }
    }

    public static ResultScanner getPageData(String startRow, int pageSize) throws IOException {
        HTable table = (HTable) connection.getTable(TableName.valueOf("user_info"));
        Scan scan = new Scan();
        if (!StringUtils.isBlank(startRow)) {
            scan.setStartRow(startRow.getBytes());
        }

        Filter pageFilter = new PageFilter(pageSize);
        Filter filter = new FilterList(pageFilter);
        scan.setFilter(filter);
        final ResultScanner tableScanner = table.getScanner(scan);
        return tableScanner;
    }
}
