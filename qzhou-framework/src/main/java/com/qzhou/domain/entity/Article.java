package com.qzhou.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

/**
 * 文章表(Article)表实体类
 *
 * @author Qzhou
 * @since 2023-04-14 18:24:54
 */
@SuppressWarnings("serial")
@AllArgsConstructor
@Data
@NoArgsConstructor
//与数据库的表进行映射
@TableName("qz_article")
public class Article {
    //标识一下 主键
    @TableId
    private Long id;
    //标题
    private String title;
    //文章内容
    private String content;
    //文章摘要
    private String summary;
    //所属分类id
    private Long categoryId;
    //mybatis 添加数据库不存在的列  是否存在在数据表
    @TableField(exist = false)
    private String categoryName;
    //缩略图
    private String thumbnail;
    //是否置顶（0否，1是）
    private String isTop;
    //状态（0已发布，1草稿）
    private String status;
    //访问量
    private Long viewCount;
    //是否允许评论 1是，0否
    private String isComment;

    private Long createBy;

    private Date createTime;

    private Long updateBy;

    private Date updateTime;
    //删除标志（0代表未删除，1代表已删除）
    private Integer delFlag;

    @TableField(exist = false)
    private List<Integer> tags;

    public Article(Long id, Long viewCount) {
        this.id = id;
        this.viewCount = viewCount;
    }


}

