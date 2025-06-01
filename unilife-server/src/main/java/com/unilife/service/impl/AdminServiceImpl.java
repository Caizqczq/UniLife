package com.unilife.service.impl;

import com.unilife.common.result.Result;
import com.unilife.mapper.*;
import com.unilife.model.entity.*;
import com.unilife.service.AdminService;
import com.unilife.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private PostMapper postMapper;
    
    @Autowired
    private CommentMapper commentMapper;
    
    @Autowired
    private CategoryMapper categoryMapper;
    
    @Autowired
    private ResourceMapper resourceMapper;
    
    @Autowired
    private UserService userService;

    @Override
    public Result getSystemStats() {
        try {
            Map<String, Object> stats = new HashMap<>();
            
            // 用户统计
            stats.put("totalUsers", userMapper.getTotalCount());
            stats.put("activeUsers", userMapper.getActiveUserCount());
            stats.put("newUsersToday", userMapper.getNewUserCountToday());
            
            // 帖子统计
            stats.put("totalPosts", postMapper.getTotalCount());
            stats.put("newPostsToday", postMapper.getNewPostCountToday());
            
            // 评论统计
            stats.put("totalComments", commentMapper.getTotalCount());
            stats.put("newCommentsToday", commentMapper.getNewCommentCountToday());
            
            // 资源统计
            stats.put("totalResources", resourceMapper.getTotalCount());
            stats.put("newResourcesToday", resourceMapper.getNewResourceCountToday());
            
            // 分类统计
            stats.put("totalCategories", categoryMapper.getTotalCount());
            
            return Result.success(stats);
        } catch (Exception e) {
            log.error("获取系统统计数据失败", e);
            return Result.error(500, "获取系统统计数据失败");
        }
    }

    @Override
    public Result getUserList(Integer page, Integer size, String keyword, Integer role, Integer status) {
        try {
            int offset = (page - 1) * size;
            List<User> users = userMapper.getAdminUserList(offset, size, keyword, role, status);
            int total = userMapper.getAdminUserCount(keyword, role, status);
            
            Map<String, Object> result = new HashMap<>();
            result.put("list", users);
            result.put("total", total);
            result.put("pages", (total + size - 1) / size);
            
            return Result.success(result);
        } catch (Exception e) {
            log.error("获取用户列表失败", e);
            return Result.error(500, "获取用户列表失败");
        }
    }

    @Override
    public Result updateUserStatus(Long userId, Integer status) {
        try {
            User user = userMapper.getUserById(userId);
            if (user == null) {
                return Result.error(404, "用户不存在");
            }
            
            userMapper.updateUserStatus(userId, status);
            return Result.success(null, "用户状态更新成功");
        } catch (Exception e) {
            log.error("更新用户状态失败", e);
            return Result.error(500, "更新用户状态失败");
        }
    }

    @Override
    public Result updateUserRole(Long userId, Integer role) {
        try {
            User user = userMapper.getUserById(userId);
            if (user == null) {
                return Result.error(404, "用户不存在");
            }
            
            userMapper.updateUserRole(userId, role);
            return Result.success(null, "用户角色更新成功");
        } catch (Exception e) {
            log.error("更新用户角色失败", e);
            return Result.error(500, "更新用户角色失败");
        }
    }

    @Override
    public Result deleteUser(Long userId) {
        try {
            User user = userMapper.getUserById(userId);
            if (user == null) {
                return Result.error(404, "用户不存在");
            }
            
            // 检查是否为管理员
            if (user.getRole() == 2) {
                return Result.error(400, "不能删除管理员账号");
            }
            
            // 调用UserService的完整删除逻辑
            return userService.deleteUser(userId);
        } catch (Exception e) {
            log.error("删除用户失败", e);
            return Result.error(500, "删除用户失败");
        }
    }

    @Override
    public Result getPostList(Integer page, Integer size, String keyword, Long categoryId, Integer status) {
        try {
            int offset = (page - 1) * size;
            List<Post> posts = postMapper.getAdminPostList(offset, size, keyword, categoryId, status);
            int total = postMapper.getAdminPostCount(keyword, categoryId, status);
            
            Map<String, Object> result = new HashMap<>();
            result.put("list", posts);
            result.put("total", total);
            result.put("pages", (total + size - 1) / size);
            
            return Result.success(result);
        } catch (Exception e) {
            log.error("获取帖子列表失败", e);
            return Result.error(500, "获取帖子列表失败");
        }
    }

    @Override
    public Result updatePostStatus(Long postId, Integer status) {
        try {
            Post post = postMapper.getPostById(postId);
            if (post == null) {
                return Result.error(404, "帖子不存在");
            }
            
            postMapper.updatePostStatus(postId, status);
            return Result.success(null, "帖子状态更新成功");
        } catch (Exception e) {
            log.error("更新帖子状态失败", e);
            return Result.error(500, "更新帖子状态失败");
        }
    }

    @Override
    public Result deletePost(Long postId) {
        try {
            Post post = postMapper.getPostById(postId);
            if (post == null) {
                return Result.error(404, "帖子不存在");
            }
            
            postMapper.deletePost(postId);
            return Result.success(null, "帖子删除成功");
        } catch (Exception e) {
            log.error("删除帖子失败", e);
            return Result.error(500, "删除帖子失败");
        }
    }

    @Override
    public Result getCommentList(Integer page, Integer size, String keyword, Long postId, Integer status) {
        try {
            int offset = (page - 1) * size;
            List<Comment> comments = commentMapper.getAdminCommentList(offset, size, keyword, postId, status);
            int total = commentMapper.getAdminCommentCount(keyword, postId, status);
            
            Map<String, Object> result = new HashMap<>();
            result.put("list", comments);
            result.put("total", total);
            result.put("pages", (total + size - 1) / size);
            
            return Result.success(result);
        } catch (Exception e) {
            log.error("获取评论列表失败", e);
            return Result.error(500, "获取评论列表失败");
        }
    }

    @Override
    public Result deleteComment(Long commentId) {
        try {
            Comment comment = commentMapper.getCommentById(commentId);
            if (comment == null) {
                return Result.error(404, "评论不存在");
            }
            
            commentMapper.deleteComment(commentId);
            return Result.success(null, "评论删除成功");
        } catch (Exception e) {
            log.error("删除评论失败", e);
            return Result.error(500, "删除评论失败");
        }
    }

    @Override
    public Result getCategoryList() {
        try {
            List<Category> categories = categoryMapper.getAllCategories();
            return Result.success(categories);
        } catch (Exception e) {
            log.error("获取分类列表失败", e);
            return Result.error(500, "获取分类列表失败");
        }
    }

    @Override
    public Result createCategory(Map<String, Object> request) {
        try {
            Category category = new Category();
            category.setName((String) request.get("name"));
            category.setDescription((String) request.get("description"));
            category.setIcon((String) request.get("icon"));
            category.setSort((Integer) request.get("sort"));
            Integer statusInt = (Integer) request.get("status");
            category.setStatus(statusInt != null ? statusInt.byteValue() : (byte) 1);
            
            categoryMapper.insertCategory(category);
            return Result.success(null, "分类创建成功");
        } catch (Exception e) {
            log.error("创建分类失败", e);
            return Result.error(500, "创建分类失败");
        }
    }

    @Override
    public Result updateCategory(Long categoryId, Map<String, Object> request) {
        try {
            Category category = categoryMapper.getCategoryById(categoryId);
            if (category == null) {
                return Result.error(404, "分类不存在");
            }
            
            category.setName((String) request.get("name"));
            category.setDescription((String) request.get("description"));
            category.setIcon((String) request.get("icon"));
            category.setSort((Integer) request.get("sort"));
            Integer statusInt = (Integer) request.get("status");
            category.setStatus(statusInt != null ? statusInt.byteValue() : (byte) 1);
            
            categoryMapper.updateCategory(category);
            return Result.success(null, "分类更新成功");
        } catch (Exception e) {
            log.error("更新分类失败", e);
            return Result.error(500, "更新分类失败");
        }
    }

    @Override
    public Result deleteCategory(Long categoryId) {
        try {
            Category category = categoryMapper.getCategoryById(categoryId);
            if (category == null) {
                return Result.error(404, "分类不存在");
            }
            
            // 检查是否有帖子或资源使用该分类
            int postCount = postMapper.getCountByCategoryId(categoryId);
            int resourceCount = resourceMapper.getCountByCategoryId(categoryId);
            
            if (postCount > 0 || resourceCount > 0) {
                return Result.error(400, "该分类下还有帖子或资源，无法删除");
            }
            
            categoryMapper.deleteCategory(categoryId);
            return Result.success(null, "分类删除成功");
        } catch (Exception e) {
            log.error("删除分类失败", e);
            return Result.error(500, "删除分类失败");
        }
    }

    @Override
    public Result getResourceList(Integer page, Integer size, String keyword, Long categoryId, Integer status) {
        try {
            int offset = (page - 1) * size;
            List<Resource> resources = resourceMapper.getAdminResourceList(offset, size, keyword, categoryId, status);
            int total = resourceMapper.getAdminResourceCount(keyword, categoryId, status);
            
            Map<String, Object> result = new HashMap<>();
            result.put("list", resources);
            result.put("total", total);
            result.put("pages", (total + size - 1) / size);
            
            return Result.success(result);
        } catch (Exception e) {
            log.error("获取资源列表失败", e);
            return Result.error(500, "获取资源列表失败");
        }
    }

    @Override
    public Result deleteResource(Long resourceId) {
        try {
            Resource resource = resourceMapper.getResourceById(resourceId);
            if (resource == null) {
                return Result.error(404, "资源不存在");
            }
            
            resourceMapper.deleteResource(resourceId);
            return Result.success(null, "资源删除成功");
        } catch (Exception e) {
            log.error("删除资源失败", e);
            return Result.error(500, "删除资源失败");
        }
    }
} 