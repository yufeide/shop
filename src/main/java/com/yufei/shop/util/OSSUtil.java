package com.yufei.shop.util;

import com.aliyun.oss.OSS;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyun.oss.model.PutObjectRequest;
import com.yufei.shop.config.OSSConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

@Component
@Slf4j
public class OSSUtil {

    @Autowired
    private OSS ossClient;

    @Autowired
    private OSSConfig ossConfig;

    private static final String[] ALLOWED_EXTENSIONS = {".jpg", ".jpeg", ".png", ".webp", ".gif", ".bmp"};
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;

    public String uploadFile(MultipartFile file, String folder) {
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("文件不能为空");
        }

        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase();

        if (!isAllowedExtension(extension)) {
            throw new RuntimeException("只支持 jpg、jpeg、png、webp、gif、bmp 格式的图片");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new RuntimeException("文件大小不能超过10MB");
        }

        try {
            String fileName = generateFileName(extension);
            String objectName = folder + "/" + fileName;

            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.getSize());
            metadata.setContentType(file.getContentType());

            InputStream inputStream = file.getInputStream();
            PutObjectRequest putObjectRequest = new PutObjectRequest(ossConfig.getBucketName(), objectName, inputStream, metadata);

            ossClient.putObject(putObjectRequest);

            String fileUrl = ossConfig.getUrlPrefix() + "/" + objectName;
            log.info("文件上传成功: {}", fileUrl);
            return fileUrl;

        } catch (IOException e) {
            log.error("文件上传失败", e);
            throw new RuntimeException("文件上传失败: " + e.getMessage());
        }
    }

    public boolean deleteFile(String fileUrl) {
        try {
            String objectName = fileUrl.replace(ossConfig.getUrlPrefix() + "/", "");
            ossClient.deleteObject(ossConfig.getBucketName(), objectName);
            log.info("文件删除成功: {}", fileUrl);
            return true;
        } catch (Exception e) {
            log.error("文件删除失败: {}", fileUrl, e);
            return false;
        }
    }

    private boolean isAllowedExtension(String extension) {
        for (String allowed : ALLOWED_EXTENSIONS) {
            if (allowed.equalsIgnoreCase(extension)) {
                return true;
            }
        }
        return false;
    }

    private String generateFileName(String extension) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String dateStr = sdf.format(new Date());
        String uuid = UUID.randomUUID().toString().replace("-", "");
        return dateStr + "_" + uuid + extension;
    }
}
