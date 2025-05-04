package com.unilife.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.unilife.common.result.Result;
import com.unilife.mapper.CategoryMapper;
import com.unilife.mapper.ResourceMapper;
import com.unilife.mapper.UserMapper;
import com.unilife.model.dto.CreateResourceDTO;
import com.unilife.model.entity.Category;
import com.unilife.model.entity.Resource;
import com.unilife.model.entity.User;
import com.unilife.model.vo.ResourceVO;
import com.unilife.service.ResourceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ResourceServiceImpl implements ResourceService {

    @Autowired
    private ResourceMapper resourceMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    // 文件存储路径，实际项目中应该配置在application.yml中
    private static final String UPLOAD_DIR = "uploads/resources/";

    @Override
    @Transactional
    public Result uploadResource(Long userId, CreateResourceDTO createResourceDTO, MultipartFile file) {
        // 检查用户是否存在
        User user = userMapper.getUserById(userId);
        if (user == null) {
            return Result.error(404, "用户不存在");
        }

        // 检查分类是否存在
        Category category = categoryMapper.getById(createResourceDTO.getCategoryId());
        if (category == null) {
            return Result.error(404, "分类不存在");
        }

        // 检查文件是否为空
        if (file.isEmpty()) {
            return Result.error(400, "上传文件不能为空");
        }

        try {
            // 创建上传目录（如果不存在）
            File uploadDir = new File(UPLOAD_DIR);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            // 生成唯一文件名
            String originalFilename = file.getOriginalFilename();
            String fileExtension = originalFilename != null ? originalFilename.substring(originalFilename.lastIndexOf(".")) : "";
            String newFilename = UUID.randomUUID().toString() + fileExtension;
            String filePath = UPLOAD_DIR + newFilename;

            // 保存文件
            Path path = Paths.get(filePath);
            Files.write(path, file.getBytes());

            // 创建资源记录
            Resource resource = new Resource();
            resource.setUserId(userId);
            resource.setTitle(createResourceDTO.getTitle());
            resource.setDescription(createResourceDTO.getDescription());
            resource.setFileUrl(filePath);
            resource.setFileSize(file.getSize());
            resource.setFileType(file.getContentType());
            resource.setCategoryId(createResourceDTO.getCategoryId());
            resource.setDownloadCount(0);
            resource.setLikeCount(0);
            resource.setStatus((byte) 1);

            // 保存资源记录
            resourceMapper.insert(resource);

            Map<String, Object> data = new HashMap<>();
            data.put("resourceId", resource.getId());

            return Result.success(data, "资源上传成功");
        } catch (IOException e) {
            log.error("文件上传失败", e);
            return Result.error(500, "文件上传失败");
        }
    }

    @Override
    public Result getResourceDetail(Long resourceId, Long userId) {
        // 获取资源
        Resource resource = resourceMapper.getById(resourceId);
        if (resource == null) {
            return Result.error(404, "资源不存在");
        }

        // 获取上传用户信息
        User user = userMapper.getUserById(resource.getUserId());

        // 获取分类信息
        Category category = categoryMapper.getById(resource.getCategoryId());

        // 构建返回数据
        ResourceVO resourceVO = ResourceVO.builder()
                .id(resource.getId())
                .title(resource.getTitle())
                .description(resource.getDescription())
                .fileUrl(resource.getFileUrl())
                .fileSize(resource.getFileSize())
                .fileType(resource.getFileType())
                .userId(resource.getUserId())
                .nickname(user != null ? user.getNickname() : "未知用户")
                .avatar(user != null ? user.getAvatar() : null)
                .categoryId(resource.getCategoryId())
                .categoryName(category != null ? category.getName() : "未知分类")
                .downloadCount(resource.getDownloadCount())
                .likeCount(resource.getLikeCount())
                .isLiked(false) // 实际项目中应该查询用户是否点赞
                .createdAt(resource.getCreatedAt())
                .updatedAt(resource.getUpdatedAt())
                .build();

        return Result.success(resourceVO);
    }

    @Override
    public Result getResourceList(Long categoryId, Long userId, String keyword, Integer page, Integer size) {
        // 参数校验
        if (page == null || page < 1) page = 1;
        if (size == null || size < 1 || size > 50) size = 10;

        // 分页查询
        PageHelper.startPage(page, size);
        List<Resource> resources = resourceMapper.getList(categoryId, userId, keyword);
        PageInfo<Resource> pageInfo = new PageInfo<>(resources);

        // 转换为VO
        List<ResourceVO> resourceVOs = new ArrayList<>();
        for (Resource resource : resources) {
            User user = userMapper.getUserById(resource.getUserId());
            Category category = categoryMapper.getById(resource.getCategoryId());

            ResourceVO resourceVO = ResourceVO.builder()
                    .id(resource.getId())
                    .title(resource.getTitle())
                    .description(resource.getDescription())
                    .fileUrl(resource.getFileUrl())
                    .fileSize(resource.getFileSize())
                    .fileType(resource.getFileType())
                    .userId(resource.getUserId())
                    .nickname(user != null ? user.getNickname() : "未知用户")
                    .avatar(user != null ? user.getAvatar() : null)
                    .categoryId(resource.getCategoryId())
                    .categoryName(category != null ? category.getName() : "未知分类")
                    .downloadCount(resource.getDownloadCount())
                    .likeCount(resource.getLikeCount())
                    .isLiked(false) // 实际项目中应该查询用户是否点赞
                    .createdAt(resource.getCreatedAt())
                    .updatedAt(resource.getUpdatedAt())
                    .build();

            resourceVOs.add(resourceVO);
        }

        // 返回结果
        Map<String, Object> data = new HashMap<>();
        data.put("total", pageInfo.getTotal());
        data.put("list", resourceVOs);
        data.put("pages", pageInfo.getPages());

        return Result.success(data);
    }

    @Override
    @Transactional
    public Result updateResource(Long resourceId, Long userId, CreateResourceDTO createResourceDTO) {
        // 获取资源
        Resource resource = resourceMapper.getById(resourceId);
        if (resource == null) {
            return Result.error(404, "资源不存在");
        }

        // 检查是否有权限更新
        if (!resource.getUserId().equals(userId)) {
            return Result.error(403, "无权限更新此资源");
        }

        // 检查分类是否存在
        Category category = categoryMapper.getById(createResourceDTO.getCategoryId());
        if (category == null) {
            return Result.error(404, "分类不存在");
        }

        // 更新资源
        resource.setTitle(createResourceDTO.getTitle());
        resource.setDescription(createResourceDTO.getDescription());
        resource.setCategoryId(createResourceDTO.getCategoryId());

        // 保存更新
        resourceMapper.update(resource);

        return Result.success(null, "更新成功");
    }

    @Override
    @Transactional
    public Result deleteResource(Long resourceId, Long userId) {
        // 获取资源
        Resource resource = resourceMapper.getById(resourceId);
        if (resource == null) {
            return Result.error(404, "资源不存在");
        }

        // 检查是否有权限删除
        if (!resource.getUserId().equals(userId)) {
            return Result.error(403, "无权限删除此资源");
        }

        // 删除资源（逻辑删除）
        resourceMapper.delete(resourceId);

        return Result.success(null, "删除成功");
    }

    @Override
    @Transactional
    public Result downloadResource(Long resourceId, Long userId) {
        // 获取资源
        Resource resource = resourceMapper.getById(resourceId);
        if (resource == null) {
            return Result.error(404, "资源不存在");
        }

        // 增加下载次数
        resourceMapper.incrementDownloadCount(resourceId);

        // 返回文件URL
        Map<String, Object> data = new HashMap<>();
        data.put("fileUrl", resource.getFileUrl());
        data.put("fileName", resource.getTitle());
        data.put("fileType", resource.getFileType());

        return Result.success(data, "获取下载链接成功");
    }

    @Override
    @Transactional
    public Result likeResource(Long resourceId, Long userId) {
        // 获取资源
        Resource resource = resourceMapper.getById(resourceId);
        if (resource == null) {
            return Result.error(404, "资源不存在");
        }

        // 检查用户是否已点赞
        // 注意：这里需要创建一个资源点赞表和相应的Mapper，实际开发中需要先创建
        boolean isLiked = false; // resourceLikeMapper.isLiked(resourceId, userId);

        if (isLiked) {
            // 取消点赞
            // resourceLikeMapper.delete(resourceId, userId);
            resourceMapper.decrementLikeCount(resourceId);
            return Result.success(null, "取消点赞成功");
        } else {
            // 添加点赞
            // resourceLikeMapper.insert(resourceId, userId);
            resourceMapper.incrementLikeCount(resourceId);
            return Result.success(null, "点赞成功");
        }
    }

    @Override
    public Result getUserResources(Long userId, Integer page, Integer size) {
        // 参数校验
        if (page == null || page < 1) page = 1;
        if (size == null || size < 1 || size > 50) size = 10;

        // 分页查询
        PageHelper.startPage(page, size);
        List<Resource> resources = resourceMapper.getList(null, userId, null);
        PageInfo<Resource> pageInfo = new PageInfo<>(resources);

        // 转换为VO
        List<ResourceVO> resourceVOs = resources.stream().map(resource -> {
            User user = userMapper.getUserById(resource.getUserId());
            Category category = categoryMapper.getById(resource.getCategoryId());

            return ResourceVO.builder()
                    .id(resource.getId())
                    .title(resource.getTitle())
                    .description(resource.getDescription())
                    .fileUrl(resource.getFileUrl())
                    .fileSize(resource.getFileSize())
                    .fileType(resource.getFileType())
                    .userId(resource.getUserId())
                    .nickname(user != null ? user.getNickname() : "未知用户")
                    .avatar(user != null ? user.getAvatar() : null)
                    .categoryId(resource.getCategoryId())
                    .categoryName(category != null ? category.getName() : "未知分类")
                    .downloadCount(resource.getDownloadCount())
                    .likeCount(resource.getLikeCount())
                    .isLiked(false) // 实际项目中应该查询用户是否点赞
                    .createdAt(resource.getCreatedAt())
                    .updatedAt(resource.getUpdatedAt())
                    .build();
        }).collect(Collectors.toList());

        // 返回结果
        Map<String, Object> data = new HashMap<>();
        data.put("total", pageInfo.getTotal());
        data.put("list", resourceVOs);
        data.put("pages", pageInfo.getPages());

        return Result.success(data);
    }
}