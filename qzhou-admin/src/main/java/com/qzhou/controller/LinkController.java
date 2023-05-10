package com.qzhou.controller;

import com.qzhou.annotation.SystemLog;
import com.qzhou.domain.ResponseResult;
import com.qzhou.domain.dto.LinkDto;
import com.qzhou.domain.vo.LinkVo;
import com.qzhou.service.LinkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/content/link")
public class LinkController {

    @Autowired
    private LinkService linkService;

    @GetMapping("/list")
    @PreAuthorize("@ps.hasPermission('content:link:list')")
    public ResponseResult getLinkListToPage(Integer pageNum,Integer pageSize,String name,String status){
        return linkService.getLinkListToPage(pageNum,pageSize,name,status);
    }

    @PostMapping
    @PreAuthorize("@ps.hasPermission('content:link:add')")
    @SystemLog(businessName = "添加友联")
    public ResponseResult addLink(@RequestBody LinkDto linkDto){
        return linkService.addLink(linkDto);
    }

    @PutMapping()
    @PreAuthorize("@ps.hasPermission('content:link:edit')")
    public ResponseResult updateLinkById(@RequestBody LinkVo linkVo){
        return linkService.updateLinkById(linkVo);
    }

    @GetMapping("/{id}")
    @PreAuthorize("@ps.hasPermission('content:link:query')")
    public ResponseResult getLinkById(@PathVariable Long id){
        return linkService.getLinkById(id);
    }

    @DeleteMapping("{id}")
    @PreAuthorize("@ps.hasPermission('content:link:remove')")
    public ResponseResult deleteLinkById(@PathVariable("id") Long id){
        return linkService.deleteLinkById(id);
    }

    @PutMapping("/changeLinkStatus")
    @PreAuthorize("@ps.hasPermission('content:link:edit')")
    public ResponseResult changeLinkStatus(@RequestBody LinkDto linkDto){
        return linkService.changeLinkStatus(linkDto);
    }
}
