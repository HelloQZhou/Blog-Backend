package com.qzhou.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.qzhou.domain.ResponseResult;
import com.qzhou.domain.dto.LinkDto;
import com.qzhou.domain.entity.Link;
import com.qzhou.domain.vo.LinkVo;


/**
 * 友链(Link)表服务接口
 *
 * @author Qzhou
 * @since 2023-04-18 20:11:38
 */
public interface LinkService extends IService<Link> {

    ResponseResult getAllLink();

    /**
     * 获取友联列表 并分页展示
     * 可以对友联名称模糊查询、能对状态进行查询
     * @param pageNum
     * @param pageSize
     * @param name
     * @param status
     * @return
     */
    ResponseResult getLinkListToPage(Integer pageNum, Integer pageSize, String name, String status);

    /**
     * 新增友联
     * @param linkDto
     * @return
     */
    ResponseResult addLink(LinkDto linkDto);

    /**
     * 修改友联
     * @return
     */
    ResponseResult updateLinkById(LinkVo linkVo);

    /**
     * 根据id查询友联信息
     * 修改友联的回显接口
     * @param id
     * @return
     */
    ResponseResult getLinkById(Long id);

    /**
     * 根据id逻辑删除友联信息
     * @param id
     * @return
     */
    ResponseResult deleteLinkById(Long id);

    /**
     * 修改友联状态
     * @param linkDto
     * @return
     */
    ResponseResult changeLinkStatus(LinkDto linkDto);
}

