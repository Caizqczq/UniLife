package com.unilife.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.unilife.common.result.Result;
import com.unilife.mapper.CategoryMapper;
import com.unilife.mapper.ResourceMapper;
import com.unilife.mapper.ResourceLikeMapper;
import com.unilife.mapper.UserMapper;
import com.unilife.model.dto.CreateResourceDTO;
import com.unilife.model.entity.Category;
import com.unilife.model.entity.Resource;
import com.unilife.model.entity.User;
import com.unilife.model.vo.ResourceVO;
import com.unilife.service.ResourceService;
import com.unilife.utils.OssService;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;


import java.util.*;
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
    
    @Autowired
    private OssService ossService;

    @Autowired
    private ResourceLikeMapper resourceLikeMapper;



    @Autowired
    private PdfVectorAsyncService pdfVectorAsyncService;

    // 文件存储路径，实际项目中应该配置在application.yml中
    private static final String UPLOAD_DIR = "uploads/resources/";
    // OSS存储目录
    private static final String OSS_DIR = "resources";

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
            // 将文件上传到阿里云OSS
            String fileUrl = ossService.uploadFile(file, OSS_DIR);
            
            // 创建资源记录
            Resource resource = new Resource();
            resource.setUserId(userId);
            resource.setTitle(createResourceDTO.getTitle());
            resource.setDescription(createResourceDTO.getDescription());
            resource.setFileUrl(fileUrl); // 存储OSS文件URL
            resource.setFileSize(file.getSize());
            resource.setFileType(file.getContentType());
            resource.setCategoryId(createResourceDTO.getCategoryId());
            resource.setDownloadCount(0);
            resource.setLikeCount(0);
            resource.setStatus((byte) 1);

            // 保存资源记录
            resourceMapper.insert(resource);

            // 异步处理PDF文件的向量存储
            if ("application/pdf".equals(file.getContentType())) {
                try {
                    // 先读取文件内容到字节数组，避免异步处理时临时文件被删除
                    byte[] fileBytes = file.getBytes();
                    pdfVectorAsyncService.processPdfVectorAsync(fileBytes, file.getOriginalFilename(), resource);
                    log.info("PDF文件已提交异步向量化处理，资源ID: {}", resource.getId());
                } catch (Exception e) {
                    log.error("读取PDF文件内容失败，跳过向量化处理，资源ID: {}", resource.getId(), e);
                }
            }

            Map<String, Object> data = new HashMap<>();
            data.put("resourceId", resource.getId());

            return Result.success(data, "资源上传成功");
        } catch (Exception e) {
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
                .fileUrl(resource.getFileUrl()) // 直接返回OSS URL
                .fileSize(resource.getFileSize())
                .fileType(resource.getFileType())
                .userId(resource.getUserId())
                .nickname(user != null ? user.getNickname() : "未知用户")
                .avatar(user != null ? user.getAvatar() : null)
                .categoryId(resource.getCategoryId())
                .categoryName(category != null ? category.getName() : "未知分类")
                .downloadCount(resource.getDownloadCount())
                .likeCount(resource.getLikeCount())
                .isLiked(userId != null ? resourceLikeMapper.isLiked(resourceId, userId) : false) // 查询用户是否点赞
                .createdAt(resource.getCreatedAt())
                .updatedAt(resource.getUpdatedAt())
                .build();

        return Result.success(resourceVO);
    }

    @Override
    public Result getResourceList(Long categoryId, Long uploaderUserId, String keyword, Integer page, Integer size, Long currentUserId) {
        // 参数校验
        if (page == null || page < 1) page = 1;
        if (size == null || size < 1 || size > 50) size = 10;

        // 分页查询
        PageHelper.startPage(page, size);
        List<Resource> resources = resourceMapper.getList(categoryId, uploaderUserId, keyword);
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
                    .isLiked(currentUserId != null ? resourceLikeMapper.isLiked(resource.getId(), currentUserId) : false) // 查询当前用户是否点赞
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

        // 先启动异步删除向量库中的相关文档（仅针对PDF文件）
        // 在删除数据库记录之前启动，确保异步方法能获取到资源信息
        if ("application/pdf".equals(resource.getFileType())) {
            pdfVectorAsyncService.deleteVectorDocumentsAsync(resourceId, resource.getTitle());
            log.info("PDF文件已提交异步删除向量文档，资源ID: {}", resourceId);
        }

        // 删除OSS中的文件
        try {
            String fileUrl = resource.getFileUrl();
            if (fileUrl != null && fileUrl.startsWith("http")) {
                ossService.deleteFile(fileUrl);
            }
        } catch (Exception e) {
            log.error("删除OSS文件失败", e);
            // 继续执行，不影响数据库记录的删除
        }

        // 最后删除资源（逻辑删除）
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
        
        // 处理文件URL，生成临时访问链接
        String fileUrl = resource.getFileUrl();
        // 提取对象名称并生成临时访问URL（有效期1小时）
        String objectName = ossService.getObjectNameFromUrl(fileUrl);
        if (objectName != null) {
            // 生成有效期为1小时的临时访问URL
            fileUrl = ossService.generatePresignedUrl(objectName, 3600 * 1000);
        }

        // 返回文件URL和文件名
        Map<String, Object> data = new HashMap<>();
        data.put("fileUrl", fileUrl);
        data.put("fileName", resource.getTitle() + getFileExtension(fileUrl));
        data.put("fileType", resource.getFileType());

        return Result.success(data, "获取下载链接成功");
    }
    
    // 获取文件扩展名
    private String getFileExtension(String filePath) {
        int lastDot = filePath.lastIndexOf(".");
        if (lastDot > 0) {
            return filePath.substring(lastDot);
        }
        return "";
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
        boolean isLiked = resourceLikeMapper.isLiked(resourceId, userId);

        if (isLiked) {
            // 取消点赞
            resourceLikeMapper.delete(resourceId, userId);
            resourceMapper.decrementLikeCount(resourceId);
            return Result.success(null, "取消点赞成功");
        } else {
            // 添加点赞
            resourceLikeMapper.insert(resourceId, userId);
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