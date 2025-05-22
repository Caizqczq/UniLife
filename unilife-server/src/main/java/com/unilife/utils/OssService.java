package com.unilife.utils;

import com.aliyun.oss.OSS;
import com.aliyun.oss.model.OSSObject;
import com.aliyun.oss.model.GeneratePresignedUrlRequest;
import com.unilife.config.OssConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

@Slf4j
@Component
public class OssService {
    
    @Autowired
    private OSS ossClient;
    
    @Autowired
    private OssConfig ossConfig;
    
    /**
     * 上传文件到OSS
     * @param file 文件
     * @param dir 存储目录
     * @return 返回文件访问URL
     */
    public String uploadFile(MultipartFile file, String dir) {
        String bucketName = ossConfig.getBucketName();
        String urlPrefix = ossConfig.getUrlPrefix();
        
        try {
            // 获取文件原始名称
            String originalFilename = file.getOriginalFilename();
            // 获取文件后缀
            String suffix = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            
            // 构建OSS存储路径：目录/日期/随机UUID.后缀
            String dateDir = new SimpleDateFormat("yyyy/MM/dd").format(new Date());
            String filename = UUID.randomUUID().toString().replaceAll("-", "") + suffix;
            String objectName = dir + "/" + dateDir + "/" + filename;
            
            // 获取文件输入流
            InputStream inputStream = file.getInputStream();
            
            // 上传文件到OSS
            ossClient.putObject(bucketName, objectName, inputStream);
            
            // 关闭输入流
            inputStream.close();
            
            // 返回文件访问URL
            return urlPrefix + objectName;
        } catch (IOException e) {
            log.error("上传文件到OSS失败: ", e);
            throw new RuntimeException("上传文件失败");
        }
    }
    
    /**
     * 从OSS删除文件
     * @param fileUrl 文件URL
     */
    public void deleteFile(String fileUrl) {
        String bucketName = ossConfig.getBucketName();
        String urlPrefix = ossConfig.getUrlPrefix();
        
        if (fileUrl.startsWith(urlPrefix)) {
            String objectName = fileUrl.substring(urlPrefix.length());
            ossClient.deleteObject(bucketName, objectName);
        }
    }
    
    /**
     * 获取OSS文件输入流
     * @param fileUrl 文件URL
     * @return 文件输入流
     */
    public InputStream getFileStream(String fileUrl) {
        String bucketName = ossConfig.getBucketName();
        String urlPrefix = ossConfig.getUrlPrefix();
        
        if (fileUrl.startsWith(urlPrefix)) {
            String objectName = fileUrl.substring(urlPrefix.length());
            OSSObject ossObject = ossClient.getObject(bucketName, objectName);
            return ossObject.getObjectContent();
        }
        return null;
    }
    
    /**
     * 获取文件的临时访问URL（适用于私有bucket）
     * @param objectName 对象名称
     * @param expiration 过期时间（毫秒）
     * @return 带签名的临时URL
     */
    public String generatePresignedUrl(String objectName, long expiration) {
        String bucketName = ossConfig.getBucketName();
        
        try {
            // 计算过期时间
            Date expirationDate = new Date(System.currentTimeMillis() + expiration);
            
            // 创建请求
            GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(bucketName, objectName);
            request.setExpiration(expirationDate);
            
            // 生成URL
            URL url = ossClient.generatePresignedUrl(request);
            return url.toString();
        } catch (Exception e) {
            log.error("生成临时访问URL失败: ", e);
            throw new RuntimeException("生成临时访问URL失败");
        }
    }
    
    /**
     * 从完整URL中提取对象名称
     * @param fileUrl 完整的文件URL
     * @return 对象名称
     */
    public String getObjectNameFromUrl(String fileUrl) {
        String urlPrefix = ossConfig.getUrlPrefix();
        if (fileUrl != null && fileUrl.startsWith(urlPrefix)) {
            return fileUrl.substring(urlPrefix.length());
        }
        return null;
    }
} 