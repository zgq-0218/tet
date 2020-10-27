package com.atguigu3.exer;

import com.atguigu2.util.JDBCUtils;
import org.junit.Test;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.Scanner;

public class Exer2Test {

    @Test
    public void testInsert() {
        //问题1：向examstudent表中添加一条记录
        /*
             Type:
             IDCard:
             ExamCard:
             StudentName:
             Location:
             Grade:
         */
        Scanner sc = new Scanner(System.in);
        System.out.print("四级/六级：");
        int type = sc.nextInt();
        System.out.print("身份证号：");
        String IDCard = sc.next();
        System.out.print("准考证号：");
        String examCard = sc.next();
        System.out.print("学生姓名：");
        String studentName = sc.next();
        System.out.print("所在城市：");
        String location = sc.next();
        System.out.print("考试成绩：");
        int grade = sc.nextInt();

        String sql = "insert into examstudent(type,IDCard,examCard,studentName,location,grade) values(?,?,?,?,?,?)";
        int insertCount = update(sql, type, IDCard, examCard, studentName, location, grade);
        if (insertCount >0){
            System.out.println("添加成功");
        }else {
            System.out.println("添加失败");
        }
    }

    //通用的增删改操作
    public static int update(String sql,Object ...args) {     //sql中占位符的个数与可变参数的长度相同
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


    //问题2：根据身份证号或者准考证号查询学生成绩信息
    @Test
    public void queryWithIDCardOrExamCard(){
        System.out.println("请输入您要选择的类型");
        System.out.println("a:准考证号");
        System.out.println("b:身份证号");
        Scanner sc = new Scanner(System.in);
        String selection = sc.next();
        if("a".equals(selection)){
            System.out.print("请输入准考证号：");
            String examCard = sc.next();
            String sql = "select FlowID flowID,type,IDCard,ExamCard examCard,StudentName name,Location location,Grade grade from examstudent where ExamCard =?";
            Student student = getInstance(Student.class, sql, examCard);
            if(student!=null){
                System.out.println("==========查询结果===========");
                System.out.println("流水号："+student.getFlowID()+"\n四级/六级："+student.getType()+"\n身份证号："+student.getIDCard()+"\n准考证号："+
                        student.getExamCard()+"\n学生姓名："+student.getName()+"\n区域:"+student.getLocation()+"\n成绩"+student.getGrade());
            }else {
                System.out.println("输入的准考证号有误！");
            }

        }else if("b".equals(selection)){
            System.out.print("请输入身份证号：");
            String IDCard = sc.next();
            String sql = "select FlowID flowID,type,IDCard,ExamCard examCard,StudentName name,Location location,Grade grade from examstudent where IDCard =?";
            Student student = getInstance(Student.class, sql, IDCard);
            if (student!=null){
                System.out.println("==========查询结果===========");
                System.out.println("流水号："+student.getFlowID()+"\n四级/六级："+student.getType()+"\n身份证号："+student.getIDCard()+"\n准考证号："+
                        student.getExamCard()+"\n学生姓名："+student.getName()+"\n区域:"+student.getLocation()+"\n成绩"+student.getGrade());
            }else {
                System.out.println("输入的身份证号有误");
            }
        }

    }

    //通用的查询操作，结果集为一行
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


    //问题3：删除指定的学生信息(先查找，再删除)
    @Test
    public void testDeleteByExamCard(){
        System.out.print("请输入学生的考号:");
        Scanner sc = new Scanner(System.in);
        String examCard = sc.next();
        //查询指定准考证号的学生
        String sql = "select FlowID flowID,type,IDCard,ExamCard examCard,StudentName name,Location location,Grade grade from examstudent where ExamCard =?";
        Student student = getInstance(Student.class, sql, examCard);
        if(student==null){
            System.out.println("查无此人，请重新输入");
        }else{
            String sql1 = "delete from examstudent where examCard = ?";
            int deleteCount = update(sql1, examCard);
            if(deleteCount >0){
                System.out.println("删除成功");
            }
        }
    }


    //问题3：删除指定的学生信息(直接删除)
    @Test
    public void testDeleteByExamCard1() {
        while (true) {
            System.out.print("请输入学生的考号:");
            Scanner sc = new Scanner(System.in);
            String examCard = sc.next();

            String sql1 = "delete from examstudent where examCard = ?";
            int deleteCount = update(sql1, examCard);
            if (deleteCount > 0) {
                System.out.println("删除成功");
            } else {
                System.out.println("查无此人，请重新输入");
            }

        }
    }


}
