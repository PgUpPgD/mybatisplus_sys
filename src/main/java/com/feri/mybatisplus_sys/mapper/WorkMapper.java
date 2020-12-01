package com.feri.mybatisplus_sys.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.feri.mybatisplus_sys.entity.Work;
import org.apache.ibatis.annotations.Param;

public interface WorkMapper extends BaseMapper<Work> {

    int updateBymoney(@Param("id") int id, @Param("money") int money);
}