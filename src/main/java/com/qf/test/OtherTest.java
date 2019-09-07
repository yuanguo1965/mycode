package com.qf.test;

import com.qf.dao.OtherDao;
import com.qf.pojo.Other;
import com.qf.utils.SqlSessionUtils;
import org.apache.commons.text.RandomStringGenerator;
import org.apache.ibatis.session.SqlSession;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.ArrayList;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class OtherTest {
//    private final static Logger LOG = LogManager.getLogger(JPATest.class);
    private SqlSession sqlSession;

    @Before
    public void init() {
        sqlSession = SqlSessionUtils.getSqlSession();
    }

//普通插入
    @Test
    public  void insert(){
        OtherDao otherDao=sqlSession.getMapper(OtherDao.class);
//        Other other=new Other( 4,"李四");
        Other other=new Other();
        other.setName("张三");
        otherDao.insert(other);


        System.out.println(other.getId());
        sqlSession.commit();

    }

    @Test
    public  void insertAndGetId(){
        OtherDao otherDao=sqlSession.getMapper(OtherDao.class);

        Other other=new Other();
        other.setName("你好");
        otherDao.insert(other);


        sqlSession.commit();
        System.out.println(other.getId());
    }


    // 批量插入，采取foreach的方式
    @Test
    public void batchInsert() {
        OtherDao otherDao = sqlSession.getMapper(OtherDao.class);

        // 随机生成的字符串在 a - z之间
        RandomStringGenerator generator = new RandomStringGenerator.Builder()
                .withinRange(new char[]{'a', 'z'}).build();

        // list默认容量 10  newCapacity = capacity + capacity >> 1;
        List<Other> list = new ArrayList<>(50010);

        long begin = System.currentTimeMillis();

        for (int i = 0; i < 50000; i++) {
            Other other = new Other();
            other.setName(generator.generate(4));

            list.add(other);
        }

        otherDao.batchInsert(list);

        sqlSession.commit();

        long end = System.currentTimeMillis();

        System.out.println("时间的消耗" + (end - begin));  //1823  1643 1939
    }









}
