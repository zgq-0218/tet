package com.atguigu2.preparedstatement.crud;

import com.atguigu2.bean.Customer;
import com.atguigu2.bean.Order;
import com.atguigu2.util.JDBCUtils;
import org.junit.Test;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.List;


public class PreparedStatementQueryTest {

    /*
        使用PreparedStatement实现针对不同表的通用得查询操作,返回多条记录
     */

    @Test
    public void testGetForList(){
        String sql = "select id,name,email from customers where id < ?";
        List<Customer> list = getForList(Customer.class, sql, 12);
        list.forEach(System.out::println);    //集合遍历

        String sql1 = "select order_id orderId,order_name orderName from `order` where order_id < ?";
        List<Order> orderList = getForList(Order.class, sql1, 5);
        orderList.forEach(System.out::println);

    }

    //通用查询操作,返回多条记录
    public <T> List<T> getForList(Class<T> clazz, String sql, Object ...args){
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            //1.获取数据库的连接
            conn = JDBCUtils.getConnection();
            //2.预编译sql语句，返回prepareStatement实例
            ps = conn.prepareStatement(sql);
            //3.填充占位符
            for(int i = 0 ;i < args.length ; i++){
                ps.setObject(i+1,args[i]);
            }
            //4.执行，并返回结果集
            rs = ps.executeQuery();
            //获取结果集的元数据:ResultSetMetaData
            ResultSetMetaData rsmd = rs.getMetaData();
            //通过ResultSetMetaData获取结果集中的数据
            int columnCount = rsmd.getColumnCount();  //通过元数据得到结果集的列数

            //创建集合对象
            ArrayList<T> list = new ArrayList<T>();
            while (rs.next()){
                T t = clazz.getConstructor().newInstance();

//                T t = clazz.newInstance();   //利用反射构建对象

                //处理结果集一行数据中的每一列
                for(int i = 0 ;i < columnCount ; i++){
                    Object columnValue = rs.getObject(i + 1);     //获取结果集每个列的值
//                    String columnName = rsmd.getColumnName(i + 1);           //获取结果集每个列的列名 （不推荐使用）
                    String columnLabel = rsmd.getColumnLabel(i + 1);          //获取结果集每个列的别名（没有别名就是列名）
                    //给t对象指定的columnName属性，赋值为columnValue ：通过反射
                    Field field = clazz.getDeclaredField(columnLabel);
                    field.setAccessible(true);
                    field.set(t,columnValue);
                }
                list.add(t);
            }
            return list;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //5.关闭资源
            JDBCUtils.closeResourse(conn,ps,rs);
        }

        return null ;
    }





    /*
        使用PreparedStatement实现针对不同表的通用得查询操作,返回一条记录
    */

    @Test
    public void testGetInstance(){

        //查询customers表信息
        String sql = "select id,name,email from customers where id =?";
        Customer customer = getInstance(Customer.class, sql, 12);
        System.out.println(customer);

        //查询order表信息
        String sql1 = "select order_id orderId,order_name orderName from `order` where order_id =?";
        Order order = getInstance(Order.class, sql1, 1);
        System.out.println(order);
    }

    //通用的查询操作，返回一条记录
    public <T> T getInstance(Class<T> clazz,String sql,Object ...args){
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            //1.获取数据库的连接
            conn = JDBCUtils.getConnection();
            //2.预编译sql语句，返回prepareStatement实例
            ps = conn.prepareStatement(sql);
            //3.填充占位符
            for(int i = 0 ;i < args.length ; i++){
                ps.setObject(i+1,args[i]);
            }
            //4.执行，并返回结果集
            rs = ps.executeQuery();
            //获取结果集的元数据:ResultSetMetaData
            ResultSetMetaData rsmd = rs.getMetaData();
            //通过ResultSetMetaData获取结果集中的数据
            int columnCount = rsmd.getColumnCount();  //通过元数据得到结果集的列数

            if(rs.next()){
                T t = clazz.getConstructor().newInstance();

//                T t = clazz.newInstance();   //利用反射构建对象

                //处理结果集一行数据中的每一列
                for(int i = 0 ;i < columnCount ; i++){
                    Object columnValue = rs.getObject(i + 1);     //获取结果集每个列的值
//                    String columnName = rsmd.getColumnName(i + 1);           //获取结果集每个列的列名 （不推荐使用）
                    String columnLabel = rsmd.getColumnLabel(i + 1);          //获取结果集每个列的别名（没有别名就是列名）
                    //给t对象指定的columnName属性，赋值为columnValue ：通过反射
                    Field field = clazz.getDeclaredField(columnLabel);
                    field.setAccessible(true);
                    field.set(t,columnValue);
                }
                return t;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //5.关闭资源
            JDBCUtils.closeResourse(conn,ps,rs);
        }

        return null ;
    }

}
