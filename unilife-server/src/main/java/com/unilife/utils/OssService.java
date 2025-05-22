package com.unilife.utils;

import com.aliyun.oss.OSS;
import com.aliyun.oss.model.DeleteObjectRequest;
import com.aliyun.oss.model.GeneratePresignedUrlRequest;
import com.aliyun.oss.model.PutObjectRequest;
import com.unilife.config.OssConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.UUID;

@Service
@Slf4j
public class OssService {

    @Autowired
    private OSS ossClient;

    @Autowired
    private OssConfig ossConfig;

    /**
     * 上传文件到OSS
     */
    public String uploadFile(MultipartFile file) throws IOException {
        // 生成文件名
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        
        // 按日期组织文件夹
        String dateFolder = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String fileName = dateFolder + "/" + UUID.randomUUID().toString() + extension;
        
        try (InputStream inputStream = file.getInputStream()) {
            // 创建上传请求
            PutObjectRequest putObjectRequest = new PutObjectRequest(
                ossConfig.getBucketName(), 
                fileName, 
                inputStream
            );
            
            // 执行上传
            ossClient.putObject(putObjectRequest);
            
            // 返回文件URL
            String fileUrl = ossConfig.getUrlPrefix() + fileName;
            log.info("文件上传成功: {}", fileUrl);
            return fileUrl;
        } catch (Exception e) {
            log.error("文件上传失败: {}", e.getMessage(), e);
            throw new RuntimeException("文件上传失败: " + e.getMessage());
        }
    }

    /**
     * 删除OSS文件
     */
    public void deleteFile(String fileUrl) {
        try {
            // 从URL中提取对象名称
            String objectName = getObjectNameFromUrl(fileUrl);
            if (objectName != null) {
                DeleteObjectRequest deleteObjectRequest = new DeleteObjectRequest(
                    ossConfig.getBucketName(), 
                    objectName
                );
                ossClient.deleteObject(deleteObjectRequest);
                log.info("文件删除成功: {}", fileUrl);
            }
        } catch (Exception e) {
            log.error("文件删除失败: {}", e.getMessage(), e);
        }
    }

    /**
     * 生成预签名URL（临时访问链接）
     */
    public String generatePresignedUrl(String fileUrl) {
        try {
            String objectName = getObjectNameFromUrl(fileUrl);
            if (objectName == null) {
                return fileUrl;
            }
            
            // 设置URL过期时间为1小时
            Date expiration = new Date(System.currentTimeMillis() + 3600 * 1000);
            
            GeneratePresignedUrlRequest generatePresignedUrlRequest = new GeneratePresignedUrlRequest(
                ossConfig.getBucketName(), 
                objectName
            );
            generatePresignedUrlRequest.setExpiration(expiration);
            
            URL url = ossClient.generatePresignedUrl(generatePresignedUrlRequest);
            return url.toString();
        } catch (Exception e) {
            log.error("生成预签名URL失败: {}", e.getMessage(), e);
            return fileUrl;
        }
    }

    /**
     * 从URL中提取对象名称
     */
    public String getObjectNameFromUrl(String fileUrl) {
        if (fileUrl == null || !fileUrl.startsWith(ossConfig.getUrlPrefix())) {
            return null;
        }
        return fileUrl.substring(ossConfig.getUrlPrefix().length());
    }
} 