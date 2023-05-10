package com.qzhou.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qzhou.domain.ResponseResult;
import com.qzhou.domain.dto.AddArticleDto;
import com.qzhou.domain.entity.Article;
import com.qzhou.domain.vo.ArticleInfoVo;

public interface ArticleService extends IService<Article> {
    /**
     * 热门列表 （浏览量前十） 【不实时更新，是隔一段时间更新】【读取数据库中的访问量】
     * @return
     */
    ResponseResult hotArticleList();

    /**
     * 在首页和分类页面展示文章列表
     * @param pageNum
     * @param pageSize
     * @param categoryId
     * @return
     */
    ResponseResult articleList(Integer pageNum, Integer pageSize, Long categoryId);

    /**
     * 查询文章详情页
     * @param id
     * @return
     */
    ResponseResult getArticleDetail(Long id);

    /**
     * 根据id更新redis中的浏览量
     * @param id
     * @return
     */
    ResponseResult updateViewCountToRedis(Long id);

    /**
     * 新增博文
     * @return
     * @param addArticleDto
     */
    ResponseResult addArticle(AddArticleDto addArticleDto);

    /**
     * 后台获取文章列表 可以 关键字/模糊 查询
     * @return
     * @param pageNum
     * @param pageSize
     * @param title
     * @param summary
     */
    ResponseResult getArticleList(Integer pageNum, Integer pageSize, String title, String summary);

    /**
     * 查询根据id查询文章详情信息
     * @param id
     * @return
     */
    ResponseResult getArticleInfoById(Long id);

    /**
     * 更新文章信息
     * @param articleInfoVo
     * @return
     */
    ResponseResult updateArticleByArticleInfoVo(ArticleInfoVo articleInfoVo);

    /**
     * 逻辑删除文章
     * @param id
     * @return
     */
    ResponseResult deleteArticleById(Long id);
}
