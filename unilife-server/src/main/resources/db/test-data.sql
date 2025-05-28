-- 测试数据插入脚本
-- 使用数据库
USE UniLife;

-- 插入测试用户数据
INSERT INTO `users` (`username`, `email`, `password`, `nickname`, `role`, `status`, `is_verified`, `student_id`, `department`, `major`, `grade`) VALUES
('testuser1', 'test1@student.edu.cn', '123456', '张小明', 0, 1, 1, '2021001001', '计算机科学与技术学院', '计算机科学与技术', '2021'),
('testuser2', 'test2@student.edu.cn', '123456', '李小红', 0, 1, 1, '2021001002', '数学与统计学院', '数学与应用数学', '2021'),
('testuser3', 'test3@student.edu.cn', '123456', '王小刚', 0, 1, 1, '2021001003', '物理学院', '物理学', '2021');

-- 插入测试资源数据
INSERT INTO `resources` (`user_id`, `title`, `description`, `file_url`, `file_size`, `file_type`, `category_id`, `download_count`, `like_count`, `status`) VALUES
(2, '数据结构课程设计报告', '包含完整的数据结构课程设计实验报告，涵盖栈、队列、树、图等数据结构的实现和应用。', 'https://example.com/files/data-structure-report.pdf', 2048576, 'application/pdf', 1, 15, 8, 1),
(2, '算法导论学习笔记', '详细的算法导论学习笔记，包含排序算法、图算法、动态规划等重要算法的分析和实现。', 'https://example.com/files/algorithm-notes.docx', 1572864, 'application/vnd.openxmlformats-officedocument.wordprocessingml.document', 1, 25, 12, 1),
(3, '高等数学期末复习资料', '高等数学期末考试复习资料合集，包含重要公式、定理证明和典型习题解答。', 'https://example.com/files/calculus-review.pdf', 3145728, 'application/pdf', 1, 32, 18, 1),
(3, '线性代数PPT课件', '线性代数完整PPT课件，包含矩阵运算、向量空间、特征值等核心内容。', 'https://example.com/files/linear-algebra.pptx', 5242880, 'application/vnd.openxmlformats-officedocument.presentationml.presentation', 1, 20, 15, 1),
(4, '校园生活指南', '新生校园生活指南，包含宿舍管理、食堂介绍、图书馆使用等实用信息。', 'https://example.com/files/campus-guide.pdf', 1048576, 'application/pdf', 2, 45, 28, 1),
(2, '计算机网络实验代码', '计算机网络课程实验代码合集，包含Socket编程、HTTP协议实现等。', 'https://example.com/files/network-lab-code.zip', 4194304, 'application/zip', 5, 18, 10, 1);

-- 插入测试课程数据
INSERT INTO `courses` (`user_id`, `name`, `teacher`, `location`, `day_of_week`, `start_time`, `end_time`, `start_week`, `end_week`, `semester`, `color`, `status`) VALUES
(2, '数据结构', '张教授', '教学楼A201', 1, '08:00:00', '09:40:00', 1, 16, '2024-1', '#409EFF', 1),
(2, '算法设计与分析', '李老师', '教学楼B301', 3, '10:00:00', '11:40:00', 1, 16, '2024-1', '#67C23A', 1),
(2, '计算机网络', '王教授', '实验楼C102', 5, '14:00:00', '15:40:00', 1, 16, '2024-1', '#E6A23C', 1),
(3, '高等数学', '赵老师', '教学楼A101', 2, '08:00:00', '09:40:00', 1, 16, '2024-1', '#F56C6C', 1),
(3, '线性代数', '钱教授', '教学楼A203', 4, '10:00:00', '11:40:00', 1, 16, '2024-1', '#909399', 1),
(4, '大学物理', '孙老师', '物理楼101', 1, '14:00:00', '15:40:00', 1, 16, '2024-1', '#9B59B6', 1),
(4, '物理实验', '周教授', '物理实验室', 3, '16:00:00', '17:40:00', 1, 16, '2024-1', '#3498DB', 1);

-- 插入测试日程数据
INSERT INTO `schedules` (`user_id`, `title`, `description`, `start_time`, `end_time`, `location`, `is_all_day`, `reminder`, `color`, `status`) VALUES
(2, '期末考试复习', '准备数据结构期末考试', '2024-06-15 19:00:00', '2024-06-15 22:00:00', '图书馆', 0, 30, '#409EFF', 1),
(2, '项目讨论会', '讨论课程设计项目进展', '2024-06-20 14:00:00', '2024-06-20 16:00:00', '实验室', 0, 15, '#67C23A', 1),
(3, '社团活动', '参加数学建模社团活动', '2024-06-18 00:00:00', '2024-06-18 23:59:59', '学生活动中心', 1, 60, '#E6A23C', 1),
(3, '导师面谈', '与导师讨论学习进度', '2024-06-22 10:00:00', '2024-06-22 11:00:00', '办公楼A305', 0, 30, '#F56C6C', 1),
(4, '实验报告提交', '提交物理实验报告', '2024-06-25 23:59:00', '2024-06-25 23:59:59', '在线提交', 0, 1440, '#9B59B6', 1); 