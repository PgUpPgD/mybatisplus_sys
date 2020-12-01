package com.feri.mybatisplus_sys.config;

import com.baomidou.mybatisplus.core.injector.ISqlInjector;
import com.baomidou.mybatisplus.extension.plugins.OptimisticLockerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import com.baomidou.mybatisplus.extension.plugins.PerformanceInterceptor;
import com.feri.mybatisplus_sys.mapper.custom.MySqlInject;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class MybatisPlusConfig {
    @Bean    //MyBatisPlus物理分页插件
    public PaginationInterceptor paginationInterceptor(){
        return new PaginationInterceptor();
    }

    @Bean    //配置类中注入乐观锁插件
    public OptimisticLockerInterceptor optimisticLockerInterceptor(){
        return new OptimisticLockerInterceptor();
    }

    @Bean    //逻辑删除
    public ISqlInjector sqlInjector() {
        return new MySqlInject();
    }

    @Bean
//    @Profile({"dev,test"})
    public PerformanceInterceptor performanceInterceptor() {
        PerformanceInterceptor performanceInterceptor = new PerformanceInterceptor();
        // 格式化sql输出
        performanceInterceptor.setFormat(true);
        // 设置sql执行最大时间，单位（ms）
        performanceInterceptor.setMaxTime(500L);

        return performanceInterceptor;
    }
}
