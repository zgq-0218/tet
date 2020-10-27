package com.atguigu3.exer;

import com.atguigu2.util.JDBCUtils;
import org.junit.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Scanner;

public class Exer1Test {

    @Test
    public void testInsert(){
        Scanner scanner = new Scanner(System.in);
        System.out.print("请输入用户名:");
        String name = scanner.nextLine();
        System.out.print("请输入邮箱:");
        String email = scanner.nextLine();
        System.out.print("请输入生日:");
        String birthday = scanner.nextLine();    //默认格式 '1992-01-12'

        String sql = "insert into customers(name,email,birth) values(?,?,?)";
        int insertcount = update(sql, name, email, birthday);
        if(insertcount >0){
            System.out.println("添加成功");
        }else{
            System.out.println("添加失败");
        }

    }

    //通用的增删改操作
    public int update(String sql,Object ...args) {     //sql中占位符的个数与可变参数的长度相同
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
//            return ps.execute();   //如果时查询操作，有结果集，则返回true，没有返回结果集则返回false
            return ps.executeUpdate();   //返回操作数据的行数

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //5.资源的关闭
            JDBCUtils.closeResourse(conn,ps);
        }
        return 0;

    }
}
