package com.qzhou.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qzhou.domain.ResponseResult;
import com.qzhou.domain.dto.TagListDto;
import com.qzhou.domain.entity.Tag;
import com.qzhou.domain.vo.PageVo;
import com.qzhou.domain.vo.TagVo;
import com.qzhou.mapper.TagMapper;
import com.qzhou.service.TagService;
import com.qzhou.utils.BeanCopyUtils;
import com.qzhou.utils.DateUtils;
import com.qzhou.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 标签(Tag)表服务实现类
 *
 * @author Qzhou
 * @since 2023-04-28 15:14:03
 */
@Service("tagService")
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag> implements TagService {

    @Autowired
    private TagMapper tagMapper;
    @Override
    public ResponseResult pageTagList(Integer pageNum, Integer pageSize, TagListDto tagListDto) {

        LambdaQueryWrapper<Tag> queryWrapper=new LambdaQueryWrapper();
        queryWrapper.eq(StringUtils.hasText(tagListDto.getName()),Tag::getName,tagListDto.getName());
        queryWrapper.eq(StringUtils.hasText(tagListDto.getRemark()),Tag::getRemark,tagListDto.getRemark());

        Page<Tag> page = new Page<>(pageNum,pageSize);
        page(page,queryWrapper);
        List<TagVo> tagVos = BeanCopyUtils.copyBeanList(page.getRecords(), TagVo.class);
        return ResponseResult.okResult(new PageVo(tagVos,page.getTotal()));
    }

    @Override
    public ResponseResult addTag(TagListDto tagListDto) {
        Long userId = SecurityUtils.getUserId();
        save(new Tag(tagListDto.getName(),userId, DateUtils.getCurrentTime(),userId, DateUtils.getCurrentTime(),tagListDto.getRemark()));
        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult deleteTag(Long id) {
        LambdaUpdateWrapper<Tag> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.eq(Tag::getId,id).set(Tag::getDelFlag,1);
        update(lambdaUpdateWrapper);
        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult getUpdateInfoById(Long id) {
        Tag tag = tagMapper.selectById(id);
        TagVo tagVo = BeanCopyUtils.copyBean(tag, TagVo.class);
        return ResponseResult.okResult(tagVo);
    }

    @Override
    public ResponseResult updateTag(TagVo tagVo) {
        Long userId = SecurityUtils.getUserId();

        tagMapper.updateById(new Tag(tagVo.getId(),tagVo.getName(),userId,DateUtils.getCurrentTime(),tagVo.getRemark()));
        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult getAllTagList() {
        List<Tag> list = list();

        List<TagVo> tagVos = BeanCopyUtils.copyBeanList(list, TagVo.class);

        return ResponseResult.okResult(tagVos);
    }
}

