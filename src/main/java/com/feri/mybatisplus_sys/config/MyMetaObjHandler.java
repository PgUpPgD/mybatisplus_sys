package com.feri.mybatisplus_sys.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Objects;

@Component
public class MyMetaObjHandler implements MetaObjectHandler {
    @Override
    public void insertFill(MetaObject metaObject) {
        if (metaObject.hasSetter("createTime")){
            setInsertFieldValByName("createTime",
                    LocalDateTime.now(),metaObject);
        }
    }

    /**
     * 有些时候我们已经设置了属性的值。不想让mybatisPlus再自动填充，
     * 也就是说我们没有设置属性的值，mybatisPlus进行填充，如果设置了那么就用我们设置的值。
     * 这种情况我们只需要在填充类中提前获取默认值，然后使用该默认值就可以了。
     */
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

}
