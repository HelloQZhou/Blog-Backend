package com.qzhou.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qzhou.constants.SystemConstants;
import com.qzhou.domain.ResponseResult;
import com.qzhou.domain.dto.CategoryDto;
import com.qzhou.domain.entity.Article;
import com.qzhou.domain.vo.CategoryVo;
import com.qzhou.domain.vo.PageVo;
import com.qzhou.mapper.CategoryMapper;
import com.qzhou.service.ArticleService;
import com.qzhou.service.CategoryService;
import com.qzhou.utils.BeanCopyUtils;
import com.qzhou.utils.DateUtils;
import com.qzhou.utils.SecurityUtils;
import entity.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 分类表(Category)表服务实现类
 *
 * @author Qzhou
 * @since 2023-04-17 20:44:26
 */
@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, entity.Category> implements CategoryService {

    @Autowired
    private ArticleService articleService;

    @Override
    public ResponseResult getCategoryList() {
        //查询分类 -> 有对应文章才展示
        //查询文章表，状态为已发布的文章
        LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Article::getStatus,SystemConstants.STATUS_NORMAL);
        List<Article> lists = articleService.list(queryWrapper);

        //获取文章分类id 并且去重
        //由于要去重，所以最后将其转化为set集合自动去重
//        List<Long> categoryIds = new ArrayList<>();
//        for (Article list : lists) {
//            Long id = list.getCategoryId();
//            if (id != null) {
//                categoryIds.add(id);
//            }
//        }
        Set<Long> categoryIds = lists.stream()
                .map(Article::getCategoryId)
                .collect(Collectors.toSet());

        //得到分类id的set集合，查询分类表
        List<Category> categories = new ArrayList<>();
        for (Long id : categoryIds) {
            if (getById(id).getStatus().equals(SystemConstants.STATUS_NORMAL)) {
                categories.add(getById(id));
            }
        }

        //封装vo
        List<CategoryVo> categoryVos = BeanCopyUtils.copyBeanList(categories, CategoryVo.class);

        return ResponseResult.okResult(categoryVos);
    }

    @Override
    public ResponseResult getAllCategoryList() {
//        LambdaQueryWrapper<Category> queryWrapper=new LambdaQueryWrapper<>();
//        queryWrapper.eq(Category::getStatus,SystemConstants.STATUS_NORMAL);
//        List<Category> list = list(queryWrapper);
        List<Category> list = list();
        List<CategoryVo> categoryVos = BeanCopyUtils.copyBeanList(list, CategoryVo.class);

        return ResponseResult.okResult(categoryVos);
    }

    @Override
    public ResponseResult getAllCategoryByPage(Integer pageNum, Integer pageSize) {

        Page<Category> page=new Page<>(pageNum,pageSize);
        page(page);
        List<CategoryVo> categoryVos = BeanCopyUtils.copyBeanList(page.getRecords(), CategoryVo.class);
        return ResponseResult.okResult(new PageVo(categoryVos,page.getTotal()));
    }

    @Override
    public ResponseResult addCategory(CategoryDto categoryDto) {
        Long userId = SecurityUtils.getUserId();
        Date currentTime = DateUtils.getCurrentTime();
        Category category = new Category(categoryDto.getName(), categoryDto.getDescription(),
                userId, currentTime, userId, currentTime, categoryDto.getStatus());
        save(category);
        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult getCateGoryById(Long id) {
        Category category = getById(id);
        CategoryVo categoryVo = BeanCopyUtils.copyBean(category, CategoryVo.class);
        return ResponseResult.okResult(categoryVo);
    }

    @Override
    public ResponseResult updateCategory(CategoryVo categoryVo) {
        Category category = BeanCopyUtils.copyBean(categoryVo, Category.class);
        category.setUpdateBy(SecurityUtils.getUserId());
        category.setUpdateTime(DateUtils.getCurrentTime());

        LambdaUpdateWrapper<Category> updateWrapper=new LambdaUpdateWrapper<>();
        updateWrapper.eq(Category::getId,categoryVo.getId());
        update(category,updateWrapper);

        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult deleteCategoryById(Long id) {
        LambdaUpdateWrapper<Category> updateWrapper=new LambdaUpdateWrapper<>();
        updateWrapper.eq(Category::getId,id).set(Category::getDelFlag,SystemConstants.STATUS_ABNORMAL);
        update(updateWrapper);
        return ResponseResult.okResult();
    }
}

