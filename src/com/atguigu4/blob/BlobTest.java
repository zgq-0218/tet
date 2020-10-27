package com.atguigu4.blob;

/*
    使用PreparedStatement操作Blob类型的数据
 */

import com.atguigu2.bean.Customer;
import com.atguigu2.util.JDBCUtils;
import org.junit.Test;

import java.io.*;
import java.sql.*;

public class BlobTest {
    //向数据表customer中插入Blob类型的字段
    @Test
    public void testInsert() throws Exception {
        Connection conn = null;
        PreparedStatement ps = null;
        FileInputStream is = null;
        try {
            conn = JDBCUtils.getConnection();
            String sql = "insert into customers(name,email,birth,photo) values(?,?,?,?)";
            ps = conn.prepareStatement(sql);
            ps.setObject(1,"张封");
            ps.setObject(2,"zhang@qq.com");
            ps.setObject(3,"1992-02-18");
            is = new FileInputStream(new File("微信图片_20201012205457.jpg"));
            ps.setBlob(4,is);
            ps.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }

        is.close();
        JDBCUtils.closeResourse(conn,ps);
    }

    //查询数据表customers中Blob类型的字段
    @Test
    public void testQuery(){
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        InputStream is = null;
        FileOutputStream fos =null;
        try {
            conn = JDBCUtils.getConnection();
            String sql = "select id,name,email,birth,photo from customers where id = ?";
            ps = conn.prepareStatement(sql);
            ps.setInt(1,16);
            rs = ps.executeQuery();
            if (rs.next()){
                int id = rs.getInt( 1);
                String name = rs.getString(2);
                String email = rs.getString(3);
                Date birth = rs.getDate(4);
                Customer customer = new Customer(id, name, email, birth);
                System.out.println(customer);

                //将Blob类型的字段下载下来，以文件的方式保存在本地
                Blob photo = rs.getBlob(5);
                is = photo.getBinaryStream();
                fos = new FileOutputStream("ZHAO.jpg");
                byte[] bys = new byte[1024];
                int len;
                while((len = is.read(bys)) != -1){
                    fos.write(bys,0,len);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                if(is!=null)
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if(fos!=null)
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            JDBCUtils.closeResourse(conn,ps,rs);
        }
    }

}
