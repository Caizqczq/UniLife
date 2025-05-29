package com.unilife.service;

import com.unilife.common.result.Result;
import com.unilife.mapper.PostMapper;
import com.unilife.mapper.UserMapper;
import com.unilife.mapper.CategoryMapper;
import com.unilife.model.dto.CreatePostDTO;
import com.unilife.model.dto.UpdatePostDTO;
import com.unilife.model.entity.Post;
import com.unilife.model.entity.User;
import com.unilife.model.entity.Category;
import com.unilife.service.impl.PostServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
class PostServiceTest {

    @Mock
    private PostMapper postMapper;

    @Mock
    private UserMapper userMapper;

    @Mock
    private CategoryMapper categoryMapper;

    @InjectMocks
    private PostServiceImpl postService;

    private User testUser;
    private Category testCategory;
    private Post testPost;
    private CreatePostDTO createPostDTO;
    private UpdatePostDTO updatePostDTO;

    @BeforeEach
    void setUp() {
        // 初始化测试数据
        testUser = new User();
        testUser.setId(1L);
        testUser.setNickname("测试用户");
        testUser.setAvatar("avatar.jpg");

        testCategory = new Category();
        testCategory.setId(1L);
        testCategory.setName("学习讨论");
        testCategory.setStatus(1);

        testPost = new Post();
        testPost.setId(1L);
        testPost.setTitle("测试帖子");
        testPost.setContent("这是一个测试帖子的内容");
        testPost.setUserId(1L);
        testPost.setCategoryId(1L);
        testPost.setLikeCount(0);
        testPost.setViewCount(0);
        testPost.setCommentCount(0);
        testPost.setCreatedAt(LocalDateTime.now());
        testPost.setUpdatedAt(LocalDateTime.now());

        createPostDTO = new CreatePostDTO();
        createPostDTO.setTitle("新帖子标题");
        createPostDTO.setContent("新帖子内容");
        createPostDTO.setCategoryId(1L);

        updatePostDTO = new UpdatePostDTO();
        updatePostDTO.setTitle("更新后的标题");
        updatePostDTO.setContent("更新后的内容");
        updatePostDTO.setCategoryId(1L);
    }

    @Test
    void testCreatePost_Success() {
        // Mock 依赖方法
        when(userMapper.findById(1L)).thenReturn(testUser);
        when(categoryMapper.findById(1L)).thenReturn(testCategory);
        when(postMapper.insert(any(Post.class))).thenReturn(1);

        // 执行测试
        Result<?> result = postService.createPost(1L, createPostDTO);

        // 验证结果
        assertTrue(result.isSuccess());
        assertEquals("帖子发布成功", result.getMessage());

        // 验证方法调用
        verify(userMapper).findById(1L);
        verify(categoryMapper).findById(1L);
        verify(postMapper).insert(any(Post.class));
    }

    @Test
    void testCreatePost_UserNotFound() {
        // Mock 用户不存在
        when(userMapper.findById(1L)).thenReturn(null);

        // 执行测试
        Result<?> result = postService.createPost(1L, createPostDTO);

        // 验证结果
        assertFalse(result.isSuccess());
        assertEquals(404, result.getCode());
        assertEquals("用户不存在", result.getMessage());

        // 验证不会尝试创建帖子
        verify(postMapper, never()).insert(any(Post.class));
    }

    @Test
    void testCreatePost_CategoryNotFound() {
        // Mock 用户存在但分类不存在
        when(userMapper.findById(1L)).thenReturn(testUser);
        when(categoryMapper.findById(1L)).thenReturn(null);

        // 执行测试
        Result<?> result = postService.createPost(1L, createPostDTO);

        // 验证结果
        assertFalse(result.isSuccess());
        assertEquals(404, result.getCode());
        assertEquals("分类不存在", result.getMessage());
    }

    @Test
    void testCreatePost_InvalidTitle() {
        // 测试空标题
        createPostDTO.setTitle("");
        
        // 执行测试
        Result<?> result = postService.createPost(1L, createPostDTO);

        // 验证结果
        assertFalse(result.isSuccess());
        assertEquals(400, result.getCode());
        assertTrue(result.getMessage().contains("标题不能为空"));
    }

    @Test
    void testGetPostDetail_Success() {
        // Mock 依赖方法
        when(postMapper.findById(1L)).thenReturn(testPost);
        when(userMapper.findById(1L)).thenReturn(testUser);
        when(categoryMapper.findById(1L)).thenReturn(testCategory);

        // 执行测试
        Result<?> result = postService.getPostDetail(1L, 1L);

        // 验证结果
        assertTrue(result.isSuccess());
        assertNotNull(result.getData());

        // 验证浏览量增加
        verify(postMapper).updateViewCount(1L);
    }

    @Test
    void testGetPostDetail_PostNotFound() {
        // Mock 帖子不存在
        when(postMapper.findById(1L)).thenReturn(null);

        // 执行测试
        Result<?> result = postService.getPostDetail(1L, 1L);

        // 验证结果
        assertFalse(result.isSuccess());
        assertEquals(404, result.getCode());
        assertEquals("帖子不存在", result.getMessage());
    }

    @Test
    void testGetPostList_Success() {
        // Mock 帖子列表
        List<Post> posts = Arrays.asList(testPost);
        when(postMapper.findByConditions(any(), any(), anyInt(), anyInt(), any())).thenReturn(posts);
        when(postMapper.countByConditions(any(), any())).thenReturn(1);

        // 执行测试
        Result<?> result = postService.getPostList(1L, "测试", 1, 10, "latest", 1L);

        // 验证结果
        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
    }

    @Test
    void testUpdatePost_Success() {
        // Mock 依赖方法
        when(postMapper.findById(1L)).thenReturn(testPost);
        when(categoryMapper.findById(1L)).thenReturn(testCategory);
        when(postMapper.update(any(Post.class))).thenReturn(1);

        // 执行测试
        Result<?> result = postService.updatePost(1L, 1L, updatePostDTO);

        // 验证结果
        assertTrue(result.isSuccess());
        assertEquals("帖子更新成功", result.getMessage());

        // 验证方法调用
        verify(postMapper).update(any(Post.class));
    }

    @Test
    void testUpdatePost_Unauthorized() {
        // Mock 其他用户的帖子
        testPost.setUserId(2L);
        when(postMapper.findById(1L)).thenReturn(testPost);

        // 执行测试
        Result<?> result = postService.updatePost(1L, 1L, updatePostDTO);

        // 验证结果
        assertFalse(result.isSuccess());
        assertEquals(403, result.getCode());
        assertEquals("无权限修改此帖子", result.getMessage());
    }

    @Test
    void testDeletePost_Success() {
        // Mock 依赖方法
        when(postMapper.findById(1L)).thenReturn(testPost);
        when(postMapper.delete(1L)).thenReturn(1);

        // 执行测试
        Result<?> result = postService.deletePost(1L, 1L);

        // 验证结果
        assertTrue(result.isSuccess());
        assertEquals("帖子删除成功", result.getMessage());

        // 验证方法调用
        verify(postMapper).delete(1L);
    }

    @Test
    void testLikePost_Success() {
        // Mock 依赖方法
        when(postMapper.findById(1L)).thenReturn(testPost);
        when(postMapper.isLikedByUser(1L, 1L)).thenReturn(false);
        when(postMapper.insertLike(1L, 1L)).thenReturn(1);

        // 执行测试
        Result<?> result = postService.likePost(1L, 1L);

        // 验证结果
        assertTrue(result.isSuccess());
        assertEquals("点赞成功", result.getMessage());

        // 验证方法调用
        verify(postMapper).insertLike(1L, 1L);
        verify(postMapper).updateLikeCount(1L, 1);
    }

    @Test
    void testUnlikePost_Success() {
        // Mock 已点赞状态
        when(postMapper.findById(1L)).thenReturn(testPost);
        when(postMapper.isLikedByUser(1L, 1L)).thenReturn(true);
        when(postMapper.deleteLike(1L, 1L)).thenReturn(1);

        // 执行测试
        Result<?> result = postService.likePost(1L, 1L);

        // 验证结果
        assertTrue(result.isSuccess());
        assertEquals("取消点赞成功", result.getMessage());

        // 验证方法调用
        verify(postMapper).deleteLike(1L, 1L);
        verify(postMapper).updateLikeCount(1L, -1);
    }
} 