package com.qzhou.job;

import com.qzhou.domain.entity.Article;
import com.qzhou.service.ArticleService;
import com.qzhou.utils.RedisCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class UpdateViewCountJob {

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private ArticleService articleService;

    @Scheduled(cron = " 0/30 * * * * ?")
    public void UpdateViewCount(){
        //获取redis中的浏览量
        Map<String,Integer> cacheMap = redisCache.getCacheMap("article:viewCount");

        //遍历 set集合
//        Set<Map.Entry<String, Long>> entries = cacheMap.entrySet();
//        List<Article> articleList = new ArrayList<>();
//        for (Map.Entry<String, Long> entry:entries) {
//                articleList.add(new Article(Long.valueOf(entry.getKey()), entry.getValue()));
//        }
//stream 流
        List<Article> articleList = cacheMap.entrySet()
                .stream()
                .map(entry -> new Article(Long.valueOf(entry.getKey()), entry.getValue().longValue()))
                .collect(Collectors.toList());

        //更新到数据库中
        articleService.updateBatchById(articleList);
    }
}
