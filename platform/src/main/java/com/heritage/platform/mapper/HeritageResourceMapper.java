package com.heritage.platform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.heritage.platform.entity.HeritageResource;
import org.apache.ibatis.annotations.Mapper;

@Mapper // This annotation marks the interface as a MyBatis mapper
public interface HeritageResourceMapper extends BaseMapper<HeritageResource> {
    // Inheriting BaseMapper provides CRUD methods out of the box; no SQL needed here
}
