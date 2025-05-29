package com.unilife.service;

import com.unilife.common.result.Result;
import com.unilife.mapper.ResourceMapper;
import com.unilife.mapper.UserMapper;
import com.unilife.mapper.CategoryMapper;
import com.unilife.model.dto.CreateResourceDTO;
import com.unilife.model.entity.Resource;
import com.unilife.model.entity.User;
import com.unilife.model.entity.Category;
import com.unilife.service.impl.ResourceServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
class ResourceServiceTest {

    @Mock
    private ResourceMapper resourceMapper;

    @Mock
    private UserMapper userMapper;

    @Mock
    private CategoryMapper categoryMapper;

    @InjectMocks
    private ResourceServiceImpl resourceService;

    private User testUser;
    private Category testCategory;
    private Resource testResource;
    private CreateResourceDTO createResourceDTO;
    private MockMultipartFile mockFile;

    @BeforeEach
    void setUp() {
        // 初始化测试数据
        testUser = new User();
        testUser.setId(1L);
        testUser.setNickname("测试用户");
        testUser.setAvatar("avatar.jpg");

        testCategory = new Category();
        testCategory.setId(1L);
        testCategory.setName("学习资料");
        testCategory.setStatus(1);

        testResource = new Resource();
        testResource.setId(1L);
        testResource.setTitle("测试资源");
        testResource.setDescription("测试资源描述");
        testResource.setFileName("test.pdf");
        testResource.setFileUrl("http://example.com/test.pdf");
        testResource.setFileSize(1024L);
        testResource.setFileType("pdf");
        testResource.setUserId(1L);
        testResource.setCategoryId(1L);
        testResource.setDownloadCount(0);
        testResource.setLikeCount(0);
        testResource.setCreatedAt(LocalDateTime.now());
        testResource.setUpdatedAt(LocalDateTime.now());

        createResourceDTO = new CreateResourceDTO();
        createResourceDTO.setTitle("新资源标题");
        createResourceDTO.setDescription("新资源描述");
        createResourceDTO.setCategoryId(1L);

        mockFile = new MockMultipartFile(
            "file", 
            "test.pdf", 
            "application/pdf", 
            "test content".getBytes()
        );
    }

    @Test
    void testUploadResource_Success() {
        // Mock 依赖方法
        when(userMapper.findById(1L)).thenReturn(testUser);
        when(categoryMapper.findById(1L)).thenReturn(testCategory);
        when(resourceMapper.insert(any(Resource.class))).thenReturn(1);

        // 执行测试
        Result<?> result = resourceService.uploadResource(1L, createResourceDTO, mockFile);

        // 验证结果
        assertTrue(result.isSuccess());
        assertEquals("资源上传成功", result.getMessage());

        // 验证方法调用
        verify(userMapper).findById(1L);
        verify(categoryMapper).findById(1L);
        verify(resourceMapper).insert(any(Resource.class));
    }

    @Test
    void testUploadResource_UserNotFound() {
        // Mock 用户不存在
        when(userMapper.findById(1L)).thenReturn(null);

        // 执行测试
        Result<?> result = resourceService.uploadResource(1L, createResourceDTO, mockFile);

        // 验证结果
        assertFalse(result.isSuccess());
        assertEquals(404, result.getCode());
        assertEquals("用户不存在", result.getMessage());

        // 验证不会尝试上传资源
        verify(resourceMapper, never()).insert(any(Resource.class));
    }

    @Test
    void testUploadResource_CategoryNotFound() {
        // Mock 用户存在但分类不存在
        when(userMapper.findById(1L)).thenReturn(testUser);
        when(categoryMapper.findById(1L)).thenReturn(null);

        // 执行测试
        Result<?> result = resourceService.uploadResource(1L, createResourceDTO, mockFile);

        // 验证结果
        assertFalse(result.isSuccess());
        assertEquals(404, result.getCode());
        assertEquals("分类不存在", result.getMessage());
    }

    @Test
    void testUploadResource_EmptyFile() {
        // 测试空文件
        MockMultipartFile emptyFile = new MockMultipartFile(
            "file", 
            "empty.pdf", 
            "application/pdf", 
            new byte[0]
        );

        // 执行测试
        Result<?> result = resourceService.uploadResource(1L, createResourceDTO, emptyFile);

        // 验证结果
        assertFalse(result.isSuccess());
        assertEquals(400, result.getCode());
        assertEquals("文件不能为空", result.getMessage());
    }

    @Test
    void testUploadResource_InvalidFileType() {
        // 测试不支持的文件类型
        MockMultipartFile invalidFile = new MockMultipartFile(
            "file", 
            "test.exe", 
            "application/octet-stream", 
            "test content".getBytes()
        );

        // 执行测试
        Result<?> result = resourceService.uploadResource(1L, createResourceDTO, invalidFile);

        // 验证结果
        assertFalse(result.isSuccess());
        assertEquals(400, result.getCode());
        assertTrue(result.getMessage().contains("不支持的文件类型"));
    }

    @Test
    void testGetResourceDetail_Success() {
        // Mock 依赖方法
        when(resourceMapper.findById(1L)).thenReturn(testResource);
        when(userMapper.findById(1L)).thenReturn(testUser);
        when(categoryMapper.findById(1L)).thenReturn(testCategory);

        // 执行测试
        Result<?> result = resourceService.getResourceDetail(1L, 1L);

        // 验证结果
        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
    }

    @Test
    void testGetResourceDetail_ResourceNotFound() {
        // Mock 资源不存在
        when(resourceMapper.findById(1L)).thenReturn(null);

        // 执行测试
        Result<?> result = resourceService.getResourceDetail(1L, 1L);

        // 验证结果
        assertFalse(result.isSuccess());
        assertEquals(404, result.getCode());
        assertEquals("资源不存在", result.getMessage());
    }

    @Test
    void testGetResourceList_Success() {
        // Mock 资源列表
        List<Resource> resources = Arrays.asList(testResource);
        when(resourceMapper.findByConditions(any(), any(), any(), anyInt(), anyInt())).thenReturn(resources);
        when(resourceMapper.countByConditions(any(), any(), any())).thenReturn(1);

        // 执行测试
        Result<?> result = resourceService.getResourceList(1L, 1L, "测试", 1, 10, 1L);

        // 验证结果
        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
    }

    @Test
    void testUpdateResource_Success() {
        // Mock 依赖方法
        when(resourceMapper.findById(1L)).thenReturn(testResource);
        when(categoryMapper.findById(1L)).thenReturn(testCategory);
        when(resourceMapper.update(any(Resource.class))).thenReturn(1);

        // 执行测试
        Result<?> result = resourceService.updateResource(1L, 1L, createResourceDTO);

        // 验证结果
        assertTrue(result.isSuccess());
        assertEquals("资源更新成功", result.getMessage());

        // 验证方法调用
        verify(resourceMapper).update(any(Resource.class));
    }

    @Test
    void testUpdateResource_Unauthorized() {
        // Mock 其他用户的资源
        testResource.setUserId(2L);
        when(resourceMapper.findById(1L)).thenReturn(testResource);

        // 执行测试
        Result<?> result = resourceService.updateResource(1L, 1L, createResourceDTO);

        // 验证结果
        assertFalse(result.isSuccess());
        assertEquals(403, result.getCode());
        assertEquals("无权限修改此资源", result.getMessage());
    }

    @Test
    void testDeleteResource_Success() {
        // Mock 依赖方法
        when(resourceMapper.findById(1L)).thenReturn(testResource);
        when(resourceMapper.delete(1L)).thenReturn(1);

        // 执行测试
        Result<?> result = resourceService.deleteResource(1L, 1L);

        // 验证结果
        assertTrue(result.isSuccess());
        assertEquals("资源删除成功", result.getMessage());

        // 验证方法调用
        verify(resourceMapper).delete(1L);
    }

    @Test
    void testDownloadResource_Success() {
        // Mock 依赖方法
        when(resourceMapper.findById(1L)).thenReturn(testResource);

        // 执行测试
        Result<?> result = resourceService.downloadResource(1L, 1L);

        // 验证结果
        assertTrue(result.isSuccess());
        assertNotNull(result.getData());

        // 验证下载量增加
        verify(resourceMapper).updateDownloadCount(1L);
    }

    @Test
    void testLikeResource_Success() {
        // Mock 依赖方法
        when(resourceMapper.findById(1L)).thenReturn(testResource);
        when(resourceMapper.isLikedByUser(1L, 1L)).thenReturn(false);
        when(resourceMapper.insertLike(1L, 1L)).thenReturn(1);

        // 执行测试
        Result<?> result = resourceService.likeResource(1L, 1L);

        // 验证结果
        assertTrue(result.isSuccess());
        assertEquals("点赞成功", result.getMessage());

        // 验证方法调用
        verify(resourceMapper).insertLike(1L, 1L);
        verify(resourceMapper).updateLikeCount(1L, 1);
    }

    @Test
    void testUnlikeResource_Success() {
        // Mock 已点赞状态
        when(resourceMapper.findById(1L)).thenReturn(testResource);
        when(resourceMapper.isLikedByUser(1L, 1L)).thenReturn(true);
        when(resourceMapper.deleteLike(1L, 1L)).thenReturn(1);

        // 执行测试
        Result<?> result = resourceService.likeResource(1L, 1L);

        // 验证结果
        assertTrue(result.isSuccess());
        assertEquals("取消点赞成功", result.getMessage());

        // 验证方法调用
        verify(resourceMapper).deleteLike(1L, 1L);
        verify(resourceMapper).updateLikeCount(1L, -1);
    }

    @Test
    void testGetUserResources_Success() {
        // Mock 用户资源列表
        List<Resource> userResources = Arrays.asList(testResource);
        when(resourceMapper.findByUserId(eq(1L), anyInt(), anyInt())).thenReturn(userResources);
        when(resourceMapper.countByUserId(1L)).thenReturn(1);

        // 执行测试
        Result<?> result = resourceService.getUserResources(1L, 1, 10);

        // 验证结果
        assertTrue(result.isSuccess());
        assertNotNull(result.getData());

        // 验证方法调用
        verify(resourceMapper).findByUserId(eq(1L), anyInt(), anyInt());
        verify(resourceMapper).countByUserId(1L);
    }
} 