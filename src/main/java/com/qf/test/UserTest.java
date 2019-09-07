package com.qf.test;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.qf.dao.UserDao;
import com.qf.pojo.User;
import com.qf.utils.SqlSessionUtils;

import org.apache.ibatis.session.SqlSession;
import org.junit.Before;
import org.junit.Test;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class UserTest {
    private SqlSession sqlSession;
    @Before
    public  void init(){
        sqlSession= SqlSessionUtils.getSqlSession();
    }
    @Test
    public void getAll(){
        UserDao userDao=sqlSession.getMapper(UserDao.class);
        List<User> list=userDao.getAll();
        list.forEach(u ->{
            System.out.println(u.getId()+"::"+u.getName());
        });

    }


    //模糊查询
    @Test
    public void getUserByName(){
        UserDao userDao=sqlSession.getMapper(UserDao.class);
        List<User> list=userDao.getUserByName("%张%");
        list.forEach(u ->{
            System.out.println(u.getId()+"::"+u.getName());
        });
    }
    //分页查询
    @Test
    public  void pageQuery(){
        UserDao userDao=sqlSession.getMapper(UserDao.class);
        List<User> list=userDao.pageQuery(2,5);
        list.forEach(u ->{
            System.out.println(u.getId()+"::"+u.getName());
        });
    }

    //
    @Test
    public void  singleColumn(){
        UserDao userDao=sqlSession.getMapper(UserDao.class);
        List<String> list=userDao.singleColumn();
        list.forEach(u ->
            System.out.println(u)
        );
    }

    //查询多列数据
    @Test
    public  void multipleColumn(){
        UserDao userDao=sqlSession.getMapper(UserDao.class);
        List<User> list=userDao.multipleColumn();
        list.forEach(u ->{
            System.out.println(u.getEmail()+"::"+u.getName());
        });
    }

    //
    @Test
    public  void multipleColumn1(){
        UserDao userDao=sqlSession.getMapper(UserDao.class);
        List<Map<String,Object>> list=userDao.multipleColumn1();
        list.forEach(map ->{
            System.out.println(map.get("name")+"::"+map.get("email"));
        });
    }
    //
    @Test
    public  void inQuery(){
        UserDao userDao=sqlSession.getMapper(UserDao.class);
        List<Integer> ids = Arrays.asList(18, 25, 10, 11);
        List<User> list=userDao.inQuery(ids);
        list.forEach(u ->{
            System.out.println(u.getEmail()+"::"+u.getName());
        });
    }

    //多条件查询
    @Test
    public void multipleParams(){
        UserDao userDao=sqlSession.getMapper(UserDao.class);
        Date begin =null;
        Date end =null;
       SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//          SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            begin=  sdf.parse("2010-06-23 16:47:17");
            end =  sdf.parse("2019-06-23 16:47:17");
        } catch (ParseException e) {
            e.printStackTrace();
        }
       Map<String,Object> map =  new HashMap<>();
        map.put("begin", begin);
        map.put("end", end);
        map.put("name", "%张%");
        map.put("gender", 'F');


        List<User> list = userDao.multipleParams(map);
        list.forEach(u -> System.out.println(u.getEmail() + "::" + u.getName()));

    }

    @Test
    public void dynamicQuery() {
        UserDao userDao = sqlSession.getMapper(UserDao.class);

        Date begin = null;
        Date end = null;

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        try {
            end = sdf.parse("2019-06-23 16:47:17");
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Map<String, Object> map = new HashMap<>();
        map.put("begin", begin);
        map.put("end", end);
        map.put("name", null);
        map.put("gender", 'F');

        List<User> list = userDao.dynamicQuery(map);
        list.forEach(u -> System.out.println(u.getEmail() + "::" + u.getName()));
    }
    @Test
    public  void deleteById(){
        UserDao userDao=sqlSession.getMapper(UserDao.class);
        userDao.deleteById(27);
        sqlSession.commit();
    }

    @Test
    public void pluginPageQuery() {
        UserDao userDao = sqlSession.getMapper(UserDao.class);

//        PageHelper.startPage(2, 5);
        PageHelper.startPage(2,5);

        List<User> list = userDao.pluginPageQuery();

        PageInfo<User> pageInfo = PageInfo.of(list);

        // 遍历数据
        list.forEach(u -> System.out.println(u.getEmail() + "::" + u.getName()));


        System.out.println(pageInfo.getTotal()); //总数据量
        System.out.println(pageInfo.getPages());  //总共多少页
        System.out.println(pageInfo.getPageNum()); //当前页
        System.out.println(pageInfo.getNextPage()); //下一页是第几页
        System.out.println(pageInfo.getPrePage()); //上一页是第几页
        System.out.println(pageInfo.getPageSize()); //每页的数据
    }

}
