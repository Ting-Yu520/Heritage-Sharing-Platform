package com.heritage.platform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.heritage.platform.entity.Category;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CategoryMapper extends BaseMapper<Category> {
    // Inheriting BaseMapper provides insert/selectList/updateById and more out of the box
}
