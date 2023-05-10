package com.qzhou.domain.entity;import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

;
/**
 * 文章标签关联表(ArticleTag)表实体类
 *
 * @author Qzhou
 * @since 2023-04-30 23:36:34
 */
@SuppressWarnings("serial")
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("qz_article_tag")
public class ArticleTag  {
    //文章id@TableId
    private Long articleId;
    //标签id@TableId
    private Long tagId;


}

