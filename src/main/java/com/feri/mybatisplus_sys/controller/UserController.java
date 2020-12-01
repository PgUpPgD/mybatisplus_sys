package com.feri.mybatisplus_sys.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.additional.query.impl.LambdaQueryChainWrapper;
import com.feri.mybatisplus_sys.entity.User;
import com.feri.mybatisplus_sys.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("user")
public class UserController {
    @Autowired
    private UserMapper mapper;

    /**
     * 1.模糊查询
     * 查询名字中包含'雨'并且年龄小于40
     * where name like '%雨%' and age < 40
     */
    @RequestMapping("test")
    public void test() {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.like("name", "雨").lt("age", 40);
        List<User> userList = mapper.selectList(wrapper);
        userList.forEach(System.out::println);
    }

    /**
     * 2.嵌套查询
     * 查找日期为2019年2月14日并且直属上级姓名为王姓
     * date_format(create_time,'%Y-%m-%d') and manager_id in (select id from user where name like '王%')
     */
    @RequestMapping("test1")
    public void test1(){
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.apply("date_format(create_time,'%Y-%m-%d')={0}","2019-02-05")
                .inSql("id","select id from user where name like '王%'");
        List<User> userList = mapper.selectList(queryWrapper);
        userList.forEach(System.out::println);
    }

    /**
     * and & or
     * 名字为王姓，（年龄小于40或者邮箱不为空）
     */
    @RequestMapping("test2")
    public void test2(){
        List<User> userList = mapper.selectList(Wrappers.<User>query().likeRight("name", "王")
                .and(t -> t.lt("age", 40).or().isNotNull("email")));
        userList.forEach(System.out::println);
    }

    /**
     * between & and
     * 名字为王姓，（年龄小于40，并且年龄大于20，并且邮箱不为空）
     */
    @RequestMapping("test3")
    public void test3(){
        List<User> userList = mapper.selectList(
                Wrappers.<User>query().likeRight("name", "王")
                .and(t -> t.between("age", 20, 40).and(w -> w.isNotNull("email"))));
        userList.forEach(System.out::println);
    }

    /**
     * nested
     * （年龄小于40或者邮箱不为空）并且名字为王姓
     * （age<40 or email is not null）and name like '王%'
     */
    @RequestMapping("test4")
    public void test4(){
        List<User> userList = mapper.selectList(
                Wrappers.<User>query().nested(t -> t.lt("age",40).or().isNotNull("email")).likeRight("name", "王"));
        userList.forEach(System.out::println);
    }

    /**
     * in
     * 年龄为30,31,35,34的员工
     */
    @RequestMapping("test5")
    public void test5(){
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("age", Arrays.asList(30,31,34,35));
        List<User> userList = mapper.selectList(queryWrapper);
        userList.forEach(System.out::println);
    }

    /**
     * 无视优化规则直接拼接到 sql 的最后(有sql注入的风险,请谨慎使用)
     * <p>例: last("limit 1")</p>
     * <p>注意只能调用一次,多次调用以最后一次为准</p>
     *
     * @param condition 执行条件
     * @param lastSql   sql语句
     * @return children
     */
//    Children last(boolean condition, String lastSql);
    /**
     * 只返回满足条件的一条语句即可
     * limit 1
     */
    @RequestMapping("test6")
    public void test6(){
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("age", Arrays.asList(30,31,34,35)).last("limit 1");
        List<User> userList = mapper.selectList(queryWrapper);
        userList.forEach(System.out::println);
    }

    /**
     * 查询指定部分列
     * 查找为王姓的员工的姓名和年龄
     */
    @RequestMapping("test7")
    public void test7(){
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("name", "age").likeRight("name", "王");
        List<User> userList = mapper.selectList(queryWrapper);
        userList.forEach(System.out::println);
    }

    /**
     * 使用过滤器查询指定列
     * 查询所有员工信息除了创建时间和员工ID列
     */
    @RequestMapping("test8")
    public void test8(){
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.select(User.class, info -> !info.getColumn().equals("create_time")
        &&!info.getColumn().equals("manager_id"))
                .likeRight("name", "王");
        List<User> userList = mapper.selectList(queryWrapper);
        userList.forEach(System.out::println);
    }

    //第四种lambda构造器 更加简洁
    @RequestMapping("test9")
    public void test9(){
        List<User> userList = new LambdaQueryChainWrapper<>(mapper).like(User::getName, "雨").ge(User::getAge, 20).list();
        userList.forEach(System.out::println);
    }

    /**
     * 新建config类，在config类中创建`PaginationInterceptor`对象
     * 查询年龄大于20 的用户信息，并以每页容量为两条分页的形式返回。
     *
     * IPage类的构造参数提供了参数的重载,第三个参数为false时，不会查询总记录数。
     *     public Page(long current, long size, boolean isSearchCount) {
     *         this(current, size, 0, isSearchCount);
     *     }
     */
    @RequestMapping("test10")
    public void test10(){
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.ge("age", 20);

        //设置当前页和容量
        Page<User> page = new Page<>(1, 2);
        IPage<User> userIPage = mapper.selectPage(page, queryWrapper);

        System.out.println("总页数："+userIPage.getPages());
        System.out.println("总记录数："+userIPage.getTotal());
        userIPage.getRecords().forEach(System.out::println);
    }

    /**
     * 通过userMapper提供的方法更新用户信息
     */
    @RequestMapping("test11")
    public void test11(){
        User user = new User();
        user.setId(1088250446457389058L);
        user.setEmail("update@email");
        int rows = mapper.updateById(user);
        System.out.println(rows);
    }

    /*
        使用UpdateWrapper更新数据(相当于使用联合主键)
     */
    @RequestMapping("test12")
    public void test12(){
        UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("name", "李艺伟").eq("age",26);
        User user = new User();
        user.setEmail("update2@email");
        int rows = mapper.update(user, updateWrapper);
        System.out.println(rows);
    }

    /*
        当我们更新少量用户信息的时候，可以不用创建对象
        ，直接通过调用set方法更新属性即可。
     */
    @RequestMapping("test13")
    public void test13(){
        UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("name", "李艺伟")
                .eq("age",26).set("email", "update2@email");
        int rows = mapper.update(null, updateWrapper);
        System.out.println(rows);
    }

    /*
        使用lambda更新数据
     */
    @RequestMapping("test14")
    public void test14(){
        LambdaUpdateWrapper<User> lambdaUpdate = Wrappers.lambdaUpdate();
        lambdaUpdate.eq(User::getName, "李艺伟").eq(User::getAge, 26)
                .set(User::getEmail, 27);
        mapper.update(null, lambdaUpdate);
    }

    /*
        删除
        删除方式和update极其类似。
        AR模式（Active Record）
        直接通过实体类完成对数据的增删改查。 实体类继承Model类
        Model类中封装了很多增删改查方法，不用使用UserMapper即可完成对数据的增删改查。
     */
    //  查询所有用户信息
    @RequestMapping("test15")
    public void test15(){
        User user = new User();
        user.selectAll().forEach(System.out::println);
    }

    //逻辑删除
    @RequestMapping("test16")
    public void test16(){
        mapper.deleteById(4566L);
    }

    //新增默认插入时间
    @RequestMapping("test17")
    public void test17(){
        User user = new User();
        user.setName("刘若英");
        user.setEmail("wtf@163.com");
        user.setAge(35);
        user.setId(2345L);
        user.setManagerId(1234L);
        user.setVersion(1);
        mapper.insert(user);
    }

    /**
     * 注意事项：
     * 1. 支持的类型只有：int，Integer,long,Long,Date,Timestamp,LocalDateTime
     * 2. 整数类型下newVerison = oldVersion+1
     * 3. newVersion会写到entity中
     * 4. 仅支持updateById(id)与update(entity,wrapper)方法
     * 5. 在update(entiry,wrapper)方法下，wrapper不能复用
     */
    //更新王天风的年龄
    @RequestMapping("test18")
    public void test18(){
        int version = 1;
        User user = new User();
        user.setEmail("wtf@163.com");
        user.setAge(35);
        user.setId(2345L);
        user.setManagerId(1234L);
        user.setVersion(2);      //先查后set，一致修改并version+1
        mapper.updateById(user);
    }

    //自定义删除的方法
    @RequestMapping("test19")
    public void test19(){
        mapper.deleteAll();
    }



}
