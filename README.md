## Mybatis

### 一. 简介

​	mybatis是一个非常轻量级的ORM框架，在3.0之前的版本叫做ibatis，在3.0之后便更名为mybatis.其下载地址为：<https://github.com/mybatis/mybatis-3/releases>

### 二. Mybatis项目搭建

#### 2.1 pom.xml文件配置

```xml
<dependencies>
	<dependency>
   		<groupId>org.mybatis</groupId>
    	<artifactId>mybatis</artifactId>
    	<version>3.5.0</version>
	<dependency>
	<dependency>
		<groupId>mysql</groupId>
		<artifactId>mysql-connector-java</artifactId>
		<version>8.0.16</version>
	</dependency>
</dependencies>
<resources>
	<resource>
		<directory>src/main/resources</directory>
		<includes>
			<include>**/*.properties</include>
			<include>**/*.xml</include>
		</includes>
		<filtering>false</filtering>
	</resource>
	<resource>
		<directory>src/main/java</directory>
		<includes>
			<include>**/*.xml</include>
		</includes>
		<filtering>false</filtering>
	</resource>
</resources>
```

#### 2.2 核心配置文件

```xml
<?xml version="1.0" encoding="UTF-8" ?> 
<!DOCTYPE configuration  PUBLIC "-//mybatis.org//DTD Config 3.0//EN"  
	"http://mybatis.org/dtd/mybatis-3-config.dtd"> 
<configuration>
	<!-- 加载属性文件 -->
	<properties resource="db.properties"></properties>
	<typeAliases>
		<!-- 再Mapper文件中映射的反射类型所对应的包 -->
		<package name="com.zelin.myb.pojo"/>
	</typeAliases>
	<environments default="mysql">
		<!-- 环境 -->
		<environment id="mysql">
			<!-- 事务管理，使用JDBC -->
			<transactionManager type="JDBC"></transactionManager>
			<!-- 使用简单的连接池 -->
			<dataSource type="POOLED">
				<property name="driver" value="${driver_class}"/>
				<property name="url" value="${url}"/>
				<property name="username" value="${username}"/>
				<property name="password" value="${password}"/>
			</dataSource>
		</environment>
	</environments>
	<mappers>
		<mapper resource="com/qf/dao/EmpDao.xml"/>
	</mappers>
</configuration>
```

#### 2.3 获取SqlSession

```java
public class SessionUtils {
	private static SqlSessionFactory sqlSessionFactory;
	static{
		try {
			InputStream is = new FileInputStream(Resources
				.getResourceAsFile("Mybatis-config.xml"));
			sqlSessionFactory = new SqlSessionFactoryBuilder()
				.build(is);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static SqlSession getSession(){
		return sqlSessionFactory.openSession();
	}
}
```

#### 2.4 接口编写

```java
public interface EmpDao {
	public List<Emp> getEmpList();
}
```

2.5 映射文件编写

```xml
<?xml version="1.0" encoding="UTF-8" ?> 
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"  
 	      "http://mybatis.org/dtd/mybatis-3-mapper.dtd"> 
<mapper namespace="com.zelin.myb.dao.EmpDao">
	<select id="getEmpList" resultType="Emp">
		select * from emp	
	</select>
</mapper>
```

#### 2.6 测试

```java
@Test
public void getAll(){
	EmpDao empDao = sqlSession.getMapper(EmpDao.class);
	List<Emp> list = empDao.getAll();
	for(Emp e : list){
		System.out.println(e.getEmpno());
	}
}
```

#### 2.7 项目结构入下图所示

![](images/1.png)

### 三. Mapper文件常见配置

#### 3.1 查询

A. like查询

```xml
<select id="likeQuery" resultType="Emp">
	select * from emp1 where ename like #{ename}
</select>
```

B. in查询

```xml
<select id="inQuery" resultType="Emp">
	select * from emp1 where empno in
	<!-- collection：表示方法中的参数名
		  item：为每次将循环的值复制给id
		  open：为以(开始 
		  separator：以什么分隔
		  close: 以什么结束
	-->
	<foreach collection="list" item="id" open="(" separator="," close=")">
		#{id}
	</foreach>
</select>
```

C.分页查询

```xml
<select id="pageQuery" resultType="Emp">
	select * from emp1 limit #{beginIndex}, #{pageSize}
</select>
```

D. 单独列查询

```xml
<select id="singleColumn" resultType="java.lang.Integer">
	select empno from emp1
</select>

<select id="selectDate" resultType="java.util.Date">
	select hiredate from emp1
</select>
```

E. 查询部分列

方式一：使用对象封装

```xml
<select id="multipleColumn" resultType="Emp">
	select hiredate, empno from emp1
</select>
```

方式二：使用Map封装

```xml
<select id="threeColumn" resultType="java.util.Map">
	select sal, hiredate, ename from emp
</select>
```

F. 多条件判断查询

```xml
<select id="conditionQuery" resultType="Emp">
	select * from emp1 
	<where>
		<if test="null != ename and '' != ename.trim()">
			ename like concat('%', concat(#{ename}, '%'))
		</if>
		<if test="null != beginTime">
			AND hiredate > #{beginTime}
		</if>
		<if test="null != endTime">
			<![CDATA[AND hiredate < endTime]]>
		</if>
		<if test="deptno != -1">
			AND deptno = #{deptno}
		</if>
	</where>
</select>
```

#### 3.2 插入

##### 3.2.1 普通插入

```xml
<insert id="insertValue" parameterType="Emp">
	insert into emp(empno, job) values(#{empno}, #{job})
</insert>
```

##### 3.2.2 插入获取主键

A. 方式一

```xml
<insert id="insertDomain" parameterType="Emp">
	<!-- 
		resultType: 返回类型
		keyProperty: 是Emp中的属性
		order: BEFORE是优先执行，如果没有指定，先执行插入，再查询
	-->
	<selectKey resultType="int" keyProperty="empno" order="BEFORE">
		select max(empno) + 1 from emp1 
	</selectKey>
	insert into emp1(empno, ename, sal, hiredate, deptno) values(#{empno}, #{ename}, #{sal}, #{hiredate}, #{deptno})
</insert>
```

B. 方式二

```xml
<insert id="insert" parameterType="Emp">
	insert into user(username, password) values(#{username}, #{password})
	<selectKey resultType="int" keyProperty="id">
		select LAST_INSERT_ID()
	</selectKey>
</insert>
```

##### 3.2.3 批量插入

```xml
<insert id="saveList">
    INSERT INTO emp(empno, job, hiredate) values
   	<foreach collection="list" item="emp" index="index" separator=",">
        (#{emp.empno},#{emp.job},#{emp.hiredate})
    </foreach>
</insert >
```

#### 3.3 删除

```xml
<delete id="deleteById">
	delete from emp1 where empno = #{empno}
</delete>
```

#### 3.4 更新

A. 普通更新

```xml
<update id="updateModel" parameterType="java.util.Map">
	update emp1 set 
		ename = #{ename}, 
		deptno = #{deptno}, 
		job = #{job}
	where empno = #{empno} 
</update>
```

B. 根据条件更新

```xml
<update id="updateModel1" parameterType="java.util.Map">
	update emp
	<set>
		<!-- 会自动将最后一个逗号(,)去掉 -->
		<if test="null != ename and '' != ename.trim()">
			ename = #{ename}, 
		</if>
		<if test="-1 != deptno">
			deptno = #{deptno},
		</if>
		<if test="null != job and '' != job.trim()">
			job = #{job},
		</if>
	</set>
	where empno = #{empno} 
</update>
```

### 四. 多表关联查询

#### 4.1 一查多

```xml
<resultMap type="Dept" id="deptMap">
	<id property="deptno" column="deptno"/>
	<result property="dname" column="dname"/>
	<result property="loc" column="loc"/>
	<collection property="list" javaType="java.util.List" ofType="Emp">
		<id property="empno" column="empno"/>
		<result property="ename" column="ename"/>
		<result property="job" column="job"/>
		<result property="hiredate" column="hiredate"/>
	</collection>
</resultMap>
	
<select id="deptJoinQuery" resultMap="deptMap">
	select d.deptno, d.dname, d.loc, e.empno, e.ename, e.job, e.hiredate 
		from dept d left join emp e on d.deptno = e.deptno 
</select>
```

#### 4.2 多查一

```xml
<resultMap type="Emp" id="empMap">
	<id property="empno" column="empno"/>
	<result property="ename" column="ename"/>
	<result property="job" column="job"/>
	<result property="deptno" column="deptno"/>
	<result property="sal" column="sal"/>
	<result property="hiredate" column="hiredate"/>
	<association property="dept" javaType="Dept">
		<id property="deptno" column="deptno"/>
		<result property="dname" column="dname"/>
		<result property="loc" column="loc"/>
	</association>
</resultMap>
	
<select id="joinQuery" resultMap="empMap">
	select e.empno, e.ename, e.job, e.deptno, e.sal, e.hiredate, d.dname, d.loc 
		from emp e left join dept d on e.deptno = d.deptno
</select>
```

### 五. 分页插件的使用

#### 5.1 引入依赖

```xml
<!-- https://mvnrepository.com/artifact/com.github.pagehelper/pagehelper -->
<dependency>
    <groupId>com.github.pagehelper</groupId>
    <artifactId>pagehelper</artifactId>
    <version>5.1.9</version>
</dependency>
```

#### 5.2 修改mybatis核心配置文件

```xml
<plugins>
	<plugin interceptor="com.github.pagehelper.PageInterceptor">
		<!-- 设置数据库方言 -->
		<property name="helperDialect" value="mysql"/>

		<!-- 默认为false, 设置为true的时候，当分页查询的时候，默认为查询总页数 -->
		<property name="rowBoundsWithCount" value="true"/>

		<!-- 默认值为false, 设置为true的时候，当指定pageSize为0，会查询所有值 -->
		<property name="pageSizeZero" value="true"/>

		<!-- 参数是否合理, 默认为false. 当设置为true的时候，当查询的pageNum<=0的时候，查询第一				页，当pageNum > 总页数的时候，查询最后一页
		-->
		<property name="reasonable" value="true"/>
	</plugin>
</plugins>
```

#### 5.3 使用分页插件

```java
@Test
public void getAll(){
	EmpDao empDao = sqlSession.getMapper(EmpDao.class);

    //在所有需要分页查询的语句前加上这句话，便可实现分页
	PageHelper.startPage(1, 5);   
	List<Emp> list = empDao.getAll();
    // 将其封装成PageInfo,可以活的到更多的信息
	PageInfo<Emp> pageInfo = PageInfo.of(list);

	for(Emp e : list){
		System.out.println(e.getEmpno());
	}

	System.out.println(pageInfo.getPageNum());   //当前页
	System.out.println(pageInfo.getEndRow());  //本次查询最后一条数据
	System.out.println(pageInfo.getPages());  //总页数
	System.out.println(pageInfo.getTotal());  //总
}
```

### 六. mybatis的增强

​	TKMybatis是对mybatis的一种封装，可以简化基本sql的书写，官网参考地址：

​	<https://github.com/abel533/Mapper/wiki>

#### 6.1 配置依赖

```xml
 <dependency>
 	<groupId>tk.mybatis</groupId>
 	<artifactId>mapper</artifactId>
	<version>4.1.5</version>
 </dependency>
```

#### 6.2 数据访问接口编写

```java
public interface EmpDao extends MySqlMapper<Emp>, ExampleMapper<Emp> {
}
```

#### 6.3 config配置

```java
@Before
public void initSqlSession() {
	this.sqlSession = SqlSessionUtils.getSession();

	MapperHelper mapperHelper = new MapperHelper();

	mapperHelper.processConfiguration(sqlSession.getConfiguration());
}
```

#### 6.4 简单分页查询

```java
@Test
public void getAll() {
	EmpDao empDao = sqlSession.getMapper(EmpDao.class);

	Example example = new Example(Emp.class);

	example.createCriteria().andLike("job", "%Cler%");

	PageHelper.startPage(1, 2);

	List<Emp> list = empDao.selectByExample(example);

	PageInfo<Emp> pageInfo = PageInfo.of(list);

	list.forEach(e -> System.out.println(e.getEmpno()));

	System.out.println(pageInfo.getTotal());
}
```