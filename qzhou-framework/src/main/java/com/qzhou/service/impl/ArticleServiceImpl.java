package com.qzhou.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qzhou.constants.SystemConstants;
import com.qzhou.domain.ResponseResult;
import com.qzhou.domain.dto.AddArticleDto;
import com.qzhou.domain.entity.Article;
import com.qzhou.domain.entity.ArticleTag;
import com.qzhou.domain.vo.*;
import com.qzhou.enums.AppHttpCodeEnum;
import com.qzhou.exception.SystemException;
import com.qzhou.mapper.ArticleMapper;
import com.qzhou.mapper.ArticleTagMapper;
import com.qzhou.service.ArticleService;
import com.qzhou.service.ArticleTagService;
import com.qzhou.service.CategoryService;
import com.qzhou.utils.BeanCopyUtils;
import com.qzhou.utils.DateUtils;
import com.qzhou.utils.RedisCache;
import com.qzhou.utils.SecurityUtils;
import entity.Category;
import io.jsonwebtoken.lang.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements ArticleService {
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private RedisCache redisCache;
    @Autowired
    private ArticleTagService articleTagService;
    @Autowired
    private ArticleMapper articleMapper;
    @Autowired
    private ArticleTagMapper articleTagMapper;

    @Override
    public ResponseResult hotArticleList() {

        //查询热门文章 封装成ResponseResult返回
        LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<>();
        //必须是正式文章
        queryWrapper.eq(Article::getStatus,SystemConstants.STATUS_NORMAL);
        //按照浏览量进行排序
        queryWrapper.orderByDesc(Article::getViewCount);
        //最多只查询10条
        Page<Article> page = new Page(1, 10);
        page(page, queryWrapper);

        List<Article> articles = page.getRecords();

        //使用工具类
        List<HotArticleVo> hotArticleVos = BeanCopyUtils.copyBeanList(articles, HotArticleVo.class);

        return ResponseResult.okResult(hotArticleVos);
    }

    @Override
    public ResponseResult articleList(Integer pageNum, Integer pageSize, Long categoryId) {
        //查询条件
        LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<>();
        //如果 有categoryId 就要 查询时要和传入的相同
        queryWrapper.eq(Objects.nonNull(categoryId) && categoryId > 0, Article::getCategoryId, categoryId);
        //状态是正式发布的  （eq 做等值匹配）
        queryWrapper.eq(Article::getStatus, SystemConstants.STATUS_NORMAL);
        //实现首页置顶功能 -> 对isTop进行降序
        queryWrapper.orderByDesc(Article::getIsTop);

        // 按发布时间 / 修改时间进行排序 ，此时是按修改时间
        queryWrapper.orderByDesc(Article::getUpdateTime);

        //分页查询
        Page<Article> page = new Page<>(pageNum, pageSize);
        page(page, queryWrapper);

        //给发送回去客户端的 categoryName 和 ViewCount 赋值 -> 先给page 中的categoryName 赋值
        List<Article> articles = page.getRecords();
        for (Article article : articles) {
            Category category = categoryService.getById(article.getCategoryId());
            article.setCategoryName(category.getName());
            //在redis中获取文章浏览量 -> 设置到article -> 返回给前端
            Integer viewCount = redisCache.getCacheMapValue("article:viewCount",article.getId().toString()) ;
            article.setViewCount(viewCount.longValue());
        }

        //封装查询结果为vo
        List<ArticleListVo> articleListVos = BeanCopyUtils.copyBeanList(articles, ArticleListVo.class);

        PageVo pageVo = new PageVo(articleListVos, page.getTotal());
        return ResponseResult.okResult(pageVo);
    }

    @Override
    public ResponseResult getArticleDetail(Long id) {
        //根据id查询文章
        Article article = getById(id);
        //在redis中获取文章浏览量
        Integer viewCount = redisCache.getCacheMapValue("article:viewCount", id.toString());
        //转换为vo
        ArticleDetailVo articleDetailVo = BeanCopyUtils.copyBean(article, ArticleDetailVo.class);

        //更新浏览量
        articleDetailVo.setViewCount(viewCount.longValue());

        //根据分类id查出categoryName 并设置在 articleDetailVo 中返回
        String name = categoryService.getById(articleDetailVo.getCategoryId()).getName();
        if (name != null) {
            articleDetailVo.setCategoryName(name);
        }
        //封装响应返回
        return ResponseResult.okResult(articleDetailVo);
    }

    @Override
    public ResponseResult updateViewCountToRedis(Long id) {
        //根据id在redis找出对应文章的浏览量 更新/加一
        redisCache.incrementCacheMapValue("article:viewCount",id.toString(),1);
        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult addArticle(AddArticleDto addArticleDto) {
        //(配合前端错误 ->此时前端置顶返回0)
        if(addArticleDto.getIsTop().equals(SystemConstants.ARTICLE_IS_TOP)){
            addArticleDto.setIsTop(SystemConstants.ARTICLE_NOT_TOP);
        }else addArticleDto.setIsTop(SystemConstants.ARTICLE_IS_TOP);

        //需要对传入的参数判空 ->文章标题、内容、分类不能为空
        if(!Strings.hasText(addArticleDto.getTitle())){
            throw new SystemException(AppHttpCodeEnum.ARTICLE_TITLE_NOT_NULL);
        }
        if(!Strings.hasText(addArticleDto.getContent())){
            throw new SystemException(AppHttpCodeEnum.ARTICLE_CONTENT_NOT_NULL);
        }
        if(addArticleDto.getCategoryId()==null){
            throw new SystemException(AppHttpCodeEnum.ARTICLE_CATEGORY_NOT_NULL);
        }

        //需要判断此时文章是否为置顶，是则取消上一篇文章的置顶
        if(addArticleDto.getIsTop().equals(SystemConstants.ARTICLE_IS_TOP)){
            LambdaUpdateWrapper<Article> updateWrapper=new LambdaUpdateWrapper<>();
            updateWrapper.eq(Article::getIsTop,SystemConstants.ARTICLE_IS_TOP).set(Article::getIsTop,SystemConstants.ARTICLE_NOT_TOP);
            update(updateWrapper);
        }

        Long userId = SecurityUtils.getUserId();
        //添加数据库中表 Article 中数据
        Article article = BeanCopyUtils.copyBean(addArticleDto, Article.class);
        article.setUpdateBy(userId);
        article.setUpdateTime(DateUtils.getCurrentTime());
        article.setCreateBy(userId);
        article.setCreateTime(DateUtils.getCurrentTime());
        save(article);

        // 添加数据库中 article_tag 表中数据
        Long articleId = article.getId();
        List<ArticleTag> articleTags=new ArrayList<>();
        for (Long categoryId: addArticleDto.getTags()){
            articleTags.add(new ArticleTag(articleId,categoryId));
        }
        articleTagService.saveBatch(articleTags);

        //将浏览量添加到redis 不然页面读取会报NullPointerException
        Map<String,Integer> map=new HashMap<>();
        map.put(article.getId().toString(),0);
        redisCache.setCacheMap("article:viewCount",map);

        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult getArticleList(Integer pageNum, Integer pageSize, String title, String summary) {
        LambdaQueryWrapper<Article> queryWrapper=new LambdaQueryWrapper<>();
        //此时的like 方法需要判空 不然会查询错误 不能使用Objects.nonnull
        queryWrapper.like(Strings.hasText(title),Article::getTitle,title);
        queryWrapper.like(Strings.hasText(summary),Article::getSummary,summary);
        Page<Article> page = new Page<>(pageNum, pageSize);
        page(page,queryWrapper);
        List<AdminArticleVo> adminArticleVos = BeanCopyUtils.copyBeanList(page.getRecords(), AdminArticleVo.class);
        return ResponseResult.okResult(new PageVo(adminArticleVos,page.getTotal()));

    }

    @Override
    public ResponseResult getArticleInfoById(Long id) {
        Article article = articleMapper.selectById(id);
        List<Long> TagIds=articleTagMapper.selectListByArticleId(id);

        ArticleInfoVo articleInfoVo = BeanCopyUtils.copyBean(article, ArticleInfoVo.class);

        articleInfoVo.setTags(TagIds);
        return ResponseResult.okResult(articleInfoVo);
    }

    @Override
    public ResponseResult updateArticleByArticleInfoVo(ArticleInfoVo articleInfoVo) {
        //(配合前端错误 ->此时前端置顶返回0)
        if(articleInfoVo.getIsTop().equals(SystemConstants.ARTICLE_IS_TOP)){
            articleInfoVo.setIsTop(SystemConstants.ARTICLE_NOT_TOP);
        }else articleInfoVo.setIsTop(SystemConstants.ARTICLE_IS_TOP);

        //需要判断此时文章是否为置顶，是则取消上一篇文章的置顶
        if(articleInfoVo.getIsTop().equals(SystemConstants.ARTICLE_IS_TOP)){
            LambdaUpdateWrapper<Article> updateWrapper=new LambdaUpdateWrapper<>();
            updateWrapper.eq(Article::getIsTop,SystemConstants.ARTICLE_IS_TOP).set(Article::getIsTop,SystemConstants.ARTICLE_NOT_TOP);
            update(updateWrapper);
        }

        Article article = BeanCopyUtils.copyBean(articleInfoVo, Article.class);

        article.setCreateTime(DateUtils.getCurrentTime());
        article.setCreateBy(SecurityUtils.getUserId());
        LambdaQueryWrapper<Article> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(Article::getId,article.getId());
        // 1、更新数据库表 article
        //如果此时 queryWrapper=null 的话 则会更新 所有del_flag=0 的行
        //呜呜 将数据全更新了
        update(article,queryWrapper);
        //2、更新数据库表article_tag
        Long articleId = articleInfoVo.getId();
        //2.1、根据此时文章id删除在article_tag 表下的数据
        LambdaQueryWrapper<ArticleTag> articleTagLambdaQueryWrapper=new LambdaQueryWrapper<>();
        articleTagLambdaQueryWrapper.eq(ArticleTag::getArticleId, articleId);
        articleTagMapper.delete(articleTagLambdaQueryWrapper);
        //2.2、添加此时获取的标签 添加到表article_tag
        List<ArticleTag> articleTagList=new ArrayList<>();
        for(Long tagId:articleInfoVo.getTags()){
            articleTagList.add(new ArticleTag(articleId,tagId));
        }
        articleTagService.saveBatch(articleTagList);

        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult deleteArticleById(Long id) {
        LambdaUpdateWrapper<Article> updateWrapper=new LambdaUpdateWrapper<>();
        updateWrapper.eq(Article::getId,id).set(Article::getDelFlag,1);
        update(updateWrapper);

        return ResponseResult.okResult();
    }
}
