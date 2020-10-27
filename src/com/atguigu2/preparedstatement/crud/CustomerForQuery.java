package com.atguigu2.preparedstatement.crud;

import com.atguigu2.bean.Customer;
import com.atguigu2.util.JDBCUtils;
import org.junit.Test;

import java.lang.reflect.Field;
import java.sql.*;

public class CustomerForQuery {

    //通过针对Customers表的通用查询操作方法 查询信息
    @Test
    public void testQueryForCustomers(){
        String sql = "select id,name,birth,email from customers where id = ?";
        Customer customer = queryForCustomers(sql, 13);
        System.out.println(customer);

    }

    //针对Customers表的通用查询操作方法
    public Customer queryForCustomers(String sql,Object ...args){
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
                Customer cust = new Customer();

                //处理结果集一行数据中的每一列
                for(int i = 0 ;i < columnCount ; i++){
                    Object columnValue = rs.getObject(i + 1);     //获取结果集每个列的值
//                    String columnName = rsmd.getColumnName(i + 1);           //获取结果集每个列的列名 （不推荐使用）
                    String columnLabel = rsmd.getColumnLabel(i + 1);          //获取结果集每个列的别名（没有别名就是列名）
                    //给cust对象指定的columnName属性，赋值为columnValue ：通过反射
                    Field field = Customer.class.getDeclaredField(columnLabel);
                    field.setAccessible(true);
                    field.set(cust,columnValue);
                }
                return cust;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //5.关闭资源
        JDBCUtils.closeResourse(conn,ps,rs);

        return null ;
    }



    //针对Customers表进行查询操作  （查询操作和增删改的区别就是“执行”步骤改变，需要返回结果集）
    @Test
    public void testQuery1() {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet resultSet = null;
        try {
            //1.获取数据库连接
            conn = JDBCUtils.getConnection();
            //2.预编译sql语句，返回preparedStatement实例
            String sql = "select id,name,email,birth from customers where id = ?";
            ps = conn.prepareStatement(sql);
            //3.填充占位符
            ps.setObject(1,1);
            //4.执行，并返回结果集
            resultSet = ps.executeQuery();
            //处理结果集
            if(resultSet.next()){    // next():判断结果集的下一条是否有数据，如果有数据返回true，并指针下移，如果没数据返回false，指针不下移
                //获取当前这条数据的各个字段值
                int id = resultSet.getInt(1);
                String name = resultSet.getString(2);
                String email = resultSet.getString(3);
                Date birth = resultSet.getDate(4);

                //将数据封装为一个对象
                Customer customer = new Customer(id, name, email, birth);
                System.out.println(customer);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //5.关闭资源
            JDBCUtils.closeResourse(conn,ps,resultSet);
        }
    }
}
