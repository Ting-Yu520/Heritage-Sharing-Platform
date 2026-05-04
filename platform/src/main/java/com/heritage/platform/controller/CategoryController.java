package com.heritage.platform.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.heritage.platform.config.RoleCheck;
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
@RoleCheck("ADMIN")   // All APIs in this controller are ADMIN-only
public class CategoryController {

    @Autowired private CategoryMapper categoryMapper;
    @Autowired private HeritageResourceMapper resourceMapper;

    /**
     * Get category list (keyword search, status filter, resource usage stats)
     */
    @GetMapping("/api/admin/categories")
    public List<Map<String, Object>> getCategories(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String filterStatus) {

        QueryWrapper<Category> query = new QueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            query.like("name", keyword).or().like("description", keyword);
        }
        query.orderByDesc("created_at");
        List<Category> categories = categoryMapper.selectList(query);

        List<Map<String, Object>> result = new ArrayList<>();

        for (Category c : categories) {
            long usageCount = resourceMapper.selectCount(new QueryWrapper<HeritageResource>().eq("category", c.getName()));

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
     * Create or update category
     */
    @PostMapping("/api/admin/categories")
    public Map<String, Object> saveCategory(@RequestBody Category category) {
        Map<String, Object> res = new HashMap<>();

        QueryWrapper<Category> dupQuery = new QueryWrapper<Category>().eq("name", category.getName());
        if (category.getId() != null) dupQuery.ne("id", category.getId());
        if (categoryMapper.selectCount(dupQuery) > 0) {
            res.put("success", false); res.put("message", "Category name already exists. Please choose another one!"); return res;
        }

        category.setUpdatedAt(LocalDateTime.now());
        if (category.getId() == null) {
            category.setCreatedAt(LocalDateTime.now());
            categoryMapper.insert(category);
            res.put("message", "Category created successfully!");
        } else {
            categoryMapper.updateById(category);
            res.put("message", "Category updated successfully!");
        }
        res.put("success", true);
        return res;
    }

    /**
     * Delete category
     */
    @DeleteMapping("/api/admin/categories/{id}")
    public Map<String, Object> deleteCategory(@PathVariable Long id) {
        Map<String, Object> res = new HashMap<>();
        categoryMapper.deleteById(id);
        res.put("success", true);
        res.put("message", "Category permanently deleted!");
        return res;
    }
}
