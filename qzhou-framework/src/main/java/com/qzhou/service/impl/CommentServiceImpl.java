package com.qzhou.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qzhou.constants.SystemConstants;
import com.qzhou.domain.ResponseResult;
import com.qzhou.domain.entity.Comment;
import com.qzhou.domain.vo.CommentVo;
import com.qzhou.domain.vo.PageVo;
import com.qzhou.enums.AppHttpCodeEnum;
import com.qzhou.exception.SystemException;
import com.qzhou.mapper.CommentMapper;
import com.qzhou.service.CommentService;
import com.qzhou.service.UserService;
import com.qzhou.utils.BeanCopyUtils;
import io.jsonwebtoken.lang.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 评论表(Comment)表服务实现类
 *
 * @author Qzhou
 * @since 2023-04-23 10:39:37
 */
@Service("commentService")
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {

    @Autowired
    private UserService userService;

    @Override
    public ResponseResult getCommentList(String commentType,Long articleId, Integer pageNum, Integer pageSize) {
        //1、查询 文章/友联 评论列表 【此时需要判断文章/友联下没有记录怎么办】
        //2、查询根评论
        LambdaQueryWrapper<Comment> queryWrapper=new LambdaQueryWrapper<>();
        //2.1 判断此时类型，是文章则获取文章评论列表，
        queryWrapper.eq(SystemConstants.ARTICLE_COMMENT.equals(commentType),Comment::getArticleId,articleId);
        //文章根评论 (id = -1)
        queryWrapper.eq(Comment::getRootId, SystemConstants.COMMENT_ROOT_ID);
        //2.2 评论类型
        queryWrapper.eq(Comment::getType,commentType);

        //3、分页显示该查询
        Page<Comment> page = new Page(pageNum,pageSize);
        page(page,queryWrapper);
        //4、封装需要返回给客户端的消息
        // 实现 无评论回复
        List<CommentVo> commentVos =getCommentVo(page.getRecords());
        //实现 有评论回复  / rows 中的 children
        for (CommentVo commentVo : commentVos) {
            List<CommentVo> children = getChildren(commentVo.getId());
            commentVo.setChildren(children);
        }

        //5、按照格式返回给客户端
        return ResponseResult.okResult(new PageVo(commentVos,page.getTotal()));
    }

    @Override
    public ResponseResult insertComment(Comment comment) {
        //评论不能为空  str != null && str.length() > 0
        if(!Strings.hasText(comment.getContent())){
            //为空 则抛出自定义异常
            throw new SystemException(AppHttpCodeEnum.CONTENT_NOT_NULL);
        }
        //添加公共字段 -> 使用 MP 中字段自动填充的功能

        //将评论存入数据库
        save(comment);
        return ResponseResult.okResult();
    }

    /**
     * 根据根评论id 查询所对应的子评论的集合
     * @param id 根评论Id
     * @return
     */
    public List<CommentVo> getChildren(Long id){
            LambdaQueryWrapper<Comment> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Comment::getRootId, id);
            queryWrapper.orderByAsc(Comment::getCreateTime);
            List<Comment> comments = list(queryWrapper);
            
            List<CommentVo> commentVo=getCommentVo(comments);

        return commentVo;
    }

    //工具类 得到客户端所需的rows 父评论
    public List<CommentVo> getCommentVo(List<Comment> lists){
        List<CommentVo> commentVos=BeanCopyUtils.copyBeanList(lists, CommentVo.class);
        for(CommentVo commentVo:commentVos){
            //使用 回复评论的用户id 得到 回复评论的用户昵称 并赋值 toCommentUserId -> toCommentUserName
            //此时需要判断 是不是根评论 根评论则没有回复的用户
            if(commentVo.getToCommentId()!=SystemConstants.COMMENT_ROOT_ID){
                String nickName = userService.getById(commentVo.getToCommentUserId()).getNickName();
                commentVo.setToCommentUserName(nickName);
            }
            //使用 创建人id得到用户昵称 并赋值
            String userName = userService.getById(commentVo.getCreateBy()).getUserName();
            commentVo.setUsername(userName);

        }
        return commentVos;
    }
}

