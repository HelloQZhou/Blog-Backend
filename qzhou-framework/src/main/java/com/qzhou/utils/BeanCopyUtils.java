package com.qzhou.utils;

import com.qzhou.domain.entity.Article;
import com.qzhou.domain.vo.HotArticleVo;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;

public class BeanCopyUtils {
    public BeanCopyUtils() {
    }

    //传入 待转化的数据（大范围） 和转化后的数据类型（小范围） 返回后者
    public static <V> V copyBean(Object source, Class<V> clazz) {
        V result = null;
        try {
            result = clazz.newInstance();
            //实现属性copy
            BeanUtils.copyProperties(source, result);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return result;
    }

    //返回 list 集合
    public static <O, V> List<V> copyBeanList(List<O> articles, Class<V> clazz) {
        List<V> list = new ArrayList<>();
        for (Object object : articles) {
            try {
                V result = clazz.newInstance();
                BeanUtils.copyProperties(object, result);
                list.add(result);
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return list;
    }

    //测试
    public static void main(String[] args) {
        Article article = new Article();
        article.setId(1L);
        article.setTitle("title");
        article.setContent("content");

        HotArticleVo hotArticleVo = BeanCopyUtils.copyBean(article, HotArticleVo.class);
        System.out.println(hotArticleVo);
    }
}
