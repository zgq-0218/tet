package com.atguigu2.preparedstatement.crud;

/*
      *使用PreparedStatement来替换Statement，实现对数据表的增删改查操作
      *
      *增删改  ； 查
      *
 */

import com.atguigu.connection.ConnectionTest;
import com.atguigu2.util.JDBCUtils;
import org.junit.AfterClass;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Properties;

public class PreparedStatementUpdateTest {
    //调用通用的增删改操作进行删除和更改操作
    @Test
    public void testCommonUpdate(){
        //删除
//        String sql = "delete from customers where id =?";
//        update(sql,3);

        //更改
        String sql = "update `order` set order_name =? where order_id =?";      //当表为order等关键字时应当用` `将其括起
        update(sql,"gg",2);
    }

    //通用的增删改操作
    public void update(String sql,Object ...args) {     //sql中占位符的个数与可变参数的长度相同
        Connection conn =null;
        PreparedStatement ps = null;
        try {
            //1.获取数据库连接
            conn = JDBCUtils.getConnection();
            //2.预编译sql语句，返回Preparement的实例
            ps = conn.prepareStatement(sql);
            //3.填充占位符
            for (int i = 0 ; i < args.length ; i++){         //占位符不知道有几个，所以用循环
                ps.setObject(i+1,args[i]);     //注意：sql中的索引从1开始，java中的数组args索引从0开始
            }
            //4.执行
            ps.execute();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //5.资源的关闭
            JDBCUtils.closeResourse(conn,ps);
        }


    }



    //修改customers中的一条记录
    @Test
    public void testUpdate(){

        Connection conn = null;
        PreparedStatement ps = null;
        try {
            //1.获取数据库的连接
            conn = JDBCUtils.getConnection();
            //2.预编译sql语句，返回PrepareStatement的实例
            String sql = "update customers set name = ? where id = ?";
            ps = conn.prepareStatement(sql);
            //3.填充占位符
            ps.setObject(1,"莫扎特");
            ps.setObject(2,18);
            //4.执行
            ps.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            //5.资源的关闭
            JDBCUtils.closeResourse(conn,ps);
        }

    }



    //向customers中添加一条记录
    @Test
    public void getConnection5(){
        Connection conn = null;
        PreparedStatement ps = null ;
        try {
            //1.读取配置文件中的四个基本信息
            InputStream is = ClassLoader.getSystemClassLoader().getResourceAsStream("jdbc.properties");

            Properties pros = new Properties();
            pros.load(is);
            String user = pros.getProperty("user");
            String password = pros.getProperty("password");
            String url = pros.getProperty("url");
            String driverClass = pros.getProperty("driverClass");

            //2.加载驱动
            Class.forName(driverClass);

            //3.获取连接
            conn = DriverManager.getConnection(url, user, password);
//        System.out.println(conn);

            //4.预编译sql语句，返回PreparedStatement的实例
            String sql = "insert into customers(name,email,birth) values(?,?,?)";      //  ？：占位符
            ps = conn.prepareStatement(sql);

            //5.填充占位符
            ps.setString(1, "哪吒");
            ps.setString(2, "nezha.com");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            java.util.Date date = sdf.parse("1000-01-01");
            ps.setDate(3, new Date(date.getTime()));

            //6.执行操作
            ps.execute();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try {
                if(ps != null)
                    ps.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }

            try {
                if(conn !=null)
                    conn.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }
}
