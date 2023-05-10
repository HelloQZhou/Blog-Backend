package com.qzhou.runner;

import com.qzhou.domain.entity.Article;
import com.qzhou.mapper.ArticleMapper;
import com.qzhou.utils.RedisCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Component
public class viewRunner implements CommandLineRunner {
    @Autowired
    private ArticleMapper articleMapper;
    @Autowired
    private RedisCache redisCache;

    @Override
    public void run(String... args) throws Exception {
        //查询博客信息 id viewCount
         List<Article> articles = articleMapper.selectList(null);
//        Map<String,Integer> viewCountMap=new HashMap<>();
//        for (Article article :articles) {
//            viewCountMap.put(article.getId().toString(),article.getViewCount());
//        }

        Map<String, Integer> viewCountMap = articles.stream()
                .collect(Collectors.toMap(article -> article.getId().toString(), article -> {
                    return article.getViewCount().intValue();//如果是Long类型的话，存入redis会变成1L
                }));

        //存储到redis
        redisCache.setCacheMap("article:viewCount",viewCountMap);
    }
}
