package com.feri.mybatisplus_sys.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

import static com.baomidou.mybatisplus.annotation.FieldFill.*;

/**
 * @TableField(exist = false)`注解 可排除非数据库字段
 */
@Data
@TableName("user")
@EqualsAndHashCode(callSuper = false)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class User extends Model<User> {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    @TableField(condition = SqlCondition.LIKE)
    private String name;
    private Integer age;
    private String email;
    private Long managerId;
    @TableField(fill = INSERT)  //插入User对象的时候自动填充插入时间  MyMetaObjHandler
    private LocalDateTime createTime;
    @TableField(fill = UPDATE)  //更新User对象的时候自动填充更新时间  MyMetaObjHandler
    private LocalDateTime updateTime;
    @Version                    //乐观锁注解  MybatisPlusConfig
    private Integer version;
    @TableLogic                 //逻辑删除的注解  MybatisPlusConfig
    @TableField(select = false) //mybatisplus在查询的时候就会自动忽略该字段。自定义查询不行
    private Integer deleted;
}
