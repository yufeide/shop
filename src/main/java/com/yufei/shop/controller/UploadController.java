package com.yufei.shop.controller;


import com.yufei.shop.entity.Result;
import com.yufei.shop.util.OSSUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.text.SimpleDateFormat;
import java.util.Date;

@Api(tags = "文件上传接口",description = "默认上传图片")
@RestController
@RequestMapping("/upload")
public class UploadController {

    @Autowired
    private OSSUtil ossUtil;

    @Value("${aliyun.oss.folder}")
    private String ossFolder;

    @PostMapping("/img")
    @ApiOperation(value = "图片上传接口")
    public Result<String> uploadImg(@RequestParam("file") MultipartFile file) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
            String dateStr = sdf.format(new Date());
            String folder = ossFolder + "/" + dateStr;

            String fileUrl = ossUtil.uploadFile(file, folder);
            return Result.success("上传成功", fileUrl);
        } catch (Exception e) {
            return Result.fail(e.getMessage());
        }
    }

}
