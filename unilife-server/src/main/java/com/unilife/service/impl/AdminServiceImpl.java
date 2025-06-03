package com.unilife.service.impl;

import com.unilife.common.result.Result;
import com.unilife.mapper.*;
import com.unilife.model.entity.*;
import com.unilife.model.vo.CourseVO;
import com.unilife.service.AdminService;
import com.unilife.service.UserService;
import com.unilife.utils.OssService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.stream.Collectors;

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
    private CourseMapper courseMapper;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private PdfVectorAsyncService pdfVectorAsyncService;
    
    @Autowired
    private OssService ossService;

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
            
            // 课程统计
            stats.put("totalCourses", courseMapper.getTotalCount());
            stats.put("newCoursesToday", courseMapper.getNewCourseCountToday());
            
            // 分类统计
            stats.put("totalCategories", categoryMapper.getTotalCount());
            
            // 简单的增长趋势计算（基于今日新增）
            int totalUsers = userMapper.getTotalCount();
            int newUsersToday = userMapper.getNewUserCountToday();
            double userGrowth = totalUsers > 0 ? (double) newUsersToday / totalUsers * 100 : 0;
            
            int totalPosts = postMapper.getTotalCount();
            int newPostsToday = postMapper.getNewPostCountToday();
            double postGrowth = totalPosts > 0 ? (double) newPostsToday / totalPosts * 100 : 0;
            
            int totalComments = commentMapper.getTotalCount();
            int newCommentsToday = commentMapper.getNewCommentCountToday();
            double commentGrowth = totalComments > 0 ? (double) newCommentsToday / totalComments * 100 : 0;
            
            int totalResources = resourceMapper.getTotalCount();
            int newResourcesToday = resourceMapper.getNewResourceCountToday();
            double resourceGrowth = totalResources > 0 ? (double) newResourcesToday / totalResources * 100 : 0;
            
            int totalCourses = courseMapper.getTotalCount();
            int newCoursesToday = courseMapper.getNewCourseCountToday();
            double courseGrowth = totalCourses > 0 ? (double) newCoursesToday / totalCourses * 100 : 0;
            
            stats.put("userGrowth", Math.round(userGrowth * 100.0) / 100.0);
            stats.put("postGrowth", Math.round(postGrowth * 100.0) / 100.0);
            stats.put("commentGrowth", Math.round(commentGrowth * 100.0) / 100.0);
            stats.put("resourceGrowth", Math.round(resourceGrowth * 100.0) / 100.0);
            stats.put("courseGrowth", Math.round(courseGrowth * 100.0) / 100.0);
            
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
            
            // 调用UserService的完整删除逻辑（逻辑删除，保留用户删除的复杂逻辑）
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
            
            // 物理删除帖子及相关数据
            postMapper.permanentDeletePost(postId);
            return Result.success(null, "帖子删除成功");
        } catch (Exception e) {
            log.error("删除帖子失败", e);
            return Result.error(500, "删除帖子失败");
        }
    }

    @Override
    public Result permanentDeletePost(Long postId) {
        try {
            Post post = postMapper.getPostById(postId);
            if (post == null) {
                return Result.error(404, "帖子不存在");
            }
            
            // 永久删除帖子（物理删除）
            postMapper.permanentDeletePost(postId);
            return Result.success(null, "帖子永久删除成功");
        } catch (Exception e) {
            log.error("永久删除帖子失败", e);
            return Result.error(500, "永久删除帖子失败");
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
            
            // 物理删除评论
            commentMapper.permanentDeleteComment(commentId);
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
            
            // 如果是PDF文件，需要先删除向量数据库中的相关文档
            if ("application/pdf".equals(resource.getFileType())) {
                pdfVectorAsyncService.deleteVectorDocumentsAsync(resourceId, resource.getTitle());
                log.info("PDF文件已提交异步删除向量文档，资源ID: {}", resourceId);
            }
            
            // 删除OSS中的文件
            try {
                String fileUrl = resource.getFileUrl();
                if (fileUrl != null && fileUrl.startsWith("http")) {
                    ossService.deleteFile(fileUrl);
                    log.info("OSS文件删除成功，资源ID: {}", resourceId);
                }
            } catch (Exception e) {
                log.error("删除OSS文件失败，资源ID: {}", resourceId, e);
                // 继续执行，不影响数据库记录的删除
            }
            
            // 最后物理删除资源数据库记录
            resourceMapper.permanentDeleteResource(resourceId);
            return Result.success(null, "资源删除成功");
        } catch (Exception e) {
            log.error("删除资源失败", e);
            return Result.error(500, "删除资源失败");
        }
    }

    @Override
    public Result getSystemStatus() {
        try {
            Map<String, Object> status = new HashMap<>();
            
            // 简单的应用状态
            Map<String, Object> appStatus = new HashMap<>();
            appStatus.put("online", true);
            appStatus.put("onlineUsers", userService.getOnlineUserCount());
            
            status.put("application", appStatus);
            
            return Result.success(status);
        } catch (Exception e) {
            log.error("获取系统状态失败", e);
            return Result.error(500, "获取系统状态失败");
        }
    }

    @Override
    public Result getSystemLogs(Integer page, Integer size, String level, String keyword, String startDate, String endDate) {
        // 日志管理功能较为复杂，暂不实现
        return Result.error(501, "日志管理功能暂未实现");
    }

    @Override
    public Result getSystemSettings() {
        // 系统设置功能较为复杂，暂不实现  
        return Result.error(501, "系统设置功能暂未实现");
    }

    @Override
    public Result updateSystemSettings(Map<String, Object> settings) {
        // 系统设置功能较为复杂，暂不实现
        return Result.error(501, "系统设置功能暂未实现");
    }

    @Override
    public Result getAnnouncements() {
        // 公告功能较为复杂，暂不实现
        return Result.error(501, "公告功能暂未实现");
    }

    @Override
    public Result createAnnouncement(Map<String, Object> announcement) {
        // 公告功能较为复杂，暂不实现
        return Result.error(501, "公告功能暂未实现");
    }

    @Override
    public Result updateAnnouncement(Long id, Map<String, Object> announcement) {
        // 公告功能较为复杂，暂不实现
        return Result.error(501, "公告功能暂未实现");
    }

    @Override
    public Result deleteAnnouncement(Long id) {
        // 公告功能较为复杂，暂不实现
        return Result.error(501, "公告功能暂未实现");
    }

    @Override
    public Result getNotifications() {
        // 通知功能较为复杂，暂不实现
        return Result.error(501, "通知功能暂未实现");
    }

    @Override
    public Result markNotificationAsRead(Long id) {
        // 通知功能较为复杂，暂不实现
        return Result.error(501, "通知功能暂未实现");
    }

    @Override
    public Result testEmail(Map<String, String> request) {
        // 邮件测试功能较为复杂，暂不实现
        return Result.error(501, "邮件测试功能暂未实现");
    }

    @Override
    public Result getStatistics() {
        // 统计图表功能较为复杂，暂不实现
        return Result.error(501, "统计图表功能暂未实现");
    }

        @Override
    public Result backupData() {
        // 数据备份功能较为复杂，暂不实现
        return Result.error(501, "数据备份功能暂未实现");
    }

    // ========== 课表管理相关方法 ==========

    @Override
    public Result getCourseList(Integer page, Integer size, String keyword, Long userId, String semester, Integer status) {
        try {
            int offset = (page - 1) * size;
            List<Course> courses = courseMapper.getAdminCourseList(offset, size, keyword, userId, semester, status);
            int total = courseMapper.getAdminCourseCount(keyword, userId, semester, status);
            
            // 转换为VO并添加用户信息
            List<Map<String, Object>> courseList = courses.stream().map(course -> {
                Map<String, Object> courseInfo = new HashMap<>();
                courseInfo.put("id", course.getId());
                courseInfo.put("userId", course.getUserId());
                courseInfo.put("name", course.getName());
                courseInfo.put("teacher", course.getTeacher());
                courseInfo.put("location", course.getLocation());
                courseInfo.put("dayOfWeek", course.getDayOfWeek());
                courseInfo.put("startTime", course.getStartTime());
                courseInfo.put("endTime", course.getEndTime());
                courseInfo.put("startWeek", course.getStartWeek());
                courseInfo.put("endWeek", course.getEndWeek());
                courseInfo.put("semester", course.getSemester());
                courseInfo.put("color", course.getColor());
                courseInfo.put("status", course.getStatus());
                courseInfo.put("createdAt", course.getCreatedAt());
                courseInfo.put("updatedAt", course.getUpdatedAt());
                
                // 获取用户信息
                try {
                    User user = userMapper.getUserById(course.getUserId());
                    if (user != null) {
                        courseInfo.put("username", user.getUsername());
                        courseInfo.put("nickname", user.getNickname());
                        courseInfo.put("studentId", user.getStudentId());
                        courseInfo.put("department", user.getDepartment());
                        courseInfo.put("major", user.getMajor());
                    }
                } catch (Exception e) {
                    log.warn("获取课程用户信息失败，课程ID: {}, 用户ID: {}", course.getId(), course.getUserId());
                }
                
                return courseInfo;
            }).collect(Collectors.toList());
            
            Map<String, Object> result = new HashMap<>();
            result.put("list", courseList);
            result.put("total", total);
            result.put("pages", (total + size - 1) / size);
            
            return Result.success(result);
        } catch (Exception e) {
            log.error("获取课程列表失败", e);
            return Result.error(500, "获取课程列表失败");
        }
    }

    @Override
    public Result getCourseDetail(Long courseId) {
        try {
            Course course = courseMapper.getCourseById(courseId);
            if (course == null) {
                return Result.error(404, "课程不存在");
            }
            
            // 构建课程详情信息
            Map<String, Object> courseDetail = new HashMap<>();
            courseDetail.put("id", course.getId());
            courseDetail.put("userId", course.getUserId());
            courseDetail.put("name", course.getName());
            courseDetail.put("teacher", course.getTeacher());
            courseDetail.put("location", course.getLocation());
            courseDetail.put("dayOfWeek", course.getDayOfWeek());
            courseDetail.put("startTime", course.getStartTime());
            courseDetail.put("endTime", course.getEndTime());
            courseDetail.put("startWeek", course.getStartWeek());
            courseDetail.put("endWeek", course.getEndWeek());
            courseDetail.put("semester", course.getSemester());
            courseDetail.put("color", course.getColor());
            courseDetail.put("status", course.getStatus());
            courseDetail.put("createdAt", course.getCreatedAt());
            courseDetail.put("updatedAt", course.getUpdatedAt());
            
            // 获取用户信息
            User user = userMapper.getUserById(course.getUserId());
            if (user != null) {
                Map<String, Object> userInfo = new HashMap<>();
                userInfo.put("id", user.getId());
                userInfo.put("username", user.getUsername());
                userInfo.put("nickname", user.getNickname());
                userInfo.put("studentId", user.getStudentId());
                userInfo.put("department", user.getDepartment());
                userInfo.put("major", user.getMajor());
                userInfo.put("grade", user.getGrade());
                courseDetail.put("user", userInfo);
            }
            
            return Result.success(courseDetail);
        } catch (Exception e) {
            log.error("获取课程详情失败", e);
            return Result.error(500, "获取课程详情失败");
        }
    }

    @Override
    public Result deleteCourse(Long courseId) {
        try {
            Course course = courseMapper.getCourseById(courseId);
            if (course == null) {
                return Result.error(404, "课程不存在");
            }
            
            // 物理删除课程
            courseMapper.permanentDeleteCourse(courseId);
            return Result.success(null, "课程删除成功");
        } catch (Exception e) {
            log.error("删除课程失败", e);
            return Result.error(500, "删除课程失败");
        }
    }

    @Override
    public Result getUserSchedule(Long userId, String semester) {
        try {
            log.info("=== 管理员获取用户课表 ===");
            log.info("用户ID: {}, 学期: {}", userId, semester);
            
            User user = userMapper.getUserById(userId);
            if (user == null) {
                log.warn("用户不存在，用户ID: {}", userId);
                return Result.error(404, "用户不存在");
            }
            log.info("找到用户: {}, 昵称: {}, 学号: {}", user.getUsername(), user.getNickname(), user.getStudentId());
            
            // 获取用户在指定学期的课程
            List<Course> courses = courseMapper.getListByUserIdAndSemester(userId, semester);
            log.info("查询到课程数量: {}", courses.size());
            
            if (!courses.isEmpty()) {
                log.info("课程详情:");
                for (Course course : courses) {
                    log.info("- 课程: {}, 教师: {}, 星期: {}, 时间: {}-{}", 
                        course.getName(), course.getTeacher(), course.getDayOfWeek(), 
                        course.getStartTime(), course.getEndTime());
                }
            }
            
            // 按星期几分组组织课表数据
            Map<Integer, List<Map<String, Object>>> schedule = new HashMap<>();
            for (int i = 1; i <= 7; i++) {
                schedule.put(i, new ArrayList<>());
            }
            
            for (Course course : courses) {
                Map<String, Object> courseInfo = new HashMap<>();
                courseInfo.put("id", course.getId());
                courseInfo.put("name", course.getName());
                courseInfo.put("teacher", course.getTeacher());
                courseInfo.put("location", course.getLocation());
                courseInfo.put("startTime", course.getStartTime());
                courseInfo.put("endTime", course.getEndTime());
                courseInfo.put("startWeek", course.getStartWeek());
                courseInfo.put("endWeek", course.getEndWeek());
                courseInfo.put("color", course.getColor());
                courseInfo.put("status", course.getStatus());
                
                schedule.get((int) course.getDayOfWeek()).add(courseInfo);
            }
            
            Map<String, Object> result = new HashMap<>();
            result.put("user", Map.of(
                "id", user.getId(),
                "username", user.getUsername(),
                "nickname", user.getNickname(),
                "studentId", user.getStudentId(),
                "department", user.getDepartment(),
                "major", user.getMajor(),
                "grade", user.getGrade()
            ));
            result.put("semester", semester);
            result.put("schedule", schedule);
            result.put("totalCourses", courses.size());
            
            log.info("返回结果，总课程数: {}", courses.size());
            return Result.success(result);
        } catch (Exception e) {
            log.error("获取用户课表失败", e);
            return Result.error(500, "获取用户课表失败");
        }
    }

} 