package com.atguigu4.blob;

import com.atguigu2.util.JDBCUtils;
import org.junit.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class InsertTest {
    //批量插入的方式二：使用PreparedStatement
    @Test
    public void testInsert(){
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = JDBCUtils.getConnection();
            String sql = "insert into goods(name) values(?)";
            ps = conn.prepareStatement(sql);
            for(int i=1;i<=20000;i++){
                ps.setObject(1,"name"+i);
                ps.execute();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResourse(conn,ps);
        }
    }

    //批量插入的方式三：
    //1.addBatch(),executeBatch(),clearBatch()
    //2.mysql服务器默认是关闭批处理的，我们需要通过一个参数，让mysql开启批处理的支持 ?rewriteBatchedStatements=true 写在配置文件的url后面
    //3.使用更新的mysql 驱动：mysql-connector-java-5.1.37-bin.jar
    @Test
    public void testInsert2(){
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            long start =System.currentTimeMillis();   //测试使用时间开始

            conn = JDBCUtils.getConnection();
            String sql = "insert into goods(name) values(?)";
            ps = conn.prepareStatement(sql);
            for(int i=1;i<=1000000;i++){
                ps.setObject(1,"name"+i);

                //1."攒"sql
                ps.addBatch();

                if (i%500 == 0 ){
                    //2.执行batch
                    ps.executeBatch();

                    //3.清空batch
                    ps.clearBatch();
                }

            }

            long end = System.currentTimeMillis();     //测试时间结束
            System.out.println("花费的时间为"+(end-start));      //    11902ms

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResourse(conn,ps);
        }
    }

    //批量插入的方式四：设置连接不允许自动提交
    @Test
    public void testInsert3(){
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            long start =System.currentTimeMillis();   //测试使用时间开始

            conn = JDBCUtils.getConnection();

            //不允许自动提交
            conn.setAutoCommit(false);

            String sql = "insert into goods(name) values(?)";
            ps = conn.prepareStatement(sql);
            for(int i=1;i<=1000000;i++){
                ps.setObject(1,"name"+i);

                //1."攒"sql
                ps.addBatch();

                if (i%500 == 0 ){
                    //2.执行batch
                    ps.executeBatch();

                    //3.清空batch
                    ps.clearBatch();
                }
            }

            //提交数据
            conn.commit();

            long end = System.currentTimeMillis();     //测试时间结束
            System.out.println("花费的时间为"+(end-start));    //7834ms

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResourse(conn,ps);
        }
    }
}
