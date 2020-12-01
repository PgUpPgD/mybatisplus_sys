package com.feri.mybatisplus_sys.mapper.custom;

import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.extension.injector.LogicSqlInjector;

import java.util.List;

//创建注入器。添加自己的方法
public class MySqlInject extends LogicSqlInjector {

    @Override
    public List<AbstractMethod> getMethodList() {
        List<AbstractMethod> methodList = super.getMethodList();
        methodList.add(new DeleteAllMethod());
        return methodList;
    }

}
