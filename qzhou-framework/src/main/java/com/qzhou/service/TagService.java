package com.qzhou.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.qzhou.domain.ResponseResult;
import com.qzhou.domain.dto.TagListDto;
import com.qzhou.domain.entity.Tag;
import com.qzhou.domain.vo.TagVo;


/**
 * 标签(Tag)表服务接口
 *
 * @author Qzhou
 * @since 2023-04-28 15:14:03
 */
public interface TagService extends IService<Tag> {

    /**
     * 在标签管理中查询文章
     * @param pageNum
     * @param pageSize
     * @param tagListDto
     * @return
     */
    ResponseResult pageTagList(Integer pageNum, Integer pageSize, TagListDto tagListDto);

    /**
     * 新增标签
     * @return
     * @param tagListDto
     */
    ResponseResult addTag(TagListDto tagListDto);

    /**
     * 删除标签
     * @param id
     * @return
     */
    ResponseResult deleteTag(Long id);

    /**
     * 点击修改信息，获得需要修改的信息
     * @param id
     * @return
     */
    ResponseResult getUpdateInfoById(Long id);

    /**
     * 提交修改信息，此时在数据库中修改信息
     * @param tagVo
     * @return
     */
    ResponseResult updateTag(TagVo tagVo);

    /**
     * 查询所有的标签
     */
    ResponseResult getAllTagList();
}

