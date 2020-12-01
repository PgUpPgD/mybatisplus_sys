# 						MyBatisPlus

## SpringBoot第一个简单应用

1. 数据库建表

```php
#创建用户表
CREATE TABLE user (
    id BIGINT(20) PRIMARY KEY NOT NULL COMMENT '主键',
    name VARCHAR(30) DEFAULT NULL COMMENT '姓名',
    age INT(11) DEFAULT NULL COMMENT '年龄',
    email VARCHAR(50) DEFAULT NULL COMMENT '邮箱',
    manager_id BIGINT(20) DEFAULT NULL COMMENT '直属上级id',
    create_time DATETIME DEFAULT NULL COMMENT '创建时间',
    CONSTRAINT manager_fk FOREIGN KEY (manager_id)
        REFERENCES user (id)
)  ENGINE=INNODB CHARSET=UTF8;

#初始化数据：
INSERT INTO user (id, name, age, email, manager_id
    , create_time)
VALUES (1087982257332887553, '大boss', 40, 'boss@baomidou.com', NULL
        , '2019-01-11 14:20:20'),
    (1088248166370832385, '王天风', 25, 'wtf@baomidou.com', 1087982257332887553
        , '2019-02-05 11:12:22'),
    (1088250446457389058, '李艺伟', 28, 'lyw@baomidou.com', 1088248166370832385
        , '2019-02-14 08:31:16'),
    (1094590409767661570, '张雨琪', 31, 'zjq@baomidou.com', 1088248166370832385
        , '2019-01-14 09:15:15'),
    (1094592041087729666, '刘红雨', 32, 'lhm@baomidou.com', 1088248166370832385
        , '2019-01-14 09:48:16');
```

1. 依赖

```xml
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-jdbc</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-configuration-processor</artifactId>
            <optional>true</optional>
        </dependency>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>

        <dependency>
            <groupId>com.baomidou</groupId>
            <artifactId>mybatis-plus-boot-starter</artifactId>
            <version>3.1.2</version>
        </dependency>
```

1. springboot配置文件

```ruby
spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    username: root
    password: root
    url: jdbc:mysql://localhost:3306/test?serverTimezone=CTT&useUnicode=true&characterEncoding=utf-8&allowMultiQueries=true
logging:
  level:
    root: warn
    org.ywb.demo.dao: trace
  pattern:
    console: '%p%m%n'
```

1. 在pojo包中新建和数据库user表映射的类

```tsx
@Data
public class User {
    private Long id;
    private String name;
    private Integer age;
    private String email;
    private String managerId;
    private LocalDateTime createTime;
}
```

1. 在dao包中创建mapper接口，并集成mybatisPlus的BaseMapper

```java
public interface UserMapper extends BaseMapper<User> {

}
```

1. 在springboot启动类添加`@MapperScan`扫描dao层接口

```java
@MapperScan("org.ywb.demo.dao")
@SpringBootApplication
public class MybatisPlusDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(MybatisPlusDemoApplication.class, args);
    }

}
```

8.编写测试类

```kotlin
@RunWith(SpringRunner.class)
@SpringBootTest
public class MybatisPlusDemoApplicationTests {

    @Resource
    private UserMapper userMapper;
    
    @Test
    public void select(){
        List<User> users = userMapper.selectList(null);
        users.forEach(System.out::println);
    }

}
```

## 常用注解

MyBatisPlus提供了一些注解供我们在实体类和表信息出现不对应的时候使用。通过使用注解完成逻辑上匹配。

|   注解名称    |                  说明                  |
| :-----------: | :------------------------------------: |
| `@TableName`  |     实体类的类名和数据库表名不一致     |
|  `@TableId`   |  实体类的主键名称和表中主键名称不一致  |
| `@TableField` | 实体类中的成员名称和表中字段名称不一致 |

```kotlin
@Data
@TableName("t_user")
public class User {
    @TableId("user_id")
    private Long id;
    @TableField("real_name")
    private String name;
    private Integer age;
    private String email;
    private Long managerId;
    private LocalDateTime createTime;
}
```

## 排除实体类中非表字段

1. 使用`transient`关键字修饰非表字段，但是被`transient`修饰后，无法进行序列化。
2. 使用`static`关键字，因为我们使用的是lombok框架生成的get/set方法，所以对于静态变量，我们需要手动生成get/set方法。
3. 使用`@TableField(exist = false)`注解

## CURD

BaseMapper中封装了很多关于增删该查的方法，后期自动生成，我们直接调用接口中的相关方法即可完成相应的操作。
`BaseMapper`部分代码

```dart
public interface BaseMapper<T> extends Mapper<T> {

    int insert(T entity);
   
    int deleteById(Serializable id);

    int deleteByMap(@Param(Constants.COLUMN_MAP) Map<String, Object> columnMap);

    int delete(@Param(Constants.WRAPPER) Wrapper<T> wrapper);

    int deleteBatchIds(@Param(Constants.COLLECTION) Collection<? extends Serializable> idList);

    int updateById(@Param(Constants.ENTITY) T entity);

...
}
```

![](F:\001\笔记本2\图片\mybatisplus.png)

通过观察类图可知，我们需要这些功能时，只需要创建`QueryWrapper`对象即可。

1. 模糊查询

```dart
/**
     * 查询名字中包含'雨'并且年龄小于40
     * where name like '%雨%' and age < 40
     */
    @Test
    public void selectByWrapper(){
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.like("name","雨").lt("age",40);
        List<User> userList = userMapper.selectList(queryWrapper);
        userList.forEach(System.out::println);
    }
```

2.嵌套查询

```dart
    /**
     * 创建日期为2019年2月14日并且直属上级姓名为王姓
     * date_format(create_time,'%Y-%m-%d') and manager_id in (select id from user where name like '王%')
     */
    @Test
    public void selectByWrapper2(){
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.apply("date_format(create_time,'%Y-%m-%d')={0}","2019-02-05")
                .inSql("id","select id from user where name like '王%'");
        List<User> userList = userMapper.selectList(queryWrapper);
        userList.forEach(System.out::println);
    }
```

注意

 上面的日期查询使用的是占位符的形式进行查询，目的就是为了防止SQL注入的风险。

 apply方法的源码

```java
   /**
     * 拼接 sql
     * <p>!! 会有 sql 注入风险 !!</p>
     * <p>例1: apply("id = 1")</p>
     * <p>例2: apply("date_format(dateColumn,'%Y-%m-%d') = '2008-08-08'")</p>
     * <p>例3: apply("date_format(dateColumn,'%Y-%m-%d') = {0}", LocalDate.now())</p>
     *
     * @param condition 执行条件
     * @return children
     */
    Children apply(boolean condition, String applySql, Object... value);
```

SQL 注入的例子：

> ```bash
> queryWrapper.apply("date_format(create_time,'%Y-%m-%d')=2019-02-14 or true=true")
>               .inSql("manager_id","select id from user where name like '王%'");
> ```

1. and & or

```php
  /**
     * 名字为王姓，（年龄小于40或者邮箱不为空）
     */
    @Test
    public void selectByWrapper3(){
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.likeRight("name","王").and(wq-> wq.lt("age",40).or().isNotNull("email"));

        List<User> userList = userMapper.selectList(queryWrapper);
        userList.forEach(System.out::println);

    }
```

1. between & and

```php
   /**
     * 名字为王姓，（年龄小于40，并且年龄大于20，并且邮箱不为空）
     */
    @Test
    public void selectWrapper4(){
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.likeRight("name", "王").and(wq -> wq.between("age", 20, 40).and(wqq -> wqq.isNotNull("email")));
        List<User> userList = userMapper.selectList(queryWrapper);
        userList.forEach(System.out::println);
    }
```

1. nested

```php
   /**
     * （年龄小于40或者邮箱不为空）并且名字为王姓
     * （age<40 or email is not null）and name like '王%'
     */
    @Test
    public void selectWrapper5(){
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();

        queryWrapper.nested(wq->wq.lt("age",40).or().isNotNull("email")).likeRight("name","王");

        List<User> userList = userMapper.selectList(queryWrapper);
        userList.forEach(System.out::println);
    }
```

1. in

```php
   /**
     * 年龄为30,31,35,34的员工
     */
    @Test
    public void selectWrapper6(){
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();

        queryWrapper.in("age", Arrays.asList(30,31,34,35));

        List<User> userList = userMapper.selectList(queryWrapper);
        userList.forEach(System.out::println);
    }
```

1. last 有SQL注入的风险！！！

```dart
  /**
     * 无视优化规则直接拼接到 sql 的最后(有sql注入的风险,请谨慎使用)
     * <p>例: last("limit 1")</p>
     * <p>注意只能调用一次,多次调用以最后一次为准</p>
     *
     * @param condition 执行条件
     * @param lastSql   sql语句
     * @return children
     */
    Children last(boolean condition, String lastSql);
   /**
     * 只返回满足条件的一条语句即可
     * limit 1
     */
    @Test
    public void selectWrapper7(){
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();

        queryWrapper.in("age", Arrays.asList(30,31,34,35)).last("limit 1");

        List<User> userList = userMapper.selectList(queryWrapper);
        userList.forEach(System.out::println);
    }
```

1. 查询指定部分列

```php
    /**
     * 查找为王姓的员工的姓名和年龄
     */
    @Test
    public void selectWrapper8(){
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("name","age").likeRight("name","王");
        List<User> userList = userMapper.selectList(queryWrapper);
        userList.forEach(System.out::println);
    }
```

1. 使用过滤器查询指定列

```kotlin
   /**
     * 查询所有员工信息除了创建时间和员工ID列
     */
    @Test
    public void selectWrapper9(){
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.select(User.class,info->!info.getColumn().equals("create_time")
                &&!info.getColumn().equals("manager_id"));
        List<User> userList = userMapper.selectList(queryWrapper);
        userList.forEach(System.out::println);
    }
```

### condition 的作用

在我们调用的查询语句中，通过查看源码(这里以`apply`方法为例)可以看出，每个查询方法的第一个参数都是boolean类型的参数，重载方法中默认给我们传入的都是true。

```csharp
 default Children apply(String applySql, Object... value) {
        return apply(true, applySql, value);
    }
    Children apply(boolean condition, String applySql, Object... value);
```

这个condition的作用是为true时，执行其中的SQL条件，为false时，忽略设置的SQL条件。

### 实体作为条件构造方法的参数

在web开发中，controller层常常会传递给我们一个用户的对象，比如通过用户姓名和用户年龄查询用户列表。
 我们可以将传递过来的对象直接以构造参数的形式传递给`QueryWrapper`，MyBatisPlus会自动根据实体对象中的属性自动构建相应查询的SQL语句。

```java
 @Test
    public void selectWrapper10(){
        User user = new User();
        user.setName("刘红雨");
        user.setAge(32);
        QueryWrapper<User> queryWrapper = new QueryWrapper<>(user);
        List<User> userList = userMapper.selectList(queryWrapper);
        userList.forEach(System.out::println);
    }
```

如果想通过对象中某些属性进行模糊查询，我们可以在跟数据库表对应的实体类中相应的属性标注注解即可。
比如我们想通过姓名进行模糊查询用户列表。

```java
@TableField(condition = SqlCondition.LIKE)
    private String name;
 @Test
    public void selectWrapper10(){
        User user = new User();
        user.setName("红");
        user.setAge(32);
        QueryWrapper<User> queryWrapper = new QueryWrapper<>(user);
        List<User> userList = userMapper.selectList(queryWrapper);
        userList.forEach(System.out::println);
    }
```

### Lambda条件构造器

MybatisPlus提供了4种方式创建lambda条件构造器，前三种分别是这样的

```java
        LambdaQueryWrapper<User> lambdaQueryWrapper = new QueryWrapper<User>().lambda();
        LambdaQueryWrapper<User> lambdaQueryWrapper1 = new LambdaQueryWrapper<>();
        LambdaQueryWrapper<User> lambdaQueryWrapper2 = Wrappers.lambdaQuery();
```

1. 查询名字中包含‘雨’并且年龄小于40的员工信息

```java
    @Test
    public void lambdaSelect(){
        LambdaQueryWrapper<User> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.like(User::getName,"雨").lt(User::getAge,40);

        List<User> userList = userMapper.selectList(lambdaQueryWrapper);
        userList.forEach(System.out::println);
    }
```

QueryWrapper类已经提供了很强大的功能，而lambda条件构造器做的和QueryWrapper的事也是相同的为什么要冗余的存在lambda条件构造器呢？
 QueryWrapper是通过自己写表中相应的属性进行构造where条件的，容易发生拼写错误，在编译时不会报错，只有运行时才会报错，而lambda条件构造器是通过调用实体类中的方法，如果方法名称写错，直接进行报错，所以lambda的纠错功能比QueryWrapper要提前很多。
 举个例子：
 查找姓名中包含“雨”字的员工信息。
 使用QueryWrapper

```bash
queryWrapper.like("name","雨");
```

使用lambda

```css
lambdaQueryWrapper.like(User::getName,"雨");
```

如果在拼写name的时候不小心，写成了naem,程序并不会报错，但是如果把方法名写成了getNaem程序立即报错。

第四种lambda构造器
 细心的人都会发现无论是之前的lambda构造器还是queryWrapper，每次编写完条件构造语句后都要将对象传递给mapper 的selectList方法，比较麻烦，MyBatisPlus提供了第四种函数式编程方式，不用每次都传。

1. 查询名字中包含“雨”字的，并且年龄大于20的员工信息

```java
    @Test
    public void lambdaSelect(){
        List<User> userList = new LambdaQueryChainWrapper<>(userMapper).like(User::getName, "雨").ge(User::getAge, 20).list();
        userList.forEach(System.out::println);
    }
```

## 自定义SQL

1. 在resources资源文件夹下新建mapper文件夹，并将mapper文件夹的路径配置到配置文件中

   ![](F:\001\笔记本2\图片\mybatisPlus自定义sql.png)

```yml
mybatis-plus:
  mapper-locations: mapper/*.xml
```

1. 在mapper 文件夹中新建UserMapper.xml。
2. 像mybatis那样在UseMapper接口中写接口，在UserMapper接口中写SQL即可。
    UserMapper

```java
public interface UserMapper extends BaseMapper<User> {

    /**
     * 查询所有用户信息
     * @return list
     */
    List<User> selectAll();
}
```

UserMapper.xml

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="org.ywb.demo.dao.UserMapper">

    <select id="selectAll" resultType="org.ywb.demo.pojo.User">
        select * from user
    </select>
</mapper>
```

## 分页查询

MyBatis分页提供的是逻辑分页，每次将所有数据查询出来，存储到内存中，然后根据页容量，逐页返回。如果表很大，无疑是一种灾难！
 **MyBatisPlus物理分页插件**

1. 新建config类，在config类中创建`PaginationInterceptor`对象

```java
@Configuration
public class MybatisPlusConfig {

    @Bean
    public PaginationInterceptor paginationInterceptor(){
        return new PaginationInterceptor();
    }
}
```

1. 测试：查询年龄大于20 的用户信息，并以每页容量为两条分页的形式返回。

```java
 @Test
    public void selectPage(){
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.ge("age",20);

        //设置当前页和页容量
        Page<User> page = new Page<>(1, 2);

        IPage<User> userIPage = userMapper.selectPage(page, queryWrapper);

        System.out.println("总页数："+userIPage.getPages());
        System.out.println("总记录数："+userIPage.getTotal());
        userIPage.getRecords().forEach(System.out::println);
    }
```

 

1. 测试：不查询总记录数，分页查询
    IPage类的构造参数提供了参数的重载,第三个参数为false时，不会查询总记录数。

```java
public Page(long current, long size, boolean isSearchCount) {
        this(current, size, 0, isSearchCount);
}
~~·
## 更新
1. 通过userMapper提供的方法更新用户信息
~~~java
    @Test
    public void updateTest1(){
        User user = new User();
        user.setId(1088250446457389058L);
        user.setEmail("update@email");
        int rows = userMapper.updateById(user);
        System.out.println(rows);
    }
```

1. 使用UpdateWrapper更新数据(相当于使用联合主键)

```java
    @Test
    public void updateTest2(){
        UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("name","李艺伟").eq("age",26);

        User user = new User();
        user.setEmail("update2@email");
        int rows = userMapper.update(user, updateWrapper);
        System.out.println(rows);
    }
```

1. 当我们更新少量用户信息的时候，可以不用创建对象，直接通过调用set方法更新属性即可。

```java
    @Test
    public void updateTest3(){
        UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("name","李艺伟").eq("age",26).set("email","update3@email.com");
        userMapper.update(null,updateWrapper);
    }
```

1. 使用lambda更新数据

```java
    @Test
    public void updateByLambda(){
        LambdaUpdateWrapper<User> lambdaUpdateWrapper = Wrappers.lambdaUpdate();
        lambdaUpdateWrapper.eq(User::getName,"李艺伟").eq(User::getAge,26).set(User::getAge,27);
        userMapper.update(null,lambdaUpdateWrapper);
    }
```

## 删除

删除方式和update极其类似。

## AR模式（Active Record）

直接通过实体类完成对数据的增删改查。

1. 实体类继承Model类

```java
@Data
@EqualsAndHashCode(callSuper = false)
public class User extends Model<User> {
    private Long id;
    @TableField(condition = SqlCondition.LIKE)
    private String name;
    private Integer age;
    private String email;
    private Long managerId;
    private LocalDateTime createTime;
}
```

Model类中封装了很多增删改查方法，不用使用UserMapper即可完成对数据的增删改查。

1. 查询所有用户信息

```java
    @Test
    public void test(){
        User user = new User();
        user.selectAll().forEach(System.out::println);
    }
```

## 主键策略

MyBatisPlus的主键策略封装在`IdType`枚举类中。

```java
@Getter
public enum IdType {
    /**
     * 数据库ID自增
     */
    AUTO(0),
    /**
     * 该类型为未设置主键类型(将跟随全局)
     */
    NONE(1),
    /**
     * 用户输入ID
     * <p>该类型可以通过自己注册自动填充插件进行填充</p>
     */
    INPUT(2),

    /* 以下3种类型、只有当插入对象ID 为空，才自动填充。 */
    /**
     * 全局唯一ID (idWorker)
     */
    ID_WORKER(3),
    /**
     * 全局唯一ID (UUID)
     */
    UUID(4),
    /**
     * 字符串全局唯一ID (idWorker 的字符串表示)
     */
    ID_WORKER_STR(5);

    private final int key;

    IdType(int key) {
        this.key = key;
    }
}
```

在实体类中对应数据库中的主键id属性上标注注解`TableId(type='xxx')`即可完成主键配置。

```kotlin
    @TableId(type = IdType.AUTO)
    private Long id;
```

这种配置方式的主键策略只能在该表中生效，但是其他表还需要进行配置，为了避免冗余，麻烦，MybatisPlus提供了全局配置，在配置文件中配置主键策略即可实现。

```python
mybatis-plus:
  mapper-locations: mapper/*.xml
  global-config:
    db-config:
      id-type: auto
```

如果全局策略和局部策略全都设置，局部策略优先。

## 基本配置

[MyBatisPlus官方文档](https://links.jianshu.com/go?to=https%3A%2F%2Fbaomidou.gitee.io%2Fmybatis-plus-doc%2F%23%2Fapi%3Fid%3Dglobalconfiguration)

```bash
mybatis-plus:
  mapper-locations: mapper/*.xml
  global-config:
    db-config:
      # 主键策略
      id-type: auto
      # 表名前缀
      table-prefix: t
      # 表名是否使用下划线间隔，默认：是
      table-underline: true
  # 添加mybatis配置文件路径
  config-location: mybatis-config.xml
  # 配置实体类包地址
  type-aliases-package: org.ywb.demo.pojo
  # 驼峰转下划线
  configuration:
    map-underscore-to-camel-case: true
```

#### 进阶

```php
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `id` bigint(20) NULL DEFAULT NULL COMMENT '主键',
  `name` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '姓名',
  `age` int(11) NULL DEFAULT NULL COMMENT '年龄',
  `email` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '邮箱',
  `manager_id` bigint(20) NULL DEFAULT NULL COMMENT '直属上级id',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '修改时间',
  `version` int(11) NULL DEFAULT 1 COMMENT '版本',
  `deleted` int(1) NULL DEFAULT 0 COMMENT '逻辑删除标识（0，未删除；1，已删除）'
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;
INSERT INTO `user` VALUES (1234, '大boss', 40, 'boss@163.com', NULL, '2019-10-02 10:08:02', '2019-10-02 10:08:05', 1, 0);
INSERT INTO `user` VALUES (2345, '王天风', 25, 'wtf@163.com', 1234, '2019-10-02 10:09:07', '2019-10-02 10:09:10', 1, 0);
INSERT INTO `user` VALUES (2346, '李艺伟', 28, 'lyw@163.com', 2345, '2019-10-02 10:10:09', '2019-10-02 10:10:12', 1, 0);
INSERT INTO `user` VALUES (3456, '张雨绮', 31, 'zyq@163.com', 2345, '2019-10-02 10:10:54', '2019-10-02 10:10:58', 1, 0);
INSERT INTO `user` VALUES (4566, '刘雨红', 32, 'lyh@163.com', 2345, '2019-10-02 10:11:51', '2019-10-02 10:11:55', 1, 0);
SET FOREIGN_KEY_CHECKS = 1;
```

#### 逻辑删除

1. 设定逻辑删除规则
    在配置文件中配置逻辑删除和逻辑未删除的值

```csharp
global-config:
    db-config:
      logic-not-delete-value: 0
      logic-delete-value: 1 
```

1. 在pojo类中在逻辑删除的字段加注解`@TableLogic` 

```kotlin
@Data
@EqualsAndHashCode(callSuper = false)
public class User extends Model<User> {
    @TableId(type = IdType.AUTO)
    private Long id;
    @TableField(condition = SqlCondition.LIKE)
    private String name;
    private Integer age;
    private String email;
    private Long managerId;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Integer version;
    @TableLogic
    private Integer deleted;
}
```

1. 通过id逻辑删除

```java
    @Test
    public void deleteById(){
        userMapper.deleteById(4566L);
    }

	@Bean    //逻辑删除
    public ISqlInjector sqlInjector() {
        return new LogicSqlInjector();
    }

```

1. 查询中排除删除标识字段及注意事项
    逻辑删除字段只是为了标识数据是否被逻辑删除，在查询的时候，并不想也将该字段查询出来。
    我们只需要在delete字段上增加`@TableField(select = false)`mybatisplus在查询的时候就会自动忽略该字段。

```java
    @Test
    public void selectIgnoreDeleteTest(){
        userMapper.selectById(3456L);
    }
```

自定义sql,MybatisPlus不会忽略deleted属性，需要我们手动忽略

#### 自动填充

MybaitsPlus在我们插入数据或者更新数据的时候，为我们提供了自动填充功能。类似MySQL提供的默认值一样。
 如果我们需要使用自动填充功能，我们需要在实体类的相应属性上加`@TableField`注解，并指定什么时候进行自动填充。mybatisPlus为我们提供了三种填充时机，在`FieldFill`枚举中

```swift
public enum FieldFill {
    /**
     * 默认不处理
     */
    DEFAULT,
    /**
     * 插入时填充字段
     */
    INSERT,
    /**
     * 更新时填充字段
     */
    UPDATE,
    /**
     * 插入和更新时填充字段
     */
    INSERT_UPDATE
}
```

设置好之后，我们还需要编写具体的填充规则，具体是编写一个填充类并交给Spring管理，然后实现`MetaObjectHandler`接口中的`insertFill`和`updateFill`方法。

1. 插入User对象的时候自动填充插入时间，更新User对象的时候自动填充更新时间。

- 指定实体类中需要自动填充的字段，并设置填充时机

```java
@Data
@EqualsAndHashCode(callSuper = false)
public class User extends Model<User> {
    ...
    @TableField(fill = INSERT)
    private LocalDateTime createTime;
    @TableField(fill = UPDATE)
    private LocalDateTime updateTime;
    ...
}
```

- 编写填充规则

```java
@Component
public class MyMetaObjHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        if(metaObject.hasSetter("createTime")){
            setInsertFieldValByName("createTime", LocalDateTime.now(),metaObject);
        }
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        if(metaObject.hasSetter("updateTime")){
            setUpdateFieldValByName("updateTime",LocalDateTime.now(),metaObject);
        }
    }
}
```

为什么要用if判断是否有对应的属性
 mybatisPlus在执行插入或者更新操作的时候，每次都会执行该方法，有些表中是没有设置自动填充字段的，而且有些自动填充字段的值的获取比较消耗系统性能，所以为了不必要的消耗，进行if判断，决定是否需要填充。

有些时候我们已经设置了属性的值。不想让mybatisPlus再自动填充，也就是说我们没有设置属性的值，mybatisPlus进行填充，如果设置了那么就用我们设置的值。这种情况我们只需要在填充类中提前获取默认值，然后使用该默认值就可以了。

```java
    @Override
    public void updateFill(MetaObject metaObject) {
        if(metaObject.hasSetter("updateTime")){
            Object updateTime = getFieldValByName("updateTime", metaObject);
            if(Objects.nonNull(updateTime)){
                setUpdateFieldValByName("updateTime",updateTime,metaObject);
            }else{
                setUpdateFieldValByName("updateTime",LocalDateTime.now(),metaObject);
            }
        }
    }
```

#### 乐观锁

乐观锁适用于读多写少的情况，更新数据的时候不使用“锁“而是使用版本号来判断是否可以更新数据。通过不加锁来减小数据更新时间和系统的性能消耗，进而提高数据库的吞吐量。CAS机制就是一种典型的乐观锁的形式。
 乐观锁是逻辑存在的一种概念，我们如果使用乐观锁需要手动在表的加上version字段。

1. mysql使用乐观锁伪代码示例：

```bash
update user 
set balabala....
where balabala... and version = xxx
```

##### 乐观锁

1.配置类中注入乐观锁插件

```java
    @Bean
    public OptimisticLockerInterceptor optimisticLockerInterceptor(){
        return new OptimisticLockerInterceptor();
    }
```

1. 实体类中的版本字段增加`@version`注解

```java
@Data
@EqualsAndHashCode(callSuper = false)
public class User extends Model<User> {
    ...
    @Version
    private Integer version;
    ...
}
```

1. test
    更新王天风的年龄

```java
    @Test
    public void testLock(){
        int version = 1;
        User user = new User();
        user.setEmail("wtf@163.com");
        user.setAge(34);
        user.setId(2345L);
        user.setManagerId(1234L);
        user.setVersion(1);
        userMapper.updateById(user);

    }
```

##### 注意事项：

1. 支持的类型只有：int，Integer,long,Long,Date,Timestamp,LocalDateTime
2. 整数类型下newVerison = oldVersion+1
3. newVersion会写到entity中
4. 仅支持updateById(id)与update(entity,wrapper)方法
5. 在update(entiry,wrapper)方法下，wrapper不能复用

#### 性能分析

1. 配置类中注入性能分析插件

```java
    @Bean
   // @Profile({"dev,test"})
    public PerformanceInterceptor performanceInterceptor() {
        PerformanceInterceptor performanceInterceptor = new PerformanceInterceptor();
        // 格式化sql输出
        performanceInterceptor.setFormat(true);
        // 设置sql执行最大时间，单位（ms）
        performanceInterceptor.setMaxTime(5L);

        return performanceInterceptor;
    }
```

执行sql就可以打印sql执行的信息了

##### 依靠第三方插件美化sql输出

[https://mp.baomidou.com/guide/p6spy.html](https://links.jianshu.com/go?to=https%3A%2F%2Fmp.baomidou.com%2Fguide%2Fp6spy.html)

1. 第三方依赖

```xml
        <dependency>
            <groupId>p6spy</groupId>
            <artifactId>p6spy</artifactId>
            <version>3.8.5</version>
        </dependency>
```

1. 更改配置文件中的dirver和url

```kotlin
spring:
  datasource:
#    driver-class-name: com.mysql.cj.jdbc.Driver
    username: root
    password: root
#    url: jdbc:mysql://localhost:3306/test?serverTimezone=CTT&useUnicode=true&characterEncoding=utf-8&allowMultiQueries=true
    driver-class-name: com.p6spy.engine.spy.P6SpyDriver
    url: jdbc:p6spy:mysql://localhost:3306/test?serverTimezone=CTT&useUnicode=true&characterEncoding=utf-8&allowMultiQueries=true
```

1. 增加spy.properties配置文件

```csharp
module.log=com.p6spy.engine.logging.P6LogFactory,com.p6spy.engine.outage.P6OutageFactory
# 自定义日志打印
logMessageFormat=com.baomidou.mybatisplus.extension.p6spy.P6SpyLogger
#日志输出到控制台
appender=com.baomidou.mybatisplus.extension.p6spy.StdoutLogger
# 使用日志系统记录 sql
#appender=com.p6spy.engine.spy.appender.Slf4JLogger
# 设置 p6spy driver 代理
deregisterdrivers=true
# 取消JDBC URL前缀
useprefix=true
# 配置记录 Log 例外,可去掉的结果集有error,info,batch,debug,statement,commit,rollback,result,resultset.
excludecategories=info,debug,result,batch,resultset
# 日期格式
dateformat=yyyy-MM-dd HH:mm:ss
# 实际驱动可多个
#driverlist=org.h2.Driver
# 是否开启慢SQL记录
outagedetection=true
# 慢SQL记录标准 2 秒
outagedetectioninterval=2
```

注意！

- driver-class-name 为 p6spy 提供的驱动类
- url 前缀为 jdbc:p6spy 跟着冒号为对应数据库连接地址
- 打印出sql为null,在excludecategories增加commit
- 批量操作不打印sql,去除excludecategories中的batch
- 批量操作打印重复的问题请使用MybatisPlusLogFactory (3.2.1新增）
- 该插件有性能损耗，不建议生产环境使用。

test

##### 注意

开启性能分析会消耗系统的性能，所以性能分析插件要配合`@Profile`注解执行使用的环境。

#### SQL注入器 ->_-> 封装自定义通用SQL

实现步骤：

1. 创建定义方法的类
2. 创建注入器
3. 在mapper中加入自定义方法

eg: 编写一个删除表所有数据的方法

1. 创建定义方法的类

```java
public class DeleteAllMethod extends AbstractMethod {
    @Override
    public MappedStatement injectMappedStatement(Class<?> mapperClass, Class<?> modelClass, TableInfo tableInfo) {
        // 执行的sql
        String sql = "delete from " + tableInfo.getTableName();
        // mapper接口方法名
        String method = "deleteAll";
        SqlSource sqlSource = languageDriver.createSqlSource(configuration, sql, mapperClass);
        return addDeleteMappedStatement(mapperClass, method, sqlSource);
    }
}
```

1. 创建注入器。添加自己的方法

```java
@Component
public class MySqlInject extends DefaultSqlInjector {
    @Override
    public List<AbstractMethod> getMethodList(Class<?> mapperClass) {
        List<AbstractMethod> methodList = super.getMethodList(mapperClass);
        methodList.add(new DeleteAllMethod());
        return methodList;
    }
}
```

1. 在mapper中加入自定义方法

```java
public interface UserMapper extends BaseMapper<User> {

    /**
     * 删除所有表数据
     *
     * @return 影响行数
     */
    int deleteAll();

}
```

1. test

```java
    @Test
    public void deleteAll(){
        userMapper.deleteAll();
    }
```









