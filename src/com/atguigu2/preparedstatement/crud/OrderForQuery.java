package com.atguigu2.preparedstatement.crud;


/*
    针对Order表的通用查询操作
 */

import com.atguigu2.bean.Order;
import com.atguigu2.util.JDBCUtils;
import com.sun.org.apache.xpath.internal.operations.Or;
import jdk.nashorn.internal.scripts.JD;
import org.junit.Test;

import java.lang.reflect.Field;
import java.sql.*;

public class OrderForQuery {

    /*
        通用的针对于Order表的查询操作
     */

    /*
        针对于表的字段名与类的属性名不相同的情况：
            1.必须声明sql时，使用类的属性名来命名字段的别名
            2.使用结果集元数据获取列名时，使用getColumnLabel()替代getColumnName()来获取列的别名
            说明：如果sql中没有给字段取别名，getColumnLabel()获取的就是列名
     */
    @Test
    public void testQueryForOrder() throws Exception {
        String sql = "select order_id orderId,order_name orderName,order_date orderDate from `order` where order_id =?";
        Order order = orderForQuery(sql, 1);
        System.out.println(order);
    }

    public Order orderForQuery(String sql,Object... args) throws Exception {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            //1.获取数据库连接
            conn = JDBCUtils.getConnection();
            //2.预编译sql语句，返回ps对象
            ps = conn.prepareStatement(sql);
            //3.填充占位符
            for (int i = 0 ; i < args.length ; i++){
                ps.setObject(i+1,args[i]);
            }
            //4.执行，并返回结果集,并放到对象中
            rs = ps.executeQuery();
            //4.1 获取结果集的元数据
            ResultSetMetaData rsmd = rs.getMetaData();
            //4.2 通过元数据获取结果集的列数
            int columnCount = rsmd.getColumnCount();
            //4.3 对结果集的每一行进行处理
            if (rs.next()){
                Order order = new Order();
                for (int i = 0 ; i<columnCount ; i++){
                    Object columnValue = rs.getObject(i+1);    //获取结果集每列的值
//                    String columnName = rsmd.getColumnName(i+1);           //获取结果集每列的列名(不推荐使用)
                    String columnLabel = rsmd.getColumnLabel(i + 1);       //获取结果集每列的别名（没有就是列名）

                    //给order对象指定的columnName属性，赋值为columnValue ：通过反射
                    Field field = Order.class.getDeclaredField(columnLabel);
                    field.setAccessible(true);
                    field.set(order,columnValue);

                }
                return order;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //5.关闭资源
            JDBCUtils.closeResourse(conn,ps,rs);
        }

        return null;
    }



    /*
    针对Order表进行查询操作
     */
    @Test
    public void testQuery1() {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            //1.建立数据库连接
            conn = JDBCUtils.getConnection();
            //2.预编译sql语句，返回ps实例
            String sql = "select order_id ,order_name ,order_date from `order` where order_id = ?";
            ps = conn.prepareStatement(sql);
            //3.填充占位符
            ps.setObject(1,1);
            //4.执行，并返回结果集
            rs = ps.executeQuery();

//            //4.1 获取结果集的元数据:ResultSetMetaData
//            ResultSetMetaData rsmd = rs.getMetaData();
//            //4.2 通过元数据ResultSetMetaData获取结果集的列数
//            int columnCount = rsmd.getColumnCount();

            //4.3 处理结果集中的每一行
            if(rs.next()){
//                Order order = new Order();
//                for(int i = 0 ; i<columnCount;i++){
//                    Object columnvalue = rs.getObject(i + 1);    //获取结果集中的每个列的值
//                    String columnName = rsmd.getColumnName(i+1);             //获取结果集中的每个列的列名
//
//                    //给order对象指定的columnName属性，赋值为columnvalue,通过反射
//                    Field field = Order.class.getDeclaredField(columnName);
//                    field.setAccessible(true);
//                    field.set(order,columnvalue);
//                }
//                return order;
                int id = (int)rs.getObject(1);
                String name = (String) rs.getObject(2);
                Date date = (Date) rs.getObject(3);
                Order order = new Order(id, name, date);
                System.out.println(order);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            //5.关闭资源
            JDBCUtils.closeResourse(conn,ps,rs);
        }


    }


}
