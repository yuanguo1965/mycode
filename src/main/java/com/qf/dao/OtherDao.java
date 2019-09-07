package com.qf.dao;

import com.qf.pojo.Other;

import java.util.List;

public interface OtherDao {
    void insert(Other other);
//插入对象 获取自增长的id
    void insertAndGetId(Other oter);
//批量插入
    void batchInsert(List<Other> list);
}
