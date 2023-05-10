package com.qzhou.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qzhou.constants.SystemConstants;
import com.qzhou.domain.ResponseResult;
import com.qzhou.domain.dto.LinkDto;
import com.qzhou.domain.entity.Link;
import com.qzhou.domain.vo.LinkVo;
import com.qzhou.domain.vo.PageVo;
import com.qzhou.mapper.LinkMapper;
import com.qzhou.service.LinkService;
import com.qzhou.utils.BeanCopyUtils;
import com.qzhou.utils.DateUtils;
import com.qzhou.utils.SecurityUtils;
import io.jsonwebtoken.lang.Strings;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 友链(Link)表服务实现类
 *
 * @author Qzhou
 * @since 2023-04-18 20:11:38
 */
@Service("linkService")
public class LinkServiceImpl extends ServiceImpl<LinkMapper, Link> implements LinkService {

    @Override
    public ResponseResult getAllLink() {
        //查询 所有通过审核的友链
        LambdaQueryWrapper<Link> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Link::getStatus, SystemConstants.LINK_STATUS_NORMAL);
        //得到list集合
        List<Link> list = list(queryWrapper);
        //转换为vo
        List<LinkVo> linkVos = BeanCopyUtils.copyBeanList(list, LinkVo.class);
        return ResponseResult.okResult(linkVos);
    }

    @Override
    public ResponseResult getLinkListToPage(Integer pageNum, Integer pageSize, String name, String status) {

        LambdaQueryWrapper<Link> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.like(Strings.hasText(name),Link::getName,name);
        queryWrapper.like(Strings.hasText(status),Link::getStatus,status);

        Page<Link> page = new Page<>(pageNum, pageSize);
        page(page,queryWrapper);

        List<LinkVo> linkAdminVos = BeanCopyUtils.copyBeanList(page.getRecords(), LinkVo.class);

        return ResponseResult.okResult(new PageVo(linkAdminVos,page.getTotal()));
    }

    @Override
    public ResponseResult addLink(LinkDto linkDto) {
        Date currentTime = DateUtils.getCurrentTime();
        Long userId = SecurityUtils.getUserId();
        Link link = BeanCopyUtils.copyBean(linkDto, Link.class);
        link.setCreateBy(userId);
        link.setCreateTime(currentTime);
        link.setUpdateBy(userId);
        link.setUpdateTime(currentTime);

        save(link);

        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult updateLinkById(LinkVo linkVo) {

        Link link = BeanCopyUtils.copyBean(linkVo, Link.class);
        link.setUpdateTime(DateUtils.getCurrentTime());
        link.setUpdateBy(SecurityUtils.getUserId());

        LambdaQueryWrapper<Link> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(Link::getId,linkVo.getId());
        update(link,queryWrapper);

        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult getLinkById(Long id) {
        Link link = getById(id);
        LinkVo linkVo = BeanCopyUtils.copyBean(link, LinkVo.class);

        return ResponseResult.okResult(linkVo);
    }

    @Override
    public ResponseResult deleteLinkById(Long id) {
        LambdaUpdateWrapper<Link> updateWrapper=new LambdaUpdateWrapper<>();
        updateWrapper.eq(Link::getId,id)
                .set(Link::getDelFlag,SystemConstants.STATUS_ABNORMAL)
                .set(Link::getUpdateBy,SecurityUtils.getUserId())
                .set(Link::getUpdateTime,DateUtils.getCurrentTime());
        update(updateWrapper);

        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult changeLinkStatus(LinkDto linkDto) {
        LambdaUpdateWrapper<Link> updateWrapper=new LambdaUpdateWrapper<>();
        updateWrapper.eq(Link::getId,linkDto.getId())
                .set(Link::getStatus,linkDto.getStatus())
                .set(Link::getUpdateBy,SecurityUtils.getUserId())
                .set(Link::getUpdateTime,DateUtils.getCurrentTime());
        update(updateWrapper);
        return ResponseResult.okResult();
    }
}

