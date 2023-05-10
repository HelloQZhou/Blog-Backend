package com.qzhou.controller;

import com.qzhou.domain.ResponseResult;
import com.qzhou.domain.dto.AddArticleDto;
import com.qzhou.domain.vo.ArticleInfoVo;
import com.qzhou.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
//@RequestMapping("getArticleList")
@RequestMapping("/content/article")
public class ArticleController {
    @Autowired
    private ArticleService articleService;

    @PostMapping
    @PreAuthorize("@ps.hasPermission('content:article:writer')")
    public ResponseResult addArticle(@RequestBody AddArticleDto addArticleDto){
        return articleService.addArticle(addArticleDto);
    }

    @GetMapping("/list")
    //@PreAuthorize("@ps.hasPermission('content:article:list')")
    public ResponseResult getArticleList(Integer pageNum,Integer pageSize,String title,String summary){
        return articleService.getArticleList(pageNum,pageSize,title,summary);
    }

    @GetMapping("/{id}")
    public ResponseResult getArticleInfoById(@PathVariable Long id){
        return articleService.getArticleInfoById(id);
    }

    @PutMapping
    public ResponseResult updateArticle(@RequestBody ArticleInfoVo articleInfoVo){

        return articleService.updateArticleByArticleInfoVo(articleInfoVo);
    }

    @DeleteMapping("/{id}")
    public ResponseResult deleteArticleById(@PathVariable("id") Long id){
        return articleService.deleteArticleById(id);
    }
}
