-- UniLife数据库完整重建脚本
-- 此脚本将删除现有数据库并重新创建，然后插入测试数据
-- 注意：执行前请确保已备份重要数据！

-- ==========================================
-- 第一步：删除并重新创建数据库
-- ==========================================

-- 删除现有数据库（如果存在）
DROP DATABASE IF EXISTS UniLife;

-- 创建新数据库
CREATE DATABASE UniLife DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 使用数据库
USE UniLife;

-- ==========================================
-- 第二步：创建表结构（无外键约束）
-- ==========================================

-- 用户表
CREATE TABLE `users` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
  `username` VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
  `email` VARCHAR(100) NOT NULL UNIQUE COMMENT '邮箱地址（学校邮箱）',
  `password` VARCHAR(255) NOT NULL COMMENT '密码（加密存储）',
  `nickname` VARCHAR(50) NOT NULL COMMENT '昵称',
  `avatar` VARCHAR(255) DEFAULT NULL COMMENT '头像URL',
  `bio` TEXT DEFAULT NULL COMMENT '个人简介',
  `gender` TINYINT DEFAULT 0 COMMENT '性别（0-未知, 1-男, 2-女）',
  `student_id` VARCHAR(20) UNIQUE DEFAULT NULL COMMENT '学号',
  `department` VARCHAR(100) DEFAULT NULL COMMENT '院系',
  `major` VARCHAR(100) DEFAULT NULL COMMENT '专业',
  `grade` VARCHAR(20) DEFAULT NULL COMMENT '年级',
  `points` INT DEFAULT 0 COMMENT '积分',
  `role` TINYINT DEFAULT 0 COMMENT '角色（0-普通用户, 1-版主, 2-管理员）',
  `status` TINYINT DEFAULT 1 COMMENT '状态（0-禁用, 1-启用）',
  `is_verified` TINYINT DEFAULT 0 COMMENT '是否验证（0-未验证, 1-已验证）',
  `login_ip` VARCHAR(50) DEFAULT NULL COMMENT '最近登录IP',
  `login_time` DATETIME DEFAULT NULL COMMENT '最近登录时间',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  INDEX `idx_email` (`email`),
  INDEX `idx_username` (`username`),
  INDEX `idx_student_id` (`student_id`),
  INDEX `idx_role` (`role`),
  INDEX `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 分类表
CREATE TABLE `categories` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '分类ID',
  `name` VARCHAR(50) NOT NULL UNIQUE COMMENT '分类名称',
  `description` VARCHAR(255) DEFAULT NULL COMMENT '分类描述',
  `icon` VARCHAR(255) DEFAULT NULL COMMENT '分类图标',
  `sort` INT DEFAULT 0 COMMENT '排序',
  `status` TINYINT DEFAULT 1 COMMENT '状态（0-禁用, 1-启用）',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  INDEX `idx_status` (`status`),
  INDEX `idx_sort` (`sort`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='分类表';

-- 帖子表
CREATE TABLE `posts` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '帖子ID',
  `user_id` BIGINT NOT NULL COMMENT '发布用户ID',
  `title` VARCHAR(100) NOT NULL COMMENT '帖子标题',
  `content` TEXT NOT NULL COMMENT '帖子内容',
  `category_id` BIGINT NOT NULL COMMENT '分类ID',
  `view_count` INT DEFAULT 0 COMMENT '浏览次数',
  `like_count` INT DEFAULT 0 COMMENT '点赞次数',
  `comment_count` INT DEFAULT 0 COMMENT '评论次数',
  `status` TINYINT DEFAULT 1 COMMENT '状态（0-删除, 1-正常, 2-置顶）',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  INDEX `idx_user_id` (`user_id`),
  INDEX `idx_category_id` (`category_id`),
  INDEX `idx_status` (`status`),
  INDEX `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='帖子表';

-- 评论表
CREATE TABLE `comments` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '评论ID',
  `post_id` BIGINT NOT NULL COMMENT '帖子ID',
  `user_id` BIGINT NOT NULL COMMENT '评论用户ID',
  `content` TEXT NOT NULL COMMENT '评论内容',
  `parent_id` BIGINT DEFAULT NULL COMMENT '父评论ID（回复某条评论）',
  `like_count` INT DEFAULT 0 COMMENT '点赞次数',
  `status` TINYINT DEFAULT 1 COMMENT '状态（0-删除, 1-正常）',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  INDEX `idx_post_id` (`post_id`),
  INDEX `idx_user_id` (`user_id`),
  INDEX `idx_parent_id` (`parent_id`),
  INDEX `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='评论表';

-- 点赞表（用户-帖子）
CREATE TABLE `post_likes` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '点赞ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `post_id` BIGINT NOT NULL COMMENT '帖子ID',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  UNIQUE KEY `uk_user_post` (`user_id`, `post_id`),
  INDEX `idx_user_id` (`user_id`),
  INDEX `idx_post_id` (`post_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='帖子点赞表';

-- 点赞表（用户-评论）
CREATE TABLE `comment_likes` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '点赞ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `comment_id` BIGINT NOT NULL COMMENT '评论ID',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  UNIQUE KEY `uk_user_comment` (`user_id`, `comment_id`),
  INDEX `idx_user_id` (`user_id`),
  INDEX `idx_comment_id` (`comment_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='评论点赞表';

-- 点赞表（用户-资源）
CREATE TABLE `resource_likes` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '点赞ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `resource_id` BIGINT NOT NULL COMMENT '资源ID',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  UNIQUE KEY `uk_user_resource` (`user_id`, `resource_id`),
  INDEX `idx_user_id` (`user_id`),
  INDEX `idx_resource_id` (`resource_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='资源点赞表';

-- 资源表
CREATE TABLE `resources` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '资源ID',
  `user_id` BIGINT NOT NULL COMMENT '上传用户ID',
  `title` VARCHAR(100) NOT NULL COMMENT '资源标题',
  `description` TEXT DEFAULT NULL COMMENT '资源描述',
  `file_url` VARCHAR(255) NOT NULL COMMENT '文件URL',
  `file_size` BIGINT NOT NULL COMMENT '文件大小（字节）',
  `file_type` VARCHAR(100) NOT NULL COMMENT '文件类型',
  `category_id` BIGINT NOT NULL COMMENT '分类ID',
  `download_count` INT DEFAULT 0 COMMENT '下载次数',
  `like_count` INT DEFAULT 0 COMMENT '点赞次数',
  `status` TINYINT DEFAULT 1 COMMENT '状态（0-删除, 1-正常）',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  INDEX `idx_user_id` (`user_id`),
  INDEX `idx_category_id` (`category_id`),
  INDEX `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='资源表';

-- 课程表
CREATE TABLE `courses` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '课程ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `name` VARCHAR(100) NOT NULL COMMENT '课程名称',
  `teacher` VARCHAR(50) DEFAULT NULL COMMENT '教师姓名',
  `location` VARCHAR(100) DEFAULT NULL COMMENT '上课地点',
  `day_of_week` TINYINT NOT NULL COMMENT '星期几（1-7）',
  `start_time` TIME NOT NULL COMMENT '开始时间',
  `end_time` TIME NOT NULL COMMENT '结束时间',
  `start_week` SMALLINT NOT NULL COMMENT '开始周次',
  `end_week` SMALLINT NOT NULL COMMENT '结束周次',
  `semester` VARCHAR(20) DEFAULT NULL COMMENT '学期（如：2023-1）',
  `color` VARCHAR(20) DEFAULT NULL COMMENT '显示颜色',
  `status` TINYINT DEFAULT 1 COMMENT '状态（0-删除, 1-正常）',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  INDEX `idx_user_id` (`user_id`),
  INDEX `idx_semester` (`semester`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='课程表';

-- 日程表
CREATE TABLE `schedules` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '日程ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `title` VARCHAR(100) NOT NULL COMMENT '日程标题',
  `description` TEXT DEFAULT NULL COMMENT '日程描述',
  `start_time` DATETIME NOT NULL COMMENT '开始时间',
  `end_time` DATETIME NOT NULL COMMENT '结束时间',
  `location` VARCHAR(100) DEFAULT NULL COMMENT '地点',
  `is_all_day` TINYINT DEFAULT 0 COMMENT '是否全天（0-否, 1-是）',
  `reminder` INT DEFAULT NULL COMMENT '提醒时间（分钟）',
  `color` VARCHAR(20) DEFAULT NULL COMMENT '显示颜色',
  `status` TINYINT DEFAULT 1 COMMENT '状态（0-删除, 1-正常）',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  INDEX `idx_user_id` (`user_id`),
  INDEX `idx_start_time` (`start_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='日程表';

-- ==========================================
-- 第三步：插入初始分类数据
-- ==========================================

INSERT INTO `categories` (`name`, `description`, `icon`, `sort`, `status`) VALUES
('学习交流', '讨论学习相关话题', 'icon-study', 1, 1),
('校园生活', '分享校园生活点滴', 'icon-campus', 2, 1),
('兴趣爱好', '交流各类兴趣爱好', 'icon-hobby', 3, 1),
('求职就业', '分享求职经验和就业信息', 'icon-job', 4, 1),
('资源共享', '分享各类学习资源', 'icon-resource', 5, 1);

-- 初始化管理员账号
INSERT INTO `users` (`username`, `email`, `password`, `nickname`, `role`, `status`, `is_verified`, `created_at`) VALUES
('admin', 'admin@unilife.com', '123456', '系统管理员', 2, 1, 1, '2024-01-01 00:00:00'),
('superadmin', 'superadmin@unilife.com', 'admin123', '超级管理员', 2, 1, 1, '2024-01-01 00:00:00');

-- ==========================================
-- 第四步：插入测试数据
-- ==========================================

-- 更新现有分类数据（更贴合武汉大学实际）
UPDATE `categories` SET 
  `name` = '学术交流', 
  `description` = '学术讨论、科研分享、竞赛经验',
  `icon` = '📚'
WHERE `name` = '学习交流';

UPDATE `categories` SET 
  `description` = '武汉大学校园生活、社团活动、文化娱乐',
  `icon` = '🏫'
WHERE `name` = '校园生活';

UPDATE `categories` SET 
  `name` = '就业实习',
  `description` = '实习经验、求职心得、职业规划',
  `icon` = '💼'
WHERE `name` = '求职就业';

-- 插入新的分类
INSERT INTO `categories` (`name`, `description`, `icon`, `sort`, `status`) VALUES
('学院专区', '各学院学生交流专区', '🎓', 6, 1),
('考研考公', '研究生入学考试、公务员考试', '📖', 7, 1),
('生活服务', '二手交易、失物招领、校园服务', '🛍️', 8, 1);

-- 插入武汉大学学生用户数据
INSERT INTO `users` (`username`, `email`, `password`, `nickname`, `bio`, `gender`, `student_id`, `department`, `major`, `grade`, `points`, `role`, `status`, `is_verified`, `created_at`) VALUES
-- 文理学部学生
('czq2024', 'czq@whu.edu.cn', '123456', '珞珈数学狗', '数学与统计学院2022级数学类，热爱数学建模，ACM银牌选手', 1, '2022301140001', '数学与统计学院', '数学类', '2022级', 150, 0, 1, 1, '2024-09-01 09:00:00'),
('lihua_cs', 'lihua@whu.edu.cn', '123456', '代码诗人', '计算机学院2021级软件工程，全栈开发爱好者，开源项目贡献者', 1, '2021301120001', '计算机学院', '软件工程', '2021级', 230, 0, 1, 1, '2024-09-01 10:00:00'),
('wangming_law', 'wangming@whu.edu.cn', '123456', '法学小白', '法学院2023级法学专业，模拟法庭常客，梦想成为大律师', 1, '2023301080001', '法学院', '法学', '2023级', 80, 0, 1, 1, '2024-09-01 11:00:00'),
('zhangwei_chem', 'zhangwei@whu.edu.cn', '123456', '化学实验员', '化学与分子科学学院2022级化学专业，实验室常驻，合成达人', 1, '2022301130001', '化学与分子科学学院', '化学', '2022级', 120, 0, 1, 1, '2024-09-01 12:00:00'),
('liuxin_econ', 'liuxin@whu.edu.cn', '123456', '经济观察者', '经济与管理学院2021级经济学，关注宏观经济政策，券商实习生', 2, '2021301110001', '经济与管理学院', '经济学', '2021级', 200, 0, 1, 1, '2024-09-01 13:00:00'),

-- 工学部学生
('chenfei_water', 'chenfei@whu.edu.cn', '123456', '水利工程师', '水利水电学院2022级水利水电工程，三峡实习经历，立志建设美丽中国', 1, '2022301320001', '水利水电学院', '水利水电工程', '2022级', 90, 0, 1, 1, '2024-09-01 14:00:00'),
('sunhao_power', 'sunhao@whu.edu.cn', '123456', '电气小子', '电气与自动化学院2023级电气工程及其自动化，电力系统仿真专家', 1, '2023301330001', '电气与自动化学院', '电气工程及其自动化', '2023级', 70, 0, 1, 1, '2024-09-01 15:00:00'),
('wujing_civil', 'wujing@whu.edu.cn', '123456', '土木妹子', '土木建筑工程学院2022级土木工程，桥梁设计爱好者，BIM技术达人', 2, '2022301340001', '土木建筑工程学院', '土木工程', '2022级', 110, 0, 1, 1, '2024-09-01 16:00:00'),

-- 信息学部学生
('liqiang_remote', 'liqiang@whu.edu.cn', '123456', '遥感专家', '遥感信息工程学院2021级遥感科学与技术，无人机航拍爱好者', 1, '2021301210001', '遥感信息工程学院', '遥感科学与技术', '2021级', 180, 0, 1, 1, '2024-09-01 17:00:00'),
('zhaoli_survey', 'zhaoli@whu.edu.cn', '123456', '测绘达人', '测绘学院2022级测绘工程，GPS定位技术研究者，野外作业经验丰富', 1, '2022301220001', '测绘学院', '测绘工程', '2022级', 95, 0, 1, 1, '2024-09-01 18:00:00'),

-- 医学部学生
('huangyan_med', 'huangyan@whu.edu.cn', '123456', '未来医生', '基础医学院2020级临床医学，人民医院实习生，立志救死扶伤', 2, '2020301410001', '基础医学院', '临床医学', '2020级', 250, 0, 1, 1, '2024-09-01 19:00:00'),
('wangpeng_dental', 'wangpeng@whu.edu.cn', '123456', '口腔医师', '口腔医学院2021级口腔医学，口腔医院见习，关注口腔健康科普', 1, '2021301420001', '口腔医学院', '口腔医学', '2021级', 160, 0, 1, 1, '2024-09-01 20:00:00'),

-- 人文社科学部学生
('luxiaoya_chinese', 'luxiaoya@whu.edu.cn', '123456', '文学少女', '文学院2022级汉语言文学，古典文学爱好者，诗词社社长', 2, '2022301050001', '文学院', '汉语言文学', '2022级', 140, 0, 1, 1, '2024-09-01 21:00:00'),
('zhoujie_history', 'zhoujie@whu.edu.cn', '123456', '史学研究生', '历史学院研究生，中国古代史方向，博物馆志愿者', 1, '2024302050001', '历史学院', '中国史', '2024级', 100, 0, 1, 1, '2024-09-01 22:00:00'),
('tanglei_news', 'tanglei@whu.edu.cn', '123456', '新传人', '新闻与传播学院2021级新闻学，校媒记者，关注社会热点', 1, '2021301070001', '新闻与传播学院', '新闻学', '2021级', 170, 0, 1, 1, '2024-09-01 23:00:00');

-- 插入论坛帖子数据（使用已存在的用户ID，初始计数设为0）
INSERT INTO `posts` (`user_id`, `category_id`, `title`, `content`, `view_count`, `like_count`, `comment_count`, `status`, `created_at`) VALUES
-- 学术交流类帖子
(2, 1, '数学建模美赛经验分享', '刚刚结束的美国大学生数学建模竞赛，我们团队获得了M奖！分享一下参赛经验和技巧，希望对学弟学妹们有帮助。数模比赛不仅考验数学能力，更重要的是团队协作和论文写作能力。首先要选择合适的队友，最好是数学、编程、英语各有所长的组合...', 256, 0, 0, 2, '2024-12-20 09:30:00'),

(3, 1, 'ACM-ICPC区域赛总结', '参加了西安站的ACM区域赛，虽然没能拿到金牌，但收获很大。分享一下刷题心得和比赛策略，特别是动态规划和图论算法的练习方法。建议大家多在Codeforces和AtCoder上练习，这些平台的题目质量很高...', 189, 0, 0, 1, '2024-12-19 16:45:00'),

(6, 1, '宏观经济学课程研讨：通胀与货币政策', '最近在学习宏观经济学，对当前的通胀形势和央行货币政策有一些思考。想和大家讨论一下利率调整对经济的影响机制，特别是在当前全球经济形势下的作用...', 145, 0, 0, 1, '2024-12-18 14:20:00'),

-- 校园生活类帖子
(14, 2, '武大樱花季摄影大赛作品展示', '樱花季刚过，分享一些在樱花大道拍摄的照片。今年的樱花开得特别美，虽然人很多，但还是拍到了一些不错的角度。附上拍摄技巧分享！使用的是佳能5D4，光圈f/2.8，ISO400，后期用LR调色...', 1234, 0, 0, 2, '2024-04-10 10:15:00'),

(16, 2, '校运动会志愿者招募！', '第55届田径运动会即将开始，现招募志愿者！工作内容包括引导、记分、颁奖等。参与志愿服务可获得志愿时长认证，还有纪念品哦～有意向的同学请在评论区留言或私信联系我', 456, 0, 0, 1, '2024-12-15 08:00:00'),

(11, 2, '测绘学院野外实习日记', '刚从庐山实习回来，分享一下野外测量的酸甜苦辣。早上5点起床，背着仪器爬山，虽然辛苦但收获满满。珞珈山的风景真是看不够啊！学到了很多实际操作技能...', 234, 0, 0, 1, '2024-12-14 19:30:00'),

-- 学院专区类帖子  
(4, 6, '法学院模拟法庭大赛预告', '一年一度的"枫叶杯"模拟法庭大赛即将开始！欢迎各年级同学组队参加。比赛分为民事组和刑事组，优胜者将代表学院参加全国比赛。这是提升法律实务能力的绝佳机会...', 345, 0, 0, 2, '2024-12-16 11:00:00'),

(5, 6, '化学实验安全注意事项提醒', '最近实验室发生了几起小事故，提醒大家一定要注意安全！特别是使用强酸强碱时，护目镜和手套必须佩戴。实验无小事，安全第一！同时要做好实验记录...', 178, 0, 0, 1, '2024-12-17 15:20:00'),

-- 就业实习类帖子
(6, 3, '券商实习面试经验分享', '刚刚拿到某头部券商的实习offer，分享一下面试经验。金融行业对专业能力和综合素质要求都很高，准备过程中要注意这几个方面：扎实的专业基础、良好的表达能力、对市场的敏感度...', 423, 0, 0, 1, '2024-12-21 14:00:00'),

(3, 3, 'IT互联网春招总结', '经历了春招季，最终选择了某大厂的后端开发岗位。分享一下投递简历、技术面试、HR面试的全流程经验，希望对计算机专业的同学有帮助。技术面试主要考察数据结构、算法、系统设计...', 567, 0, 0, 2, '2024-05-18 09:15:00'),

-- 考研考公类帖子
(15, 7, '历史学考研经验贴', '成功上岸北师大中国史专业！分享一下备考经验：如何选择学校、如何制定复习计划、如何准备专业课等。考研路上不孤单，加油！专业课复习要注意史料分析和论述题...', 389, 0, 0, 1, '2024-12-10 22:00:00'),

-- 生活服务类帖子
(9, 8, '出售工科教材一批', '即将毕业，出售一些专业课教材：《结构力学》《材料力学》《工程制图》等，八成新，价格优惠。有需要的学弟学妹可以联系我～都是正版教材，保存得很好', 156, 0, 0, 1, '2024-12-22 18:30:00'),

(13, 8, '寻找珞珈山丢失的口腔器械包', '昨天在樱花大道丢失了一个蓝色器械包，里面有重要的口腔实习用具。如有好心人捡到，请联系我，必有重谢！器械包上有我的姓名标签', 89, 0, 0, 1, '2024-12-23 07:45:00');

-- 插入评论数据
INSERT INTO `comments` (`post_id`, `user_id`, `content`, `parent_id`, `like_count`, `status`, `created_at`) VALUES
-- 对数学建模帖子的评论
(1, 3, '恭喜学长！我们正在准备下半年的国赛，请问有什么推荐的学习资料吗？', NULL, 5, 1, '2024-12-20 10:30:00'),
(1, 6, '数模确实需要很强的团队协作能力，我们当时就是沟通不够充分才没拿到好成绩', NULL, 3, 1, '2024-12-20 11:15:00'),
(1, 2, '推荐《数学建模方法与分析》这本书，MATLAB和Python都要熟练掌握', 1, 2, 1, '2024-12-20 12:00:00'),

-- 对樱花帖子的评论
(4, 10, '照片拍得真美！求拍摄参数和后期处理方法', NULL, 8, 1, '2024-04-10 14:30:00'),
(4, 16, '武大的樱花确实是一绝，每年都要来打卡', NULL, 4, 1, '2024-04-10 15:45:00'),

-- 对法学院帖子的评论
(7, 15, '法学院的模拟法庭一直很有名，想去观摩学习', NULL, 3, 1, '2024-12-16 13:00:00'),
(7, 4, '欢迎其他学院的同学来观摩！比赛时间是下周五晚上', 6, 1, 1, '2024-12-16 14:30:00'),

-- 对实习帖子的评论
(9, 2, '金融行业竞争确实激烈，学长有什么建议给想进入这个行业的同学吗？', NULL, 4, 1, '2024-12-21 15:30:00'),
(9, 6, '建议先把CFA一级考出来，然后多参加实习积累经验', 8, 6, 1, '2024-12-21 16:45:00');

-- 插入点赞数据
INSERT INTO `post_likes` (`user_id`, `post_id`, `created_at`) VALUES
-- 用户点赞帖子
(2, 4, '2024-04-10 11:00:00'),
(2, 7, '2024-12-16 12:00:00'),
(3, 1, '2024-12-20 10:00:00'),
(3, 4, '2024-04-10 16:00:00'),
(4, 1, '2024-12-20 11:30:00'),
(4, 9, '2024-12-21 15:00:00'),
(5, 2, '2024-12-19 17:30:00'),
(6, 1, '2024-12-20 13:00:00'),
(6, 10, '2024-05-18 10:00:00'),
(7, 1, '2024-12-20 14:00:00'),
(8, 4, '2024-04-10 17:00:00'),
(9, 7, '2024-12-16 15:00:00'),
(10, 4, '2024-04-10 18:00:00'),
(11, 9, '2024-12-21 16:00:00'),
(12, 1, '2024-12-20 15:00:00'),
(13, 4, '2024-04-10 19:00:00'),
(14, 7, '2024-12-16 16:00:00'),
(15, 9, '2024-12-21 17:00:00'),
(16, 4, '2024-04-10 20:00:00'),
(2, 1, '2024-12-20 16:00:00'),
(3, 7, '2024-12-16 17:00:00'),
(5, 4, '2024-04-10 21:00:00'),
(6, 9, '2024-12-21 18:00:00'),
(8, 1, '2024-12-20 17:00:00'),
(9, 4, '2024-04-10 22:00:00'),
(11, 7, '2024-12-16 18:00:00'),
(13, 9, '2024-12-21 19:00:00'),
(14, 1, '2024-12-20 18:00:00'),
(15, 4, '2024-04-10 23:00:00'),
(16, 7, '2024-12-16 19:00:00');

-- 插入评论点赞数据
INSERT INTO `comment_likes` (`user_id`, `comment_id`, `created_at`) VALUES
-- 对评论的点赞
(2, 1, '2024-12-20 11:00:00'),
(3, 1, '2024-12-20 11:15:00'),
(4, 1, '2024-12-20 11:30:00'),
(5, 1, '2024-12-20 11:45:00'),
(6, 1, '2024-12-20 12:00:00'),
(7, 2, '2024-12-20 12:15:00'),
(8, 2, '2024-12-20 12:30:00'),
(9, 2, '2024-12-20 12:45:00'),
(10, 3, '2024-12-20 13:00:00'),
(11, 3, '2024-12-20 13:15:00'),
(12, 4, '2024-04-10 15:00:00'),
(13, 4, '2024-04-10 15:15:00'),
(14, 4, '2024-04-10 15:30:00'),
(15, 4, '2024-04-10 15:45:00'),
(16, 4, '2024-04-10 16:00:00'),
(2, 4, '2024-04-10 16:15:00'),
(3, 4, '2024-04-10 16:30:00'),
(5, 4, '2024-04-10 16:45:00'),
(6, 5, '2024-04-10 17:00:00'),
(7, 5, '2024-04-10 17:15:00'),
(8, 5, '2024-04-10 17:30:00'),
(9, 5, '2024-04-10 17:45:00'),
(10, 6, '2024-12-16 14:00:00'),
(11, 6, '2024-12-16 14:15:00'),
(12, 6, '2024-12-16 14:30:00'),
(13, 7, '2024-12-16 15:00:00'),
(14, 8, '2024-12-21 16:00:00'),
(15, 8, '2024-12-21 16:15:00'),
(16, 8, '2024-12-21 16:30:00'),
(2, 8, '2024-12-21 16:45:00'),
(3, 9, '2024-12-21 17:00:00'),
(4, 9, '2024-12-21 17:15:00'),
(5, 9, '2024-12-21 17:30:00'),
(6, 9, '2024-12-21 17:45:00'),
(7, 9, '2024-12-21 18:00:00'),
(8, 9, '2024-12-21 18:15:00');

-- 插入学习资源数据
INSERT INTO `resources` (`user_id`, `title`, `description`, `file_url`, `file_size`, `file_type`, `category_id`, `download_count`, `like_count`, `status`) VALUES
(2, '数据结构课程设计报告', '包含完整的数据结构课程设计实验报告，涵盖栈、队列、树、图等数据结构的实现和应用。', '/files/data-structure-report.pdf', 2048576, 'application/pdf', 1, 15, 0, 1),
(3, '算法导论学习笔记', '详细的算法导论学习笔记，包含排序算法、图算法、动态规划等重要算法的分析和实现。', '/files/algorithm-notes.docx', 1572864, 'application/msword', 1, 25, 0, 1),
(2, '高等数学期末复习资料', '高等数学期末考试复习资料合集，包含重要公式、定理证明和典型习题解答。', '/files/calculus-review.pdf', 3145728, 'application/pdf', 1, 32, 0, 1),
(6, '宏观经济学PPT课件', '经济学专业课件，包含货币政策、财政政策等核心内容。', '/files/macro-economics.pptx', 5242880, 'application/vnd.ms-powerpoint', 1, 20, 0, 1),
(14, '校园生活指南', '新生校园生活指南，包含宿舍管理、食堂介绍、图书馆使用等实用信息。', '/files/campus-guide.pdf', 1048576, 'application/pdf', 2, 45, 0, 1),
(3, '计算机网络实验代码', '计算机网络课程实验代码合集，包含Socket编程、HTTP协议实现等。', '/files/network-lab-code.zip', 4194304, 'application/zip', 5, 18, 0, 1);

-- 插入课程数据
INSERT INTO `courses` (`user_id`, `name`, `teacher`, `location`, `day_of_week`, `start_time`, `end_time`, `start_week`, `end_week`, `semester`, `color`, `status`) VALUES
-- 数学专业学生(ID=3 czq2024)的课程
(3, '高等代数', '张教授', '数学学院楼201', 1, '08:00:00', '09:40:00', 1, 16, '2024-2025-2', '#409EFF', 1),
(3, '实变函数', '李老师', '数学学院楼301', 3, '14:00:00', '15:40:00', 1, 16, '2024-2025-2', '#67C23A', 1),
(3, '数学建模', '王教授', '计算中心机房', 5, '19:00:00', '21:00:00', 1, 16, '2024-2025-2', '#E6A23C', 1),

-- 计算机专业学生(ID=4 lihua_cs)的课程
(4, '数据结构与算法', '赵教授', '信息学部计算机楼', 2, '10:00:00', '11:40:00', 1, 16, '2024-2025-2', '#409EFF', 1),
(4, '软件工程', '钱老师', '信息学部B楼302', 4, '14:00:00', '15:40:00', 1, 16, '2024-2025-2', '#67C23A', 1),
(4, '算法竞赛训练', 'ACM教练', '信息学部机房', 6, '19:30:00', '21:30:00', 1, 16, '2024-2025-2', '#E6A23C', 1),

-- 法学专业学生(ID=5 wangming_law)的课程
(5, '民法学', '孙教授', '法学院模拟法庭', 1, '10:00:00', '11:40:00', 1, 16, '2024-2025-2', '#409EFF', 1),
(5, '法理学', '周老师', '法学院研讨室', 3, '15:50:00', '17:30:00', 1, 16, '2024-2025-2', '#67C23A', 1),

-- 化学专业学生(ID=6 zhangwei_chem)的课程
(6, '有机化学', '陈教授', '化学楼实验室', 2, '08:00:00', '09:40:00', 1, 16, '2024-2025-2', '#409EFF', 1),
(6, '物理化学', '刘老师', '化学楼201', 4, '14:00:00', '15:40:00', 1, 16, '2024-2025-2', '#67C23A', 1),

-- 经济学专业学生(ID=7 liuxin_econ)的课程
(7, '宏观经济学', '吴教授', '经管大楼B201', 1, '08:00:00', '09:40:00', 1, 16, '2024-2025-2', '#409EFF', 1),
(7, '计量经济学', '郑老师', '经管大楼机房', 2, '10:00:00', '11:40:00', 1, 16, '2024-2025-2', '#67C23A', 1);

-- 插入日程数据
INSERT INTO `schedules` (`user_id`, `title`, `description`, `start_time`, `end_time`, `location`, `is_all_day`, `reminder`, `color`, `status`) VALUES
-- 学习相关日程
(2, '高等代数期末复习', '准备高等代数期末考试，重点复习线性变换和特征值', '2025-01-10 19:00:00', '2025-01-10 22:00:00', '图书馆总馆3楼', 0, 30, '#409EFF', 1),
(3, '算法竞赛训练', 'ACM周赛讲解，动态规划专题', '2025-01-12 19:30:00', '2025-01-12 21:30:00', '信息学部机房', 0, 15, '#67C23A', 1),
(4, '模拟法庭准备', '准备"枫叶杯"模拟法庭大赛材料', '2025-01-08 14:00:00', '2025-01-08 17:00:00', '法学院研讨室', 0, 60, '#E6A23C', 1),

-- 社团活动
(14, '诗词社例会', '讨论新学期诗词创作活动安排', '2025-01-15 18:30:00', '2025-01-15 20:00:00', '樱园学生活动中心', 0, 30, '#909399', 1),
(16, '校报编辑部会议', '讨论下期专题策划和采访安排', '2025-01-16 17:00:00', '2025-01-16 18:30:00', '学生事务中心', 0, 15, '#909399', 1),

-- 实习实践
(6, '券商实习面试', '参加XX证券公司实习生面试', '2025-01-20 14:00:00', '2025-01-20 16:00:00', '金融街', 0, 60, '#F56C6C', 1),
(12, '医院见习', '跟随带教老师查房学习', '2025-01-18 07:30:00', '2025-01-18 12:00:00', '人民医院', 0, 120, '#C0C4CC', 1),

-- 个人安排
(2, '期末考试', '高等代数期末考试', '2025-01-25 08:00:00', '2025-01-25 10:00:00', '数学学院楼201', 0, 1440, '#F56C6C', 1),
(3, '项目答辩', '软件工程课程设计项目答辩', '2025-01-22 14:00:00', '2025-01-22 17:00:00', '信息学部B楼', 0, 720, '#E6A23C', 1),
(6, '考研复试准备', '准备经济学研究生复试材料', '2025-01-30 09:00:00', '2025-01-30 18:00:00', '图书馆', 1, 2880, '#9B59B6', 1);

-- ==========================================
-- 第五步：更新计数字段以保持数据一致性
-- ==========================================

-- 根据实际点赞数据更新帖子的点赞计数
UPDATE `posts` p SET 
    `like_count` = (
        SELECT COUNT(*) 
        FROM `post_likes` pl 
        WHERE pl.`post_id` = p.`id`
    );

-- 根据实际评论数据更新帖子的评论计数  
UPDATE `posts` p SET 
    `comment_count` = (
        SELECT COUNT(*) 
        FROM `comments` c 
        WHERE c.`post_id` = p.`id` AND c.`status` = 1
    );

-- 根据实际点赞数据更新评论的点赞计数
UPDATE `comments` c SET 
    `like_count` = (
        SELECT COUNT(*) 
        FROM `comment_likes` cl 
        WHERE cl.`comment_id` = c.`id`
    );

-- 根据实际点赞数据更新资源的点赞计数
UPDATE `resources` r SET 
    `like_count` = (
        SELECT COUNT(*) 
        FROM `resource_likes` rl 
        WHERE rl.`resource_id` = r.`id`
    );

-- ==========================================
-- 完成提示
-- ==========================================

SELECT 'UniLife数据库重建完成！所有表和测试数据已成功插入。' AS result;
SELECT '数据库结构：无外键约束，使用应用层维护数据一致性。' AS architecture;
SELECT '计数字段已根据实际数据自动更新，确保数据一致性。' AS consistency_check;
SELECT CONCAT('总共创建了 ', COUNT(*), ' 个表') AS table_count FROM information_schema.tables WHERE table_schema = 'UniLife';
SELECT '可以开始启动应用服务了！' AS next_step; 