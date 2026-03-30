package com.heritage.platform.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.heritage.platform.entity.Category;
import com.heritage.platform.entity.HeritageResource;
import com.heritage.platform.mapper.CategoryMapper;
import com.heritage.platform.mapper.HeritageResourceMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@CrossOrigin
@RestController
public class CategoryController {

    @Autowired private CategoryMapper categoryMapper;
    @Autowired private HeritageResourceMapper resourceMapper;

    /**
     * ✨ PBI 1 & 5: 获取分类列表（包含关键字搜索、状态过滤、以及资源使用量统计）
     */
    @GetMapping("/api/admin/categories")
    public List<Map<String, Object>> getCategories(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String filterStatus) {

        // 1. 关键字查询
        QueryWrapper<Category> query = new QueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            query.like("name", keyword).or().like("description", keyword);
        }
        query.orderByDesc("created_at");
        List<Category> categories = categoryMapper.selectList(query);

        List<Map<String, Object>> result = new ArrayList<>();

        // 2. 统计每个分类的 Usage Count
        for (Category c : categories) {
            long usageCount = resourceMapper.selectCount(new QueryWrapper<HeritageResource>().eq("category", c.getName()));

            // 3. 应用使用状态过滤器 (PBI 5)
            if ("UNUSED".equals(filterStatus) && usageCount > 0) continue;
            if ("IN_USE".equals(filterStatus) && usageCount == 0) continue;

            Map<String, Object> map = new HashMap<>();
            map.put("id", c.getId());
            map.put("name", c.getName());
            map.put("description", c.getDescription());
            map.put("createdAt", c.getCreatedAt());
            map.put("usageCount", usageCount);
            result.add(map);
        }
        return result;
    }

    /**
     * ✨ PBI 2 & 3: 新增或修改分类
     */
    @PostMapping("/api/admin/categories")
    public Map<String, Object> saveCategory(@RequestBody Category category) {
        Map<String, Object> res = new HashMap<>();

        // 唯一性校验
        QueryWrapper<Category> dupQuery = new QueryWrapper<Category>().eq("name", category.getName());
        if (category.getId() != null) dupQuery.ne("id", category.getId());
        if (categoryMapper.selectCount(dupQuery) > 0) {
            res.put("success", false); res.put("message", "分类名称已存在，请更换！"); return res;
        }

        category.setUpdatedAt(LocalDateTime.now());
        if (category.getId() == null) {
            category.setCreatedAt(LocalDateTime.now());
            categoryMapper.insert(category);
            res.put("message", "新增分类成功！");
        } else {
            categoryMapper.updateById(category);
            res.put("message", "分类修改成功！");
        }
        res.put("success", true);
        return res;
    }

    /**
     * ✨ PBI 4: 删除分类 (前端已经校验了勾选逻辑，后端直接执行)
     */
    @DeleteMapping("/api/admin/categories/{id}")
    public Map<String, Object> deleteCategory(@PathVariable Long id) {
        Map<String, Object> res = new HashMap<>();
        categoryMapper.deleteById(id);
        res.put("success", true);
        res.put("message", "分类已永久删除！");
        return res;
    }
}