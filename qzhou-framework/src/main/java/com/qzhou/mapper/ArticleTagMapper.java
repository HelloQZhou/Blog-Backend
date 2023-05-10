package com.qzhou.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qzhou.domain.entity.ArticleTag;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;


/**
 * 文章标签关联表(ArticleTag)表数据库访问层
 *
 * @author Qzhou
 * @since 2023-04-30 23:36:34
 */
@Mapper
public interface ArticleTagMapper extends BaseMapper<ArticleTag> {
    @Select("SELECT tag_id\n" +
            "\tFROM qz_article_tag\n" +
            "\tWHERE article_id=#{id}")
    List<Long> selectListByArticleId(Long id);
}

