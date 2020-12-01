package com.feri.mybatisplus_sys.mapper.custom;

import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlSource;

/**
 * SQL注入器 ->_-> 封装自定义通用SQL
 *
 * 实现步骤：
 *
 * 1. 创建定义方法的类
 * 2. 创建注入器
 * 3. 在mapper中加入自定义方法
 *
 * eg: 编写一个删除表所有数据的方法
 *
 * 1. 创建定义方法的类
 */
public class DeleteAllMethod extends AbstractMethod {
    @Override
    public MappedStatement injectMappedStatement(Class<?> mapperClass, Class<?> modelClass, TableInfo tableInfo) {
        //执行的sql
        String sql = "delete from " + tableInfo.getTableName();
        //mapper接口的方法名
        String method = "deleteAll";
        SqlSource sqlSource = languageDriver.createSqlSource(configuration, sql, mapperClass);

        return addDeleteMappedStatement(mapperClass, method, sqlSource);
    }
}
