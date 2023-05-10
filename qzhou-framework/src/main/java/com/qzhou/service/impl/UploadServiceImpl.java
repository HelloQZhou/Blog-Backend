package com.qzhou.service.impl;

import com.google.gson.Gson;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;
import com.qzhou.domain.ResponseResult;
import com.qzhou.enums.AppHttpCodeEnum;
import com.qzhou.exception.SystemException;
import com.qzhou.service.UploadService;
import com.qzhou.utils.PathUtils;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

@Service
@Data
@ConfigurationProperties(prefix = "oss")
public class UploadServiceImpl implements UploadService {
    private String accessKey;
    private String secretKey;
    private String bucket;
    private String testRegion;

    @Override
    public ResponseResult uploadImg(MultipartFile img) {
        //TODO 判断文件类型

        //获取原始文件名
        String originalFilename = img.getOriginalFilename();
        //对原始文件名进行判断
        if(!(originalFilename.endsWith(".png")||originalFilename.endsWith(".jpg"))){
            throw new SystemException(AppHttpCodeEnum.FILE_TYPE_ERROR);
        }
        //如果是，文件则上传到oss
        String filePath= PathUtils.generateFilePath(originalFilename);
        return ResponseResult.okResult(uploadOss(img,filePath));
    }


    public String uploadOss(MultipartFile imgFile, String filePath) {
        //构造一个带指定 Region 对象的配置类
        Configuration cfg = new Configuration(Region.autoRegion());

        UploadManager uploadManager = new UploadManager(cfg);

        //指定文件名（路径），默认不指定key的情况下，以文件内容的hash值作为文件名
        String key = filePath;

        try {
            //此时路径不能有中文
            //InputStream byteInputStream =new FileInputStream("C:\\Users\\zqqq\\Desktop\\Miko fox.png");
            InputStream byteInputStream =imgFile.getInputStream();
            Auth auth = Auth.create(accessKey, secretKey);
            String upToken = auth.uploadToken(bucket);

            try {
                Response response = uploadManager.put(byteInputStream,key,upToken,null, null);
                //解析上传成功的结果
                DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
                System.out.println(putRet.key);
                System.out.println(putRet.hash);
                return testRegion+key;
            } catch (QiniuException ex) {
                Response r = ex.response;
                System.err.println(r.toString());
                try {
                    System.err.println(r.bodyString());
                } catch (QiniuException ex2) {
                    //ignore
                }
            }
        } catch (Exception ex) {
            //ignore
        }
        return testRegion+key;
    }

}
