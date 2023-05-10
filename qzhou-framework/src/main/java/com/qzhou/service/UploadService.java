package com.qzhou.service;

import com.qzhou.domain.ResponseResult;
import org.springframework.web.multipart.MultipartFile;

public interface UploadService {
    /**
     * 上传文件
     * @param img
     * @return
     */
    ResponseResult uploadImg(MultipartFile img);
}
