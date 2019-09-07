package com.qf.dao;

import com.qf.pojo.User;
import org.apache.ibatis.annotations.Param;
import org.junit.runners.Parameterized;

import java.util.List;
import java.util.Map;

public interface UserDao {
    List<User> getAll();
    List<User> getUserByName(String name);
    //分页查询
    List<User> pageQuery( @Param("offset")int offset,@Param("limit")int limit);
    List<String> singleColumn();
    //
    List<User> multipleColumn();
    List<Map<String,Object>>  multipleColumn1();
    //in 查询
    List<User> inQuery(List<Integer> ids);
    //多条件查询

    List<User> multipleParams(Map<String, Object> map);
    //动态拼接,判断条件

    List<User> dynamicQuery(Map<String, Object> map);

    void deleteById( Integer id);
    List<User> pluginPageQuery();
}
