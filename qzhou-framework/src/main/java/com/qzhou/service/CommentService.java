package com.qzhou.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.qzhou.domain.ResponseResult;
import com.qzhou.domain.entity.Comment;


/**
 * 评论表(Comment)表服务接口
 *
 * @author Qzhou
 * @since 2023-04-23 10:39:37
 */
public interface CommentService extends IService<Comment> {
    /**
     * 获取文章的评论列表 -> 先获取不带评论回复的列表 -> 再加上评论的更评论
     *    友联
     * @param articleId
     * @param pageNum
     * @param pageSize
     * @return
     */
    ResponseResult getCommentList(String commentType,Long articleId, Integer pageNum, Integer pageSize);

    /**
     * 增加评论 和 评论回复
     * @param comment
     * @return
     */
    ResponseResult insertComment(Comment comment);


}

