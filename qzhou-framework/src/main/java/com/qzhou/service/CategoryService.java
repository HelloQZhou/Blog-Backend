package com.qzhou.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qzhou.domain.ResponseResult;
import com.qzhou.domain.dto.CategoryDto;
import com.qzhou.domain.vo.CategoryVo;


/**
 * 分类表(Category)表服务接口
 *
 * @author Qzhou
 * @since 2023-04-17 20:44:26
 */
public interface CategoryService extends IService<entity.Category> {

    /**
     * 在blog中 获取分类列表 有文章的列表才需要展示
     * @return
     */
    ResponseResult getCategoryList();


    /**
     * 在admin中 查询所有的分类列表
     * @return
     */
    ResponseResult getAllCategoryList();

    /**
     * 以分页形式查询所有分类列表
     * @param pageNum
     * @param pageSize
     * @return
     */
    ResponseResult getAllCategoryByPage(Integer pageNum, Integer pageSize);

    /**
     * 增加新分类
     * @param categoryDto
     * @return
     */
    ResponseResult addCategory(CategoryDto categoryDto);

    /**
     * 根据id获取分类信息
     * 修改分类信息的数据回显
     * @param id
     * @return
     */
    ResponseResult getCateGoryById(Long id);

    /**
     * 修改分类信息
     * @param categoryVo
     * @return
     */
    ResponseResult updateCategory(CategoryVo categoryVo);

    /**
     * 根据id逻辑删除分类信息
     * @param id
     * @return
     */
    ResponseResult deleteCategoryById(Long id);
}

