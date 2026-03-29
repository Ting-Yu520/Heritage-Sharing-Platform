package com.heritage.platform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.heritage.platform.entity.HeritageResource;
import org.apache.ibatis.annotations.Mapper;

@Mapper // 这个注解告诉 Java 这是一个数据库操作员
public interface HeritageResourceMapper extends BaseMapper<HeritageResource> {
    // 继承了 BaseMapper，它就自动拥有了增删改查的所有超能力！一行 SQL 都不用写！
}