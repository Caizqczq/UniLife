package com.unilife.service;

import com.unilife.common.result.Result;
import com.unilife.model.dto.CreateResourceDTO;
import org.springframework.web.multipart.MultipartFile;

/**
 * 资源服务接口
 */
public interface ResourceService {
    /**
     * 上传资源
     * @param userId 用户ID
     * @param createResourceDTO 创建资源DTO
     * @param file 上传的文件
     * @return 结果
     */
    Result uploadResource(Long userId, CreateResourceDTO createResourceDTO, MultipartFile file);
    
    /**
     * 获取资源详情
     * @param resourceId 资源ID
     * @param userId 当前用户ID，可为null
     * @return 结果
     */
    Result getResourceDetail(Long resourceId, Long userId);
    
    /**
     * 获取资源列表
     * @param categoryId 分类ID，可为null
     * @param userId 用户ID，可为null
     * @param keyword 关键词，可为null
     * @param page 页码
     * @param size 每页大小
     * @return 结果
     */
    Result getResourceList(Long categoryId, Long userId, String keyword, Integer page, Integer size);
    
    /**
     * 更新资源
     * @param resourceId 资源ID
     * @param userId 用户ID
     * @param createResourceDTO 更新资源DTO
     * @return 结果
     */
    Result updateResource(Long resourceId, Long userId, CreateResourceDTO createResourceDTO);
    
    /**
     * 删除资源
     * @param resourceId 资源ID
     * @param userId 用户ID
     * @return 结果
     */
    Result deleteResource(Long resourceId, Long userId);
    
    /**
     * 下载资源
     * @param resourceId 资源ID
     * @param userId 用户ID，可为null
     * @return 结果
     */
    Result downloadResource(Long resourceId, Long userId);
    
    /**
     * 点赞/取消点赞资源
     * @param resourceId 资源ID
     * @param userId 用户ID
     * @return 结果
     */
    Result likeResource(Long resourceId, Long userId);
    
    /**
     * 获取用户上传的资源列表
     * @param userId 用户ID
     * @param page 页码
     * @param size 每页大小
     * @return 结果
     */
    Result getUserResources(Long userId, Integer page, Integer size);
}