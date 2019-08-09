package com.znv.fssrqs.utils;

import com.lowagie.text.*;
import com.lowagie.text.rtf.RtfWriter2;

import java.awt.*;
import java.io.FileOutputStream;
import java.sql.*;
import java.util.*;
import java.util.List;

/**
 * Created by dongzelong on 2017/8/28 19:14.
 *
 * @author dongzelong
 * @version 1.0
 */
public class DbDocUtil {
    //键类型字典
    private static Map<String,String> keyType = new HashMap<String,String>();
    //初始化jdbc
    static{
        try {
            keyType.put("PRI", "主键");
            keyType.put("UNI", "唯一键");
            keyType.put("YES","Y");
            keyType.put("NO","N");
            keyType.put("auto_increment","Y");
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    private static String url = "jdbc:mysql://10.45.152.100:3306/usmsc?serverTimezone=UTC&useUnicode=true&characterEncoding=utf-8";//链接url
    private static String username = "root"; //用户名
    private static String password = "zxm10"; //密码
    private static String schema = "usmsc"; //目标数据库名
    //查询所有表的sql语句
    private static String sql_get_all_tables = "select table_name,TABLE_COMMENT from INFORMATION_SCHEMA.tables where TABLE_SCHEMA='"+schema+"' and TABLE_TYPE='BASE TABLE'";
    //查询所有字段的sql语句
    private static String sql_get_all_columns = "select column_name,data_type,character_octet_length,column_default,column_key,is_nullable,extra,column_comment from information_schema.`columns` where table_name='{table_name}' and table_schema='"+schema+"'";
    public static void main(String[] args) throws Exception {
        //初始化word文档
        Document document = new Document(PageSize.A4);
        RtfWriter2.getInstance(document,new FileOutputStream("D:/fss.doc"));
        document.open();
        //查询开始
        Connection conn = getConnection();
        //获取所有表
        List tables = getDataBySQL(sql_get_all_tables,conn);
        int i=1;
        for (Iterator iterator = tables.iterator(); iterator.hasNext();) {
            String [] arr = (String []) iterator.next();
            //循环获取字段信息
            System.out.print(i+".正在处理数据表-----------"+arr[0]);
            addTableMetaData(document,arr,i);
            List columns = getDataBySQL(sql_get_all_columns.replace("{table_name}", arr[0]),conn);
            addTableDetail(document,columns);
            addBlank(document);
            System.out.println("...done");
            i++;
        }
        document.close();
        conn.close();
    }
    /**
     * 添加一个空行
     * @param document
     * @throws Exception
     */
    public static void addBlank(Document document)throws Exception{
        Paragraph ph = new Paragraph("");
        ph.setAlignment(Paragraph.ALIGN_LEFT);
        document.add(ph);
    }
    /**
     * 添加包含字段详细信息的表格
     * @param document
     * @param columns
     * @throws Exception
     */
    public static void addTableDetail(Document document,List columns)throws Exception{
        Table table = new Table(9);
        table.setWidth(100f);//表格 宽度100%
        table.setBorderWidth(1);
        table.setBorderColor(Color.BLACK);
        table.setPadding(0);
        table.setSpacing(0);
        Cell cell1 = new Cell("序号");// 单元格
        cell1.setHeader(true);

        Cell cell2 = new Cell("列名");// 单元格
        cell2.setHeader(true);

        Cell cell3 = new Cell("类型");// 单元格
        cell3.setHeader(true);

        Cell cell4 = new Cell("长度");// 单元格
        cell4.setHeader(true);

        Cell cell5 = new Cell("默认值");// 单元格
        cell5.setHeader(true);

        Cell cell6 = new Cell("键");// 单元格
        cell6.setHeader(true);

        Cell cell7=new Cell("允许为空");
        cell7.setHeader(true);

        Cell cell8 = new Cell("自动增长");// 单元格
        cell8.setHeader(true);

        Cell cell9 = new Cell("注释");// 单元格
        cell9.setHeader(true);
        //设置表头格式
        table.setWidths(new float[]{8f,18f,10f,8f,10f,10f,7f,7f,21f});
        cell1.setHorizontalAlignment(Cell.ALIGN_LEFT);
        cell1.setBackgroundColor(Color.gray);
        cell2.setHorizontalAlignment(Cell.ALIGN_LEFT);
        cell2.setBackgroundColor(Color.gray);
        cell3.setHorizontalAlignment(Cell.ALIGN_LEFT);
        cell3.setBackgroundColor(Color.gray);
        cell4.setHorizontalAlignment(Cell.ALIGN_LEFT);
        cell4.setBackgroundColor(Color.gray);
        cell5.setHorizontalAlignment(Cell.ALIGN_LEFT);
        cell5.setBackgroundColor(Color.gray);
        cell6.setHorizontalAlignment(Cell.ALIGN_LEFT);
        cell6.setBackgroundColor(Color.gray);
        cell7.setHorizontalAlignment(Cell.ALIGN_LEFT);
        cell7.setBackgroundColor(Color.gray);
        cell8.setHorizontalAlignment(Cell.ALIGN_LEFT);
        cell8.setBackgroundColor(Color.gray);
        cell9.setHorizontalAlignment(Cell.ALIGN_LEFT);
        cell9.setBackgroundColor(Color.gray);
        table.addCell(cell1);
        table.addCell(cell2);
        table.addCell(cell3);
        table.addCell(cell4);
        table.addCell(cell5);
        table.addCell(cell6);
        table.addCell(cell7);
        table.addCell(cell8);
        table.addCell(cell9);
        table.endHeaders();// 表头结束
        int x = 1;
        for (Iterator iterator = columns.iterator(); iterator.hasNext();) {
            String [] arr2 = (String []) iterator.next();
            Cell c1 = new Cell(x+"");
            //字段名:column_name
            Cell c2 = new Cell(arr2[0]);
            //字段类型:data_type
            Cell c3 = new Cell(arr2[1]);
            //数据长度:character_octet_length
            Cell c4 = new Cell(arr2[2]);
            //默认值:column_default
            Cell c5=new Cell(arr2[3]);
            //主键:column_key
            String key = keyType.get(arr2[4]);
            if(key==null)key = "";
            Cell c6 = new Cell(key);
            //是否允许为NULL:is_nullable
            String yn = keyType.get(arr2[5]);
            if(yn==null)yn = "";
            Cell c7=new Cell(yn);
            //自动增长:extra
            String auto = keyType.get(arr2[6]);
            if(auto==null)auto = "";
            Cell c8 = new Cell(auto);
            //注释:column_comment
            Cell c9 = new Cell(arr2[7]);

            c1.setHorizontalAlignment(Cell.ALIGN_LEFT);
            c2.setHorizontalAlignment(Cell.ALIGN_LEFT);
            c3.setHorizontalAlignment(Cell.ALIGN_LEFT);
            c4.setHorizontalAlignment(Cell.ALIGN_LEFT);
            c5.setHorizontalAlignment(Cell.ALIGN_LEFT);
            c6.setHorizontalAlignment(Cell.ALIGN_LEFT);
            c7.setHorizontalAlignment(Cell.ALIGN_LEFT);
            c8.setHorizontalAlignment(Cell.ALIGN_LEFT);
            c9.setHorizontalAlignment(Cell.ALIGN_LEFT);
            table.addCell(c1);
            table.addCell(c2);
            table.addCell(c3);
            table.addCell(c4);
            table.addCell(c5);
            table.addCell(c6);
            table.addCell(c7);
            table.addCell(c8);
            table.addCell(c9);
            x++;
        }
        document.add(table);
    }
    /**
     * 增加表概要信息
     * @param dcument
     * @param arr
     * @param i
     * @throws Exception
     */
    public static void addTableMetaData(Document dcument,String [] arr,int i) throws Exception{
        //Paragraph ph = new Paragraph(i+". 表名: "+arr[0]+"        说明: "+(arr[1]==null?"":arr[1]));
        Paragraph ph = new Paragraph(i+". 表名: "+arr[0]);
        ph.setAlignment(Paragraph.ALIGN_LEFT);
        dcument.add(ph);
    }
    /**
     * 把SQL语句查询出列表
     * @param sql
     * @param conn
     * @return
     */
    public static List getDataBySQL(String sql,Connection conn){
        Statement stmt = null;
        ResultSet rs = null;
        List list = new ArrayList();
        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            while(rs.next()){
                String [] arr = new String[rs.getMetaData().getColumnCount()];
                for(int i=0;i<arr.length;i++){
                    arr[i] = rs.getString(i+1);
                }
                list.add(arr);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally{
            try {
                if(rs!=null)rs.close();
                if(stmt!=null)stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return list;
    }
    /**
     * 获取数据库连接
     * @return
     */
    public static Connection getConnection(){
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
