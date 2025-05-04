package com.unilife.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.unilife.common.result.Result;
// import com.unilife.mapper.CategoryMapper;
import com.unilife.mapper.PostMapper;
import com.unilife.mapper.UserMapper;
import com.unilife.model.dto.CreatePostDTO;
import com.unilife.model.dto.UpdatePostDTO;
// import com.unilife.model.entity.Category;
import com.unilife.model.entity.Post;
import com.unilife.model.entity.User;
import com.unilife.model.vo.PostListVO;
import com.unilife.model.vo.PostVO;
import com.unilife.service.PostService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PostServiceImpl implements PostService {

    @Autowired
    private PostMapper postMapper;

    @Autowired
    private UserMapper userMapper;

    // @Autowired
    // private CategoryMapper categoryMapper;

    @Override
    public Result createPost(Long userId, CreatePostDTO createPostDTO) {
        // 检查用户是否存在
        User user = userMapper.getUserById(userId);
        if (user == null) {
            return Result.error(404, "用户不存在");
        }

        // 检查分类是否存在
        // 注意：这里假设已经创建了CategoryMapper接口，实际开发中需要先创建
        // Category category = categoryMapper.getById(createPostDTO.getCategoryId());
        // if (category == null) {
        //     return Result.error(404, "分类不存在");
        // }

        // 创建帖子
        Post post = new Post();
        post.setUserId(userId);
        post.setTitle(createPostDTO.getTitle());
        post.setContent(createPostDTO.getContent());
        post.setCategoryId(createPostDTO.getCategoryId());
        post.setViewCount(0);
        post.setLikeCount(0);
        post.setCommentCount(0);
        post.setStatus((byte) 1);

        // 保存帖子
        postMapper.insert(post);

        Map<String, Object> data = new HashMap<>();
        data.put("postId", post.getId());

        return Result.success(data, "发布成功");
    }

    @Override
    public Result getPostDetail(Long postId, Long userId) {
        // 获取帖子
        Post post = postMapper.getById(postId);
        if (post == null) {
            return Result.error(404, "帖子不存在");
        }

        // 增加浏览次数
        postMapper.incrementViewCount(postId);

        // 获取发布用户信息
        User user = userMapper.getUserById(post.getUserId());

        // 获取分类信息
        // 注意：这里假设已经创建了CategoryMapper接口，实际开发中需要先创建
        // Category category = categoryMapper.getById(post.getCategoryId());

        // 构建返回数据
        PostVO postVO = PostVO.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .userId(post.getUserId())
                .nickname(user != null ? user.getNickname() : "未知用户")
                .avatar(user != null ? user.getAvatar() : null)
                .categoryId(post.getCategoryId())
                .categoryName("未知分类") // 实际开发中应该从category对象获取
                .viewCount(post.getViewCount() + 1) // 已经增加了浏览次数
                .likeCount(post.getLikeCount())
                .commentCount(post.getCommentCount())
                .isLiked(false) // 实际开发中应该查询用户是否点赞
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .build();

        return Result.success(postVO);
    }

    @Override
    public Result getPostList(Long categoryId, Integer page, Integer size, String sort) {
        // 参数校验
        if (page == null || page < 1) page = 1;
        if (size == null || size < 1 || size > 50) size = 10;
        if (StrUtil.isBlank(sort)) sort = "latest";

        // 只使用PageHelper进行分页，不设置排序
        PageHelper.startPage(page, size);

        // 调用mapper方法，传入排序参数
        List<Post> posts = postMapper.getListByCategory(categoryId, sort);

        // 获取分页信息
        PageInfo<Post> pageInfo = new PageInfo<>(posts);

        // 转换为VO
        List<PostListVO> postListVOs = posts.stream().map(post -> {
            User user = userMapper.getUserById(post.getUserId());
            return PostListVO.builder()
                    .id(post.getId())
                    .title(post.getTitle())
                    .summary(generateSummary(post.getContent()))
                    .userId(post.getUserId())
                    .nickname(user != null ? user.getNickname() : "未知用户")
                    .avatar(user != null ? user.getAvatar() : null)
                    .categoryId(post.getCategoryId())
                    .categoryName("未知分类")
                    .viewCount(post.getViewCount())
                    .likeCount(post.getLikeCount())
                    .commentCount(post.getCommentCount())
                    .createdAt(post.getCreatedAt())
                    .build();
        }).collect(Collectors.toList());

        // 返回结果
        Map<String, Object> data = new HashMap<>();
        data.put("total", pageInfo.getTotal());
        data.put("list", postListVOs);
        data.put("pages", pageInfo.getPages());

        return Result.success(data);
    }

    @Override
    public Result updatePost(Long postId, Long userId, UpdatePostDTO updatePostDTO) {
        // 获取帖子
        Post post = postMapper.getById(postId);
        if (post == null) {
            return Result.error(404, "帖子不存在");
        }

        // 检查是否有权限更新
        if (!post.getUserId().equals(userId)) {
            return Result.error(403, "无权限更新此帖子");
        }

        // 检查分类是否存在
        // 注意：这里假设已经创建了CategoryMapper接口，实际开发中需要先创建
        // Category category = categoryMapper.getById(updatePostDTO.getCategoryId());
        // if (category == null) {
        //     return Result.error(404, "分类不存在");
        // }

        // 更新帖子
        post.setTitle(updatePostDTO.getTitle());
        post.setContent(updatePostDTO.getContent());
        post.setCategoryId(updatePostDTO.getCategoryId());

        // 保存更新
        postMapper.update(post);

        return Result.success(null, "更新成功");
    }

    @Override
    public Result deletePost(Long postId, Long userId) {
        // 获取帖子
        Post post = postMapper.getById(postId);
        if (post == null) {
            return Result.error(404, "帖子不存在");
        }

        // 检查是否有权限删除
        if (!post.getUserId().equals(userId)) {
            return Result.error(403, "无权限删除此帖子");
        }

        // 删除帖子（逻辑删除）
        postMapper.delete(postId);

        return Result.success(null, "删除成功");
    }

    @Override
    public Result likePost(Long postId, Long userId) {
        // 获取帖子
        Post post = postMapper.getById(postId);
        if (post == null) {
            return Result.error(404, "帖子不存在");
        }

        // 检查用户是否已点赞
        // 注意：这里需要创建一个点赞表和相应的Mapper，实际开发中需要先创建
        boolean isLiked = false; // postLikeMapper.isLiked(postId, userId);

        if (isLiked) {
            // 取消点赞
            // postLikeMapper.delete(postId, userId);
            postMapper.decrementLikeCount(postId);
            return Result.success(null, "取消点赞成功");
        } else {
            // 添加点赞
            // postLikeMapper.insert(postId, userId);
            postMapper.incrementLikeCount(postId);
            return Result.success(null, "点赞成功");
        }
    }

    /**
     * 生成摘要
     * @param content 内容
     * @return 摘要
     */
    private String generateSummary(String content) {
        if (StrUtil.isBlank(content)) {
            return "";
        }

        // 去除HTML标签
        content = content.replaceAll("<[^>]+>", "");

        // 截取前100个字符作为摘要
        int length = Math.min(content.length(), 100);
        String summary = content.substring(0, length);

        // 如果内容超过100个字符，添加省略号
        if (content.length() > 100) {
            summary += "...";
        }

        return summary;
    }
}
