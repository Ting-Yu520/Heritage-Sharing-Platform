package com.heritage.platform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.heritage.platform.entity.Category;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CategoryMapper extends BaseMapper<Category> {
    // 继承了 BaseMapper 之后，insert、selectList、updateById 等方法就自动拥有了！
}