package com.qzhou.controller;

import com.qzhou.domain.ResponseResult;
import com.qzhou.domain.dto.TagListDto;
import com.qzhou.domain.vo.TagVo;
import com.qzhou.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/content/tag")
public class TagController {
    @Autowired
    private TagService tagService;

    @GetMapping("/list")
    public ResponseResult list(Integer pageNum, Integer pageSize, TagListDto tagListDto){
        return tagService.pageTagList(pageNum,pageSize,tagListDto);
       // return ResponseResult.okResult(tagService.list());
    }

    @PostMapping
    public ResponseResult addTag(@RequestBody TagListDto tagListDto){
        return tagService.addTag(tagListDto);
    }

    @DeleteMapping("/{id}")
    public ResponseResult deleteTag(@PathVariable Long id){
        return tagService.deleteTag(id);
    }

    @GetMapping("/{id}")
    public ResponseResult getUpdateInfoById(@PathVariable Long id){
        return tagService.getUpdateInfoById(id);
    }

    @PutMapping
    public ResponseResult updateTag(@RequestBody TagVo tagVo){
        return tagService.updateTag(tagVo);
    }

    @GetMapping("/listAllTag")
    public ResponseResult getAllTagList(){
       return tagService.getAllTagList();
    }
}