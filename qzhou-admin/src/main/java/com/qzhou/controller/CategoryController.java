package com.qzhou.controller;

import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson.JSON;
import com.qzhou.domain.ResponseResult;
import com.qzhou.domain.dto.CategoryDto;
import com.qzhou.domain.vo.CategoryVo;
import com.qzhou.domain.vo.ExcelCategoryVo;
import com.qzhou.enums.AppHttpCodeEnum;
import com.qzhou.service.CategoryService;
import com.qzhou.utils.BeanCopyUtils;
import com.qzhou.utils.WebUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/content/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    @GetMapping("/listAllCategory")
    public ResponseResult getAllCategoryList(){
        return categoryService.getAllCategoryList();
    }

    @GetMapping("/export")
    @PreAuthorize("@ps.hasPermission('content:category:export')")
    public void export(HttpServletResponse response)throws IOException {

        try {
            //设置下载文件的请求头
            WebUtils.setDownLoadHeader("分类.xlsx",response);

            //获取需要导出的数据
            List<entity.Category> categoryVos = categoryService.list();

            //转化为返回给客户端的格式
            List<ExcelCategoryVo> excelCategoryVos = BeanCopyUtils.copyBeanList(categoryVos, ExcelCategoryVo.class);
            // 这里需要设置不关闭流
            //把数据写入到Excel中
            EasyExcel.write(response.getOutputStream(), ExcelCategoryVo.class).autoCloseStream(Boolean.FALSE).sheet("模板")
                    .doWrite(excelCategoryVos);
        } catch (Exception e) {
//            // 重置response
//            response.reset();
//            response.setContentType("application/json");
//            response.setCharacterEncoding("utf-8");
//            Map<String, String> map = MapUtils.newHashMap();
//            map.put("status", "failure");
//            map.put("message", "下载文件失败" + e.getMessage());
//            response.getWriter().println(JSON.toJSONString(map));

            //如果出现异常也要响应json
            ResponseResult result = ResponseResult.errorResult(AppHttpCodeEnum.SYSTEM_ERROR);
            WebUtils.renderString(response, JSON.toJSONString(result));
        }

    }

    @GetMapping("/list")
    public ResponseResult getAllCategoryByPage(Integer pageNum,Integer pageSize){
        return categoryService.getAllCategoryByPage(pageNum,pageSize);
    }

    @PostMapping
    public ResponseResult addCategory(@RequestBody CategoryDto categoryDto){
        return categoryService.addCategory(categoryDto);
    }

    @GetMapping("/{id}")
    public ResponseResult getCategoryById(@PathVariable("id") Long id){
        return categoryService.getCateGoryById(id);
    }

    @PutMapping
    public ResponseResult updateCategory(@RequestBody CategoryVo categoryVo){
        return categoryService.updateCategory(categoryVo);
    }

    @DeleteMapping("/{id}")
    public ResponseResult deleteCategoryById(@PathVariable Long id){
        return categoryService.deleteCategoryById(id);
    }

}
