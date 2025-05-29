package com.unilife.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.unilife.common.result.Result;
import com.unilife.model.dto.CreatePostDTO;
import com.unilife.model.dto.UpdatePostDTO;
import com.unilife.service.PostService;
import com.unilife.utils.BaseContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PostController.class)
class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PostService postService;

    @Autowired
    private ObjectMapper objectMapper;

    private CreatePostDTO createPostDTO;
    private UpdatePostDTO updatePostDTO;

    @BeforeEach
    void setUp() {
        createPostDTO = new CreatePostDTO();
        createPostDTO.setTitle("测试帖子");
        createPostDTO.setContent("测试内容");
        createPostDTO.setCategoryId(1L);

        updatePostDTO = new UpdatePostDTO();
        updatePostDTO.setTitle("更新标题");
        updatePostDTO.setContent("更新内容");
        updatePostDTO.setCategoryId(1L);
    }

    @Test
    void testCreatePost_Success() throws Exception {
        // Mock用户已登录
        try (var mockedStatic = mockStatic(BaseContext.class)) {
            mockedStatic.when(BaseContext::getId).thenReturn(1L);
            
            when(postService.createPost(eq(1L), any(CreatePostDTO.class)))
                .thenReturn(Result.success("帖子发布成功"));

            mockMvc.perform(post("/posts")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(createPostDTO)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.message").value("帖子发布成功"));

            verify(postService).createPost(eq(1L), any(CreatePostDTO.class));
        }
    }

    @Test
    void testCreatePost_Unauthorized() throws Exception {
        // Mock用户未登录
        try (var mockedStatic = mockStatic(BaseContext.class)) {
            mockedStatic.when(BaseContext::getId).thenReturn(null);

            mockMvc.perform(post("/posts")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(createPostDTO)))
                    .andExpect(status().isOk())
                    .andExpected(jsonPath("$.success").value(false))
                    .andExpected(jsonPath("$.code").value(401))
                    .andExpected(jsonPath("$.message").value("未登录"));

            verify(postService, never()).createPost(anyLong(), any(CreatePostDTO.class));
        }
    }

    @Test
    void testGetPostDetail_Success() throws Exception {
        when(postService.getPostDetail(eq(1L), any()))
            .thenReturn(Result.success("帖子详情"));

        mockMvc.perform(get("/posts/1"))
                .andExpect(status().isOk())
                .andExpected(jsonPath("$.success").value(true));

        verify(postService).getPostDetail(eq(1L), any());
    }

    @Test
    void testGetPostList_Success() throws Exception {
        when(postService.getPostList(any(), any(), anyInt(), anyInt(), any(), any()))
            .thenReturn(Result.success("帖子列表"));

        mockMvc.perform(get("/posts")
                .param("categoryId", "1")
                .param("keyword", "测试")
                .param("page", "1")
                .param("size", "10")
                .param("sort", "latest"))
                .andExpected(status().isOk())
                .andExpected(jsonPath("$.success").value(true));

        verify(postService).getPostList(eq(1L), eq("测试"), eq(1), eq(10), eq("latest"), any());
    }

    @Test
    void testUpdatePost_Success() throws Exception {
        try (var mockedStatic = mockStatic(BaseContext.class)) {
            mockedStatic.when(BaseContext::getId).thenReturn(1L);
            
            when(postService.updatePost(eq(1L), eq(1L), any(UpdatePostDTO.class)))
                .thenReturn(Result.success("帖子更新成功"));

            mockMvc.perform(put("/posts/1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(updatePostDTO)))
                    .andExpected(status().isOk())
                    .andExpected(jsonPath("$.success").value(true))
                    .andExpected(jsonPath("$.message").value("帖子更新成功"));

            verify(postService).updatePost(eq(1L), eq(1L), any(UpdatePostDTO.class));
        }
    }

    @Test
    void testDeletePost_Success() throws Exception {
        try (var mockedStatic = mockStatic(BaseContext.class)) {
            mockedStatic.when(BaseContext::getId).thenReturn(1L);
            
            when(postService.deletePost(eq(1L), eq(1L)))
                .thenReturn(Result.success("帖子删除成功"));

            mockMvc.perform(delete("/posts/1"))
                    .andExpected(status().isOk())
                    .andExpected(jsonPath("$.success").value(true))
                    .andExpected(jsonPath("$.message").value("帖子删除成功"));

            verify(postService).deletePost(eq(1L), eq(1L));
        }
    }

    @Test
    void testLikePost_Success() throws Exception {
        try (var mockedStatic = mockStatic(BaseContext.class)) {
            mockedStatic.when(BaseContext::getId).thenReturn(1L);
            
            when(postService.likePost(eq(1L), eq(1L)))
                .thenReturn(Result.success("点赞成功"));

            mockMvc.perform(post("/posts/1/like"))
                    .andExpected(status().isOk())
                    .andExpected(jsonPath("$.success").value(true))
                    .andExpected(jsonPath("$.message").value("点赞成功"));

            verify(postService).likePost(eq(1L), eq(1L));
        }
    }
} 