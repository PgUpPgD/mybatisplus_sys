package com.feri.mybatisplus_sys.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.feri.mybatisplus_sys.entity.User;
import org.springframework.stereotype.Repository;

@Repository
public interface UserMapper extends BaseMapper<User> {
    /**
     * 删除所有表数据
     */
    int deleteAll();
}
