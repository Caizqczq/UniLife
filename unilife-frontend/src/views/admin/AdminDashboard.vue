<template>
  <div class="admin-dashboard">
    <!-- 顶部导航 -->
    <div class="admin-header">
      <div class="header-left">
        <h1 class="admin-title">UniLife 管理后台</h1>
        <div class="system-status">
          <el-tag :type="systemStatus.online ? 'success' : 'danger'" size="small">
            {{ systemStatus.online ? '系统正常' : '系统异常' }}
          </el-tag>
          <span class="online-users">在线用户: {{ systemStatus.onlineUsers }}</span>
        </div>
      </div>
      <div class="header-right">
        <el-badge :value="unreadNotifications" class="notification-badge">
          <el-button @click="showNotifications = true" circle>
            <el-icon><Bell /></el-icon>
          </el-button>
        </el-badge>
        <span class="admin-user">{{ userStore.user?.nickname }}</span>
        <el-button @click="logout" type="danger" size="small">退出登录</el-button>
      </div>
    </div>

    <!-- 主要内容区域 -->
    <div class="admin-content">
      <!-- 侧边栏 -->
      <div class="admin-sidebar">
        <el-menu
          :default-active="activeMenu"
          class="admin-menu"
          @select="handleMenuSelect"
        >
          <el-menu-item index="dashboard">
            <el-icon><DataBoard /></el-icon>
            <span>数据概览</span>
          </el-menu-item>
          
          <el-sub-menu index="content">
            <template #title>
              <el-icon><Document /></el-icon>
              <span>内容管理</span>
            </template>
            <el-menu-item index="posts">帖子管理</el-menu-item>
            <el-menu-item index="comments">评论管理</el-menu-item>
            <el-menu-item index="categories">分类管理</el-menu-item>
            <el-menu-item index="resources">资源管理</el-menu-item>
          </el-sub-menu>
          
          <el-menu-item index="schedules">
            <el-icon><Calendar /></el-icon>
            <span>课表管理</span>
          </el-menu-item>
          
          <el-sub-menu index="user-management">
            <template #title>
              <el-icon><User /></el-icon>
              <span>用户管理</span>
            </template>
            <el-menu-item index="users">用户列表</el-menu-item>
          </el-sub-menu>
          
          <el-sub-menu index="system">
            <template #title>
              <el-icon><Setting /></el-icon>
              <span>系统管理</span>
            </template>
            <el-menu-item index="monitor">系统监控</el-menu-item>
          </el-sub-menu>
          
          <el-menu-item index="statistics">
            <el-icon><TrendCharts /></el-icon>
            <span>数据统计</span>
          </el-menu-item>
        </el-menu>
      </div>

      <!-- 主内容区 -->
      <div class="admin-main">
        <!-- 数据概览 -->
        <div v-if="activeMenu === 'dashboard'" class="dashboard-content">
          <h2>系统数据概览</h2>
          <div class="stats-grid">
            <div class="stat-card">
              <div class="stat-icon user-icon">
                <el-icon><User /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-number">{{ stats.totalUsers || 0 }}</div>
                <div class="stat-label">总用户数</div>
                <div class="stat-trend" :class="stats.userGrowth > 0 ? 'positive' : 'negative'">
                  {{ stats.userGrowth > 0 ? '↗' : '↘' }} {{ Math.abs(stats.userGrowth) }}%
                </div>
              </div>
            </div>
            <div class="stat-card">
              <div class="stat-icon post-icon">
                <el-icon><Document /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-number">{{ stats.totalPosts || 0 }}</div>
                <div class="stat-label">总帖子数</div>
                <div class="stat-trend" :class="stats.postGrowth > 0 ? 'positive' : 'negative'">
                  {{ stats.postGrowth > 0 ? '↗' : '↘' }} {{ Math.abs(stats.postGrowth) }}%
                </div>
              </div>
            </div>
            <div class="stat-card">
              <div class="stat-icon comment-icon">
                <el-icon><ChatDotRound /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-number">{{ stats.totalComments || 0 }}</div>
                <div class="stat-label">总评论数</div>
                <div class="stat-trend" :class="stats.commentGrowth > 0 ? 'positive' : 'negative'">
                  {{ stats.commentGrowth > 0 ? '↗' : '↘' }} {{ Math.abs(stats.commentGrowth) }}%
                </div>
              </div>
            </div>
            <div class="stat-card">
              <div class="stat-icon resource-icon">
                <el-icon><Files /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-number">{{ stats.totalResources || 0 }}</div>
                <div class="stat-label">总资源数</div>
                <div class="stat-trend" :class="stats.resourceGrowth > 0 ? 'positive' : 'negative'">
                  {{ stats.resourceGrowth > 0 ? '↗' : '↘' }} {{ Math.abs(stats.resourceGrowth) }}%
                </div>
              </div>
            </div>
            <div class="stat-card">
              <div class="stat-icon course-icon">
                <el-icon><Calendar /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-number">{{ stats.totalCourses || 0 }}</div>
                <div class="stat-label">总课程数</div>
                <div class="stat-trend" :class="stats.courseGrowth > 0 ? 'positive' : 'negative'">
                  {{ stats.courseGrowth > 0 ? '↗' : '↘' }} {{ Math.abs(stats.courseGrowth) }}%
                </div>
              </div>
            </div>
          </div>
          
          <!-- 快速操作 -->
          <div class="quick-actions">
            <h3>快速操作</h3>
            <div class="action-cards">
              <div class="action-card" @click="activeMenu = 'users'">
                <el-icon><User /></el-icon>
                <span>管理用户</span>
              </div>
              <div class="action-card" @click="activeMenu = 'posts'">
                <el-icon><Document /></el-icon>
                <span>管理帖子</span>
              </div>
              <div class="action-card" @click="activeMenu = 'announcements'">
                <el-icon><Bell /></el-icon>
                <span>发布公告</span>
              </div>
              <div class="action-card" @click="activeMenu = 'settings'">
                <el-icon><Setting /></el-icon>
                <span>系统设置</span>
              </div>
            </div>
          </div>

          <!-- 实时活动 -->
          <div class="recent-activities">
            <h3>最近活动</h3>
            <div class="activity-list">
              <div v-for="activity in recentActivities" :key="activity.id" class="activity-item">
                <div class="activity-icon" :class="activity.type">
                  <el-icon v-if="activity.type === 'user'"><User /></el-icon>
                  <el-icon v-else-if="activity.type === 'post'"><Document /></el-icon>
                  <el-icon v-else-if="activity.type === 'comment'"><ChatDotRound /></el-icon>
                  <el-icon v-else><Files /></el-icon>
                </div>
                <div class="activity-content">
                  <div class="activity-text">{{ activity.description }}</div>
                  <div class="activity-time">{{ activity.time }}</div>
                </div>
              </div>
              <div v-if="recentActivities.length === 0" class="no-activity">
                <el-empty description="暂无活动数据" />
              </div>
            </div>
          </div>
        </div>

        <!-- 系统监控 -->
        <div v-if="activeMenu === 'monitor'" class="monitor-content">
          <h2>系统监控</h2>
          
          <div class="monitor-grid">
            <div class="monitor-card">
              <h3>应用状态</h3>
              <div class="monitor-item">
                <span>系统状态:</span>
                <span class="monitor-value" :style="{ color: systemStatus.online ? '#67c23a' : '#f56c6c' }">
                  {{ systemStatus.online ? '在线' : '离线' }}
                </span>
              </div>
              <div class="monitor-item">
                <span>在线用户:</span>
                <span class="monitor-value">{{ systemStatus.onlineUsers }}</span>
              </div>
            </div>
          </div>
        </div>

        <!-- 日志管理 -->
        <div v-if="activeMenu === 'logs'" class="logs-content">
          <h2>日志管理</h2>
          
          <div class="logs-toolbar">
            <el-select v-model="logLevel" placeholder="日志级别" @change="loadLogs">
              <el-option label="全部" value="" />
              <el-option label="ERROR" value="ERROR" />
              <el-option label="WARN" value="WARN" />
              <el-option label="INFO" value="INFO" />
              <el-option label="DEBUG" value="DEBUG" />
            </el-select>
            <el-date-picker
              v-model="logDateRange"
              type="daterange"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              @change="loadLogs"
            />
            <el-input
              v-model="logSearch"
              placeholder="搜索日志内容..."
              style="width: 300px"
              @input="loadLogs"
            />
          </div>
          
          <el-table :data="logs" style="width: 100%" max-height="600">
            <el-table-column prop="timestamp" label="时间" width="180" />
            <el-table-column prop="level" label="级别" width="80">
              <template #default="scope">
                <el-tag :type="getLogLevelType(scope.row.level)" size="small">
                  {{ scope.row.level }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="logger" label="模块" width="200" />
            <el-table-column prop="message" label="消息" show-overflow-tooltip />
            <el-table-column label="操作" width="120">
              <template #default="scope">
                <el-button size="small" @click="viewLogDetail(scope.row)">详情</el-button>
              </template>
            </el-table-column>
          </el-table>
          
          <el-pagination
            v-model:current-page="logPage"
            v-model:page-size="logPageSize"
            :total="logTotal"
            @current-change="loadLogs"
            layout="total, prev, pager, next"
          />
        </div>

        <!-- 系统设置 -->
        <div v-if="activeMenu === 'settings'" class="settings-content">
          <h2>系统设置</h2>
          
          <el-tabs v-model="activeSettingTab">
            <el-tab-pane label="网站设置" name="website">
              <el-form :model="websiteSettings" label-width="120px">
                <el-form-item label="网站名称">
                  <el-input v-model="websiteSettings.siteName" />
                </el-form-item>
                <el-form-item label="网站描述">
                  <el-input v-model="websiteSettings.siteDescription" type="textarea" />
                </el-form-item>
                <el-form-item label="网站关键词">
                  <el-input v-model="websiteSettings.siteKeywords" />
                </el-form-item>
                <el-form-item label="维护模式">
                  <el-switch v-model="websiteSettings.maintenanceMode" />
                </el-form-item>
                <el-form-item label="允许注册">
                  <el-switch v-model="websiteSettings.allowRegistration" />
                </el-form-item>
                <el-form-item>
                  <el-button type="primary" @click="saveWebsiteSettings">保存设置</el-button>
                </el-form-item>
              </el-form>
            </el-tab-pane>
            
            <el-tab-pane label="邮件设置" name="email">
              <el-form :model="emailSettings" label-width="120px">
                <el-form-item label="SMTP服务器">
                  <el-input v-model="emailSettings.smtpHost" />
                </el-form-item>
                <el-form-item label="SMTP端口">
                  <el-input v-model="emailSettings.smtpPort" type="number" />
                </el-form-item>
                <el-form-item label="发件邮箱">
                  <el-input v-model="emailSettings.fromEmail" />
                </el-form-item>
                <el-form-item label="邮箱密码">
                  <el-input v-model="emailSettings.password" type="password" />
                </el-form-item>
                <el-form-item label="启用SSL">
                  <el-switch v-model="emailSettings.enableSSL" />
                </el-form-item>
                <el-form-item>
                  <el-button type="primary" @click="saveEmailSettings">保存设置</el-button>
                  <el-button @click="testEmail">测试邮件</el-button>
                </el-form-item>
              </el-form>
            </el-tab-pane>
            
            <el-tab-pane label="安全设置" name="security">
              <el-form :model="securitySettings" label-width="120px">
                <el-form-item label="登录失败限制">
                  <el-input v-model="securitySettings.maxLoginAttempts" type="number" />
                  <div class="form-tip">连续登录失败超过此次数将锁定账户</div>
                </el-form-item>
                <el-form-item label="锁定时间(分钟)">
                  <el-input v-model="securitySettings.lockoutDuration" type="number" />
                </el-form-item>
                <el-form-item label="会话超时(小时)">
                  <el-input v-model="securitySettings.sessionTimeout" type="number" />
                </el-form-item>
                <el-form-item label="强制HTTPS">
                  <el-switch v-model="securitySettings.forceHttps" />
                </el-form-item>
                <el-form-item>
                  <el-button type="primary" @click="saveSecuritySettings">保存设置</el-button>
                </el-form-item>
              </el-form>
            </el-tab-pane>
          </el-tabs>
        </div>

        <!-- 系统公告 -->
        <div v-if="activeMenu === 'announcements'" class="announcements-content">
          <h2>系统公告</h2>
          
          <div class="table-toolbar">
            <el-button type="primary" @click="showCreateAnnouncementDialog">
              <el-icon><Plus /></el-icon>
              发布公告
            </el-button>
          </div>
          
          <el-table :data="announcements" style="width: 100%">
            <el-table-column prop="id" label="ID" width="80" />
            <el-table-column prop="title" label="标题" width="200" />
            <el-table-column prop="type" label="类型" width="100">
              <template #default="scope">
                <el-tag :type="getAnnouncementTypeColor(scope.row.type)">
                  {{ getAnnouncementTypeText(scope.row.type) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="status" label="状态" width="100">
              <template #default="scope">
                <el-tag :type="scope.row.status === 1 ? 'success' : 'info'">
                  {{ scope.row.status === 1 ? '发布中' : '草稿' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="startTime" label="开始时间" width="180" />
            <el-table-column prop="endTime" label="结束时间" width="180" />
            <el-table-column prop="createdAt" label="创建时间" width="180" />
            <el-table-column label="操作" width="200">
              <template #default="scope">
                <el-button size="small" @click="editAnnouncement(scope.row)">编辑</el-button>
                <el-button 
                  size="small" 
                  :type="scope.row.status === 1 ? 'warning' : 'success'"
                  @click="toggleAnnouncementStatus(scope.row)"
                >
                  {{ scope.row.status === 1 ? '下线' : '发布' }}
                </el-button>
                <el-button size="small" type="danger" @click="deleteAnnouncement(scope.row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>

        <!-- 数据统计 -->
        <div v-if="activeMenu === 'statistics'" class="statistics-content">
          <h2>数据统计</h2>
          
          <div class="chart-container">
            <div class="chart-card">
              <h3>用户增长趋势</h3>
              <div id="user-growth-chart" class="chart"></div>
            </div>
            
            <div class="chart-card">
              <h3>内容发布统计</h3>
              <div id="content-stats-chart" class="chart"></div>
            </div>
            
            <div class="chart-card">
              <h3>活跃度分析</h3>
              <div id="activity-chart" class="chart"></div>
            </div>
          </div>
        </div>

        <!-- 用户管理 -->
        <div v-if="activeMenu === 'users'" class="users-content">
          <h2>用户管理</h2>
          <div class="table-toolbar">
            <el-input
              v-model="userSearch"
              placeholder="搜索用户..."
              style="width: 300px"
              @input="searchUsers"
            />
            <el-select v-model="userRoleFilter" placeholder="角色筛选" @change="searchUsers">
              <el-option label="全部" :value="null" />
              <el-option label="普通用户" :value="0" />
              <el-option label="版主" :value="1" />
              <el-option label="管理员" :value="2" />
            </el-select>
          </div>
          <el-table :data="users" style="width: 100%">
            <el-table-column prop="id" label="ID" width="80" />
            <el-table-column prop="username" label="用户名" width="120" />
            <el-table-column prop="nickname" label="昵称" width="120" />
            <el-table-column prop="email" label="邮箱" width="200" />
            <el-table-column prop="role" label="角色" width="100">
              <template #default="scope">
                <el-tag :type="getRoleType(scope.row.role)">
                  {{ getRoleText(scope.row.role) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="status" label="状态" width="100">
              <template #default="scope">
                <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
                  {{ scope.row.status === 1 ? '正常' : '禁用' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="createdAt" label="注册时间" width="180" />
            <el-table-column label="操作" width="200">
              <template #default="scope">
                <el-button
                  size="small"
                  :type="scope.row.status === 1 ? 'warning' : 'success'"
                  @click="toggleUserStatus(scope.row)"
                >
                  {{ scope.row.status === 1 ? '禁用' : '启用' }}
                </el-button>
                <el-button
                  size="small"
                  type="danger"
                  @click="deleteUser(scope.row)"
                  :disabled="scope.row.role === 2"
                >
                  删除
                </el-button>
              </template>
            </el-table-column>
          </el-table>
          <el-pagination
            v-model:current-page="userPage"
            v-model:page-size="userPageSize"
            :total="userTotal"
            @current-change="loadUsers"
            layout="total, prev, pager, next"
          />
        </div>



        <!-- 帖子管理 -->
        <div v-if="activeMenu === 'posts'" class="posts-content">
          <h2>帖子管理</h2>
          <div class="table-toolbar">
            <el-input
              v-model="postSearch"
              placeholder="搜索帖子..."
              style="width: 300px"
              @input="searchPosts"
            />
            <el-select v-model="postCategoryFilter" placeholder="分类筛选" @change="searchPosts">
              <el-option label="全部" :value="null" />
              <el-option 
                v-for="category in categories" 
                :key="category.id" 
                :label="category.name" 
                :value="category.id" 
              />
            </el-select>
            <el-select v-model="postStatusFilter" placeholder="状态筛选" @change="searchPosts">
              <el-option label="全部" :value="null" />
              <el-option label="正常" :value="1" />
              <el-option label="置顶" :value="2" />
              <el-option label="已删除" :value="0" />
            </el-select>
          </div>
          <el-table :data="posts" style="width: 100%">
            <el-table-column prop="id" label="ID" width="80" />
            <el-table-column prop="title" label="标题" width="200" show-overflow-tooltip />
            <el-table-column prop="nickname" label="作者" width="120" />
            <el-table-column prop="categoryName" label="分类" width="100" />
            <el-table-column prop="viewCount" label="浏览" width="80" />
            <el-table-column prop="likeCount" label="点赞" width="80" />
            <el-table-column prop="commentCount" label="评论" width="80" />
            <el-table-column prop="status" label="状态" width="100">
              <template #default="scope">
                <el-tag :type="getPostStatusType(scope.row.status)">
                  {{ getPostStatusText(scope.row.status) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="createdAt" label="发布时间" width="180" />
            <el-table-column label="操作" width="250">
              <template #default="scope">
                <!-- 已删除的帖子显示恢复按钮 -->
                <template v-if="scope.row.status === 0">
                  <el-button
                    size="small"
                    type="success"
                    @click="restorePost(scope.row)"
                  >
                    恢复
                  </el-button>
                  <el-button
                    size="small"
                    type="danger"
                    @click="permanentDeletePost(scope.row)"
                  >
                    永久删除
                  </el-button>
                </template>
                <!-- 正常和置顶的帖子显示置顶和删除按钮 -->
                <template v-else>
                  <el-button
                    size="small"
                    :type="scope.row.status === 2 ? 'warning' : 'primary'"
                    @click="togglePostTop(scope.row)"
                  >
                    {{ scope.row.status === 2 ? '取消置顶' : '置顶' }}
                  </el-button>
                  <el-button
                    size="small"
                    type="danger"
                    @click="deletePost(scope.row)"
                  >
                    删除
                  </el-button>
                </template>
              </template>
            </el-table-column>
          </el-table>
          <el-pagination
            v-model:current-page="postPage"
            v-model:page-size="postPageSize"
            :total="postTotal"
            @current-change="loadPosts"
            layout="total, prev, pager, next"
          />
        </div>

        <!-- 评论管理 -->
        <div v-if="activeMenu === 'comments'" class="comments-content">
          <h2>评论管理</h2>
          <div class="table-toolbar">
            <el-input
              v-model="commentSearch"
              placeholder="搜索评论..."
              style="width: 300px"
              @input="searchComments"
            />
            <el-select v-model="commentStatusFilter" placeholder="状态筛选" @change="searchComments">
              <el-option label="全部" :value="null" />
              <el-option label="正常" :value="1" />
              <el-option label="已删除" :value="0" />
            </el-select>
          </div>
          <el-table :data="comments" style="width: 100%">
            <el-table-column prop="id" label="ID" width="80" />
            <el-table-column prop="content" label="内容" width="300" show-overflow-tooltip />
            <el-table-column prop="nickname" label="评论者" width="120" />
            <el-table-column prop="postTitle" label="所属帖子" width="200" show-overflow-tooltip />
            <el-table-column prop="likeCount" label="点赞" width="80" />
            <el-table-column prop="status" label="状态" width="100">
              <template #default="scope">
                <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
                  {{ scope.row.status === 1 ? '正常' : '已删除' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="createdAt" label="发布时间" width="180" />
            <el-table-column label="操作" width="120">
              <template #default="scope">
                <el-button
                  size="small"
                  type="danger"
                  @click="deleteComment(scope.row)"
                >
                  删除
                </el-button>
              </template>
            </el-table-column>
          </el-table>
          <el-pagination
            v-model:current-page="commentPage"
            v-model:page-size="commentPageSize"
            :total="commentTotal"
            @current-change="loadComments"
            layout="total, prev, pager, next"
          />
        </div>

        <!-- 分类管理 -->
        <div v-if="activeMenu === 'categories'" class="categories-content">
          <h2>分类管理</h2>
          <div class="table-toolbar">
            <el-button type="primary" @click="showCreateCategoryDialog">
              <el-icon><Plus /></el-icon>
              新增分类
            </el-button>
          </div>
          <el-table :data="categories" style="width: 100%">
            <el-table-column prop="id" label="ID" width="80" />
            <el-table-column prop="name" label="分类名称" width="150" />
            <el-table-column prop="description" label="描述" width="200" show-overflow-tooltip />
            <el-table-column prop="icon" label="图标" width="80" />
            <el-table-column prop="sort" label="排序" width="80" />
            <el-table-column prop="status" label="状态" width="100">
              <template #default="scope">
                <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
                  {{ scope.row.status === 1 ? '启用' : '禁用' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="createdAt" label="创建时间" width="180" />
            <el-table-column label="操作" width="200">
              <template #default="scope">
                <el-button
                  size="small"
                  type="primary"
                  @click="editCategory(scope.row)"
                >
                  编辑
                </el-button>
                <el-button
                  size="small"
                  type="danger"
                  @click="deleteCategory(scope.row)"
                >
                  删除
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>

        <!-- 资源管理 -->
        <div v-if="activeMenu === 'resources'" class="resources-content">
          <h2>资源管理</h2>
          <div class="table-toolbar">
            <el-input
              v-model="resourceSearch"
              placeholder="搜索资源..."
              style="width: 300px"
              @input="searchResources"
            />
            <el-select v-model="resourceCategoryFilter" placeholder="分类筛选" @change="searchResources">
              <el-option label="全部" :value="null" />
              <el-option 
                v-for="category in categories" 
                :key="category.id" 
                :label="category.name" 
                :value="category.id" 
              />
            </el-select>
            <el-select v-model="resourceStatusFilter" placeholder="状态筛选" @change="searchResources">
              <el-option label="全部" :value="null" />
              <el-option label="正常" :value="1" />
              <el-option label="已删除" :value="0" />
            </el-select>
          </div>
          <el-table :data="resources" style="width: 100%">
            <el-table-column prop="id" label="ID" width="80" />
            <el-table-column prop="title" label="标题" width="200" show-overflow-tooltip />
            <el-table-column prop="nickname" label="上传者" width="120" />
            <el-table-column prop="categoryName" label="分类" width="100" />
            <el-table-column prop="fileType" label="文件类型" width="120" />
            <el-table-column prop="fileSize" label="文件大小" width="100">
              <template #default="scope">
                {{ formatFileSize(scope.row.fileSize) }}
              </template>
            </el-table-column>
            <el-table-column prop="downloadCount" label="下载" width="80" />
            <el-table-column prop="likeCount" label="点赞" width="80" />
            <el-table-column prop="status" label="状态" width="100">
              <template #default="scope">
                <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
                  {{ scope.row.status === 1 ? '正常' : '已删除' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="createdAt" label="上传时间" width="180" />
            <el-table-column label="操作" width="120">
              <template #default="scope">
                <el-button
                  size="small"
                  type="danger"
                  @click="deleteResource(scope.row)"
                >
                  删除
                </el-button>
              </template>
            </el-table-column>
          </el-table>
          <el-pagination
            v-model:current-page="resourcePage"
            v-model:page-size="resourcePageSize"
            :total="resourceTotal"
            @current-change="loadResources"
            layout="total, prev, pager, next"
          />
        </div>

        <!-- 用户课表管理 -->
        <div v-if="activeMenu === 'schedules'" class="schedules-content">
          <h2>用户课表管理</h2>
          
          <!-- 用户选择和操作工具栏 -->
          <div class="schedule-toolbar">
            <div class="user-selector">
              <span>选择用户：</span>
              <el-select
                v-model="selectedUser"
                placeholder="请选择用户"
                filterable
                clearable
                style="width: 300px"
                @change="loadSchedules"
              >
                <el-option
                  v-for="user in users"
                  :key="user.id"
                  :label="`${user.nickname || user.username} (${user.studentId}) - ${user.department || ''}${user.major || ''}`"
                  :value="user"
                />
              </el-select>
            </div>
            <div class="semester-selector">
              <span>学期：</span>
              <el-input
                v-model="selectedSemester"
                placeholder="学期 (如: 2024-2025-2)"
                style="width: 200px"
                @change="loadSchedules"
              />
            </div>
            <div class="schedule-actions" v-if="selectedUser">
              <el-button type="primary" @click="showCourseDialog = true">
                <el-icon><Plus /></el-icon>
                添加课程
              </el-button>
            </div>
          </div>

          <!-- 课表展示 -->
          <div v-if="userSchedule" class="schedule-display">
            <div class="schedule-header">
              <h3>{{ userSchedule.user.nickname || userSchedule.user.username }} 的课表</h3>
              <div class="user-info">
                <el-tag>学号: {{ userSchedule.user.studentId }}</el-tag>
                <el-tag>院系: {{ userSchedule.user.department }}</el-tag>
                <el-tag>专业: {{ userSchedule.user.major }}</el-tag>
                <el-tag>年级: {{ userSchedule.user.grade }}</el-tag>
                <el-tag>学期: {{ userSchedule.semester }}</el-tag>
                <el-tag type="success">总课程数: {{ userSchedule.totalCourses }}</el-tag>
              </div>
            </div>
            
            <!-- 课程列表视图 -->
            <div class="course-list-view">
              <h4>课程列表</h4>
              <el-table :data="allCourses" style="width: 100%" size="small">
                <el-table-column prop="id" label="ID" width="60" />
                <el-table-column prop="name" label="课程名称" width="150" show-overflow-tooltip />
                <el-table-column prop="teacher" label="教师" width="100" />
                <el-table-column prop="location" label="地点" width="120" />
                <el-table-column label="时间" width="200">
                  <template #default="scope">
                    <div>
                      <div>{{ getDayOfWeekName(scope.row.dayOfWeek) }} {{ scope.row.startTime }}-{{ scope.row.endTime }}</div>
                      <div style="font-size: 12px; color: #666;">第{{ scope.row.startWeek }}-{{ scope.row.endWeek }}周</div>
                    </div>
                  </template>
                </el-table-column>
                <el-table-column prop="status" label="状态" width="100">
                  <template #default="scope">
                    <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'" size="small">
                      {{ scope.row.status === 1 ? '正常' : '已删除' }}
                    </el-tag>
                  </template>
                </el-table-column>
                <el-table-column label="操作" width="150">
                  <template #default="scope">
                    <el-button
                      size="small"
                      @click="editCourse(scope.row)"
                    >
                      编辑
                    </el-button>
                    <el-button
                      size="small"
                      type="danger"
                      @click="deleteCourse(scope.row)"
                    >
                      删除
                    </el-button>
                  </template>
                </el-table-column>
              </el-table>
            </div>
            
            <!-- 课表网格视图 -->
            <div class="schedule-table">
              <h4>课表视图</h4>
              <table class="week-schedule">
                <thead>
                  <tr>
                    <th>时间</th>
                    <th>周一</th>
                    <th>周二</th>
                    <th>周三</th>
                    <th>周四</th>
                    <th>周五</th>
                    <th>周六</th>
                    <th>周日</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="timeSlot in timeSlots" :key="timeSlot.name">
                    <td class="time-slot">{{ timeSlot.name }}<br/>{{ timeSlot.time }}</td>
                    <td v-for="day in 7" :key="day" class="course-cell">
                      <div 
                        v-for="course in getCoursesForTimeSlot(day, timeSlot)" 
                        :key="course.id" 
                        class="course-item"
                        :style="{ backgroundColor: course.color || '#409eff', color: 'white' }"
                        @click="editCourse(course)"
                      >
                        <div class="course-name">{{ course.name }}</div>
                        <div class="course-teacher">{{ course.teacher }}</div>
                        <div class="course-location">{{ course.location }}</div>
                        <div class="course-weeks">{{ course.startWeek }}-{{ course.endWeek }}周</div>
                      </div>
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>
          
          <el-empty v-else-if="!scheduleLoading" description="请选择用户查看课表" />
          <div v-if="scheduleLoading" style="text-align: center; padding: 50px;">
            <el-icon class="is-loading"><Loading /></el-icon>
            <p>加载中...</p>
          </div>
        </div>

        <!-- 课程编辑对话框 -->
        <el-dialog
          v-model="showCourseDialog"
          :title="editingCourse.id ? '编辑课程' : '添加课程'"
          width="600px"
          @close="resetCourseForm"
        >
          <el-form :model="editingCourse" :rules="courseRules" ref="courseFormRef" label-width="100px">
            <el-form-item label="课程名称" prop="name">
              <el-input v-model="editingCourse.name" placeholder="请输入课程名称" />
            </el-form-item>
            <el-form-item label="教师" prop="teacher">
              <el-input v-model="editingCourse.teacher" placeholder="请输入教师姓名" />
            </el-form-item>
            <el-form-item label="上课地点" prop="location">
              <el-input v-model="editingCourse.location" placeholder="请输入上课地点" />
            </el-form-item>
            <el-form-item label="星期" prop="dayOfWeek">
              <el-select v-model="editingCourse.dayOfWeek" placeholder="选择星期">
                <el-option label="周一" :value="1" />
                <el-option label="周二" :value="2" />
                <el-option label="周三" :value="3" />
                <el-option label="周四" :value="4" />
                <el-option label="周五" :value="5" />
                <el-option label="周六" :value="6" />
                <el-option label="周日" :value="7" />
              </el-select>
            </el-form-item>
            <el-form-item label="上课时间" required>
              <div style="display: flex; gap: 10px; align-items: center;">
                <el-time-picker
                  v-model="editingCourse.startTime"
                  placeholder="开始时间"
                  format="HH:mm"
                  value-format="HH:mm"
                  style="width: 120px"
                />
                <span>至</span>
                <el-time-picker
                  v-model="editingCourse.endTime"
                  placeholder="结束时间"
                  format="HH:mm"
                  value-format="HH:mm"
                  style="width: 120px"
                />
              </div>
            </el-form-item>
            <el-form-item label="周次范围" required>
              <div style="display: flex; gap: 10px; align-items: center;">
                <el-input-number
                  v-model="editingCourse.startWeek"
                  :min="1"
                  :max="30"
                  placeholder="开始周"
                  style="width: 120px"
                />
                <span>至</span>
                <el-input-number
                  v-model="editingCourse.endWeek"
                  :min="1"
                  :max="30"
                  placeholder="结束周"
                  style="width: 120px"
                />
                <span>周</span>
              </div>
            </el-form-item>
            <el-form-item label="学期" prop="semester">
              <el-input v-model="editingCourse.semester" placeholder="如：2024-2025-2" />
            </el-form-item>
            <el-form-item label="颜色">
              <el-color-picker v-model="editingCourse.color" />
            </el-form-item>
          </el-form>
          
          <template #footer>
            <el-button @click="showCourseDialog = false">取消</el-button>
            <el-button type="primary" @click="saveCourse" :loading="isSavingCourse">
              {{ editingCourse.id ? '更新' : '添加' }}
            </el-button>
          </template>
        </el-dialog>
      </div>
    </div>

    <!-- 公告编辑对话框 -->
    <el-dialog
      v-model="showAnnouncementDialog"
      :title="editingAnnouncement.id ? '编辑公告' : '发布公告'"
      width="600px"
    >
      <el-form :model="editingAnnouncement" label-width="100px">
        <el-form-item label="公告标题" required>
          <el-input v-model="editingAnnouncement.title" />
        </el-form-item>
        <el-form-item label="公告类型" required>
          <el-select v-model="editingAnnouncement.type">
            <el-option label="系统通知" value="system" />
            <el-option label="功能更新" value="feature" />
            <el-option label="维护通知" value="maintenance" />
            <el-option label="紧急通知" value="urgent" />
          </el-select>
        </el-form-item>
        <el-form-item label="公告内容" required>
          <el-input v-model="editingAnnouncement.content" type="textarea" :rows="6" />
        </el-form-item>
        <el-form-item label="开始时间">
          <el-date-picker
            v-model="editingAnnouncement.startTime"
            type="datetime"
            placeholder="选择开始时间"
          />
        </el-form-item>
        <el-form-item label="结束时间">
          <el-date-picker
            v-model="editingAnnouncement.endTime"
            type="datetime"
            placeholder="选择结束时间"
          />
        </el-form-item>
        <el-form-item label="是否置顶">
          <el-switch v-model="editingAnnouncement.isTop" />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="showAnnouncementDialog = false">取消</el-button>
        <el-button @click="saveAnnouncementAsDraft">保存草稿</el-button>
        <el-button type="primary" @click="publishAnnouncement">立即发布</el-button>
      </template>
    </el-dialog>

    <!-- 通知对话框 -->
    <el-dialog v-model="showNotifications" title="通知中心" width="400px">
      <div class="notification-list">
        <div v-for="notification in notifications" :key="notification.id" class="notification-item">
          <div class="notification-content">
            <div class="notification-title">{{ notification.title }}</div>
            <div class="notification-message">{{ notification.message }}</div>
            <div class="notification-time">{{ notification.time }}</div>
          </div>
          <el-button size="small" @click="markAsRead(notification.id)">标记已读</el-button>
        </div>
      </div>
    </el-dialog>

    <!-- 分类编辑对话框 -->
    <el-dialog
      v-model="categoryDialogVisible"
      :title="isEditingCategory ? '编辑分类' : '新增分类'"
      width="500px"
    >
      <el-form :model="categoryForm" :rules="categoryRules" ref="categoryFormRef" label-width="80px">
        <el-form-item label="分类名称" prop="name">
          <el-input v-model="categoryForm.name" placeholder="请输入分类名称" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="categoryForm.description" type="textarea" placeholder="请输入分类描述" />
        </el-form-item>
        <el-form-item label="图标" prop="icon">
          <el-input v-model="categoryForm.icon" placeholder="请输入图标（如：📚）" />
        </el-form-item>
        <el-form-item label="排序" prop="sort">
          <el-input-number v-model="categoryForm.sort" :min="1" :max="100" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="categoryForm.status">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="categoryDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="saveCategoryForm" :loading="isSavingCategory">
            {{ isEditingCategory ? '更新' : '创建' }}
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  DataBoard,
  User,
  Document,
  ChatDotRound,
  FolderOpened,
  Files,
  Plus,
  Bell,
  Setting,
  TrendCharts
} from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import { adminApi } from '@/api/admin'

// 定义统计数据接口
interface SystemStats {
  totalUsers?: number
  totalPosts?: number
  totalComments?: number
  totalResources?: number
  totalCourses?: number
  userGrowth: number
  postGrowth: number
  commentGrowth: number
  resourceGrowth: number
  courseGrowth: number
}

// 定义分类接口
interface Category {
  id: number
  name: string
  description?: string
  icon?: string
  sort: number
  status: number
  createdAt?: string
  updatedAt?: string
}

// 定义分类表单接口
interface CategoryForm {
  id: number | null
  name: string
  description: string
  icon: string
  sort: number
  status: number
}

const router = useRouter()
const userStore = useUserStore()

// 当前激活的菜单
const activeMenu = ref('dashboard')

// 统计数据
const stats = ref<SystemStats>({
  totalUsers: 0,
  totalPosts: 0,
  totalComments: 0,
  totalResources: 0,
  totalCourses: 0,
  userGrowth: 0,
  postGrowth: 0,
  commentGrowth: 0,
  resourceGrowth: 0,
  courseGrowth: 0
})

// 用户管理相关
const users = ref<any[]>([])
const userSearch = ref('')
const userRoleFilter = ref(null)
const userPage = ref(1)
const userPageSize = ref(10)
const userTotal = ref(0)

// 帖子管理相关
const posts = ref<any[]>([])
const postSearch = ref('')
const postCategoryFilter = ref<number | null>(null)
const postStatusFilter = ref<number | null>(null)
const postPage = ref(1)
const postPageSize = ref(10)
const postTotal = ref(0)

// 评论管理相关
const comments = ref<any[]>([])
const commentSearch = ref('')
const commentStatusFilter = ref<number | null>(null)
const commentPage = ref(1)
const commentPageSize = ref(10)
const commentTotal = ref(0)

// 分类管理相关
const categories = ref<Category[]>([])
const categoryDialogVisible = ref(false)
const isEditingCategory = ref(false)
const categoryForm = ref<CategoryForm>({
  id: null,
  name: '',
  description: '',
  icon: '',
  sort: 1,
  status: 1
})
const categoryRules = ref({
  name: [{ required: true, message: '请输入分类名称', trigger: 'blur' }],
  sort: [{ required: true, message: '请输入排序', trigger: 'blur' }]
})
const categoryFormRef = ref(null)
const isSavingCategory = ref(false)

// 资源管理相关
const resources = ref<any[]>([])
const resourceSearch = ref('')
const resourceCategoryFilter = ref<number | null>(null)
const resourceStatusFilter = ref<number | null>(null)
const resourcePage = ref(1)
const resourcePageSize = ref(10)
const resourceTotal = ref(0)



// 用户课表相关
const selectedUser = ref<any>(null)
const selectedSemester = ref('2024-2025-2')
const userSchedule = ref<any>(null)
const scheduleLoading = ref(false)
const allCourses = ref<any[]>([])

// 课程编辑相关
const showCourseDialog = ref(false)
const editingCourse = ref<any>({
  id: null,
  name: '',
  teacher: '',
  location: '',
  dayOfWeek: null,
  startTime: '',
  endTime: '',
  startWeek: 1,
  endWeek: 18,
  semester: '2024-2025-2',
  color: '#409eff'
})
const courseRules = ref({
  name: [{ required: true, message: '请输入课程名称', trigger: 'blur' }],
  teacher: [{ required: true, message: '请输入教师姓名', trigger: 'blur' }],
  location: [{ required: true, message: '请输入上课地点', trigger: 'blur' }],
  dayOfWeek: [{ required: true, message: '请选择星期', trigger: 'change' }],
  semester: [{ required: true, message: '请输入学期', trigger: 'blur' }]
})
const courseFormRef = ref(null)
const isSavingCourse = ref(false)



// 系统状态相关
const systemStatus = ref({
  online: true,
  onlineUsers: 0
})

// 公告相关
const announcements = ref<any[]>([])
const showAnnouncementDialog = ref(false)
const editingAnnouncement = ref({
  id: null,
  title: '',
  type: 'system',
  content: '',
  startTime: '',
  endTime: '',
  isTop: false,
  status: 0
})

// 通知相关
const notifications = ref<any[]>([])
const showNotifications = ref(false)
const unreadNotifications = ref(0)

// 日志相关
const logs = ref<any[]>([])
const logLevel = ref('')
const logDateRange = ref([])
const logSearch = ref('')
const logPage = ref(1)
const logPageSize = ref(20)
const logTotal = ref(0)

// 设置相关
const activeSettingTab = ref('website')
const websiteSettings = ref({
  siteName: 'UniLife',
  siteDescription: '大学生活交流平台',
  siteKeywords: '大学,学习,交流,社区',
  maintenanceMode: false,
  allowRegistration: true
})
const emailSettings = ref({
  smtpHost: '',
  smtpPort: 587,
  fromEmail: '',
  password: '',
  enableSSL: false
})
const securitySettings = ref({
  maxLoginAttempts: 5,
  lockoutDuration: 30,
  sessionTimeout: 24,
  forceHttps: false
})

// 最近活动数据
const recentActivities = ref<any[]>([])

// 时间段定义（用于课表显示）
const timeSlots = ref([
  { name: '第1-2节', time: '08:00-09:50' },
  { name: '第3-4节', time: '10:10-12:00' },
  { name: '第5-6节', time: '14:00-15:50' },
  { name: '第7-8节', time: '16:10-18:00' },
  { name: '第9-10节', time: '19:00-20:50' },
  { name: '第11-12节', time: '21:00-22:50' }
])

// 菜单选择处理
const handleMenuSelect = (index: string) => {
  activeMenu.value = index
  if (index === 'dashboard') {
    loadStats()
  } else if (index === 'users') {
    loadUsers()
  } else if (index === 'posts') {
    loadPosts()
  } else if (index === 'comments') {
    loadComments()
  } else if (index === 'categories') {
    loadCategories()
  } else if (index === 'resources') {
    loadResources()
  } else if (index === 'schedules') {
    loadUsers() // 加载用户列表供选择
  } else if (index === 'monitor') {
    loadServerStatus()
  } else if (index === 'statistics') {
    ElMessage.warning('此功能暂未实现')
  }
}

// 加载统计数据
const loadStats = async () => {
  try {
    const response = await adminApi.getSystemStats()
    if (response.code === 200) {
      stats.value = response.data
    }
  } catch (error) {
    ElMessage.error('加载统计数据失败')
  }
}

// 加载用户列表
const loadUsers = async () => {
  try {
    const response = await adminApi.getUserList({
      page: userPage.value,
      size: userPageSize.value,
      keyword: userSearch.value || undefined,
      role: userRoleFilter.value
    })
    if (response.code === 200) {
      users.value = response.data.list
      userTotal.value = response.data.total
    }
  } catch (error) {
    ElMessage.error('加载用户列表失败')
  }
}

// 加载帖子列表
const loadPosts = async () => {
  try {
    const response = await adminApi.getPostList({
      page: postPage.value,
      size: postPageSize.value,
      keyword: postSearch.value || undefined,
      categoryId: postCategoryFilter.value || undefined,
      status: postStatusFilter.value || undefined
    })
    if (response.code === 200) {
      posts.value = response.data.list
      postTotal.value = response.data.total
    }
  } catch (error) {
    ElMessage.error('加载帖子列表失败')
  }
}

// 加载评论列表
const loadComments = async () => {
  try {
    const response = await adminApi.getCommentList({
      page: commentPage.value,
      size: commentPageSize.value,
      keyword: commentSearch.value || undefined,
      status: commentStatusFilter.value || undefined
    })
    if (response.code === 200) {
      comments.value = response.data.list
      commentTotal.value = response.data.total
    }
  } catch (error) {
    ElMessage.error('加载评论列表失败')
  }
}

// 加载分类列表
const loadCategories = async () => {
  try {
    const response = await adminApi.getCategoryList()
    if (response.code === 200) {
      categories.value = response.data
    }
  } catch (error) {
    ElMessage.error('加载分类列表失败')
  }
}

// 加载资源列表
const loadResources = async () => {
  try {
    const response = await adminApi.getResourceList({
      page: resourcePage.value,
      size: resourcePageSize.value,
      keyword: resourceSearch.value || undefined,
      categoryId: resourceCategoryFilter.value || undefined,
      status: resourceStatusFilter.value || undefined
    })
    if (response.code === 200) {
      resources.value = response.data.list
      resourceTotal.value = response.data.total
    }
  } catch (error) {
    ElMessage.error('加载资源列表失败')
  }
}

// 搜索用户
const searchUsers = () => {
  userPage.value = 1
  loadUsers()
}

// 搜索帖子
const searchPosts = () => {
  postPage.value = 1
  loadPosts()
}

// 搜索评论
const searchComments = () => {
  commentPage.value = 1
  loadComments()
}

// 搜索资源
const searchResources = () => {
  resourcePage.value = 1
  loadResources()
}



// 加载用户课表
const loadSchedules = async () => {
  if (!selectedUser.value) {
    ElMessage.warning('请先选择用户')
    return
  }
  
  scheduleLoading.value = true
  try {
    const response = await adminApi.getUserSchedule(selectedUser.value.id, selectedSemester.value)
    if (response.code === 200) {
      userSchedule.value = response.data
      // 提取所有课程到列表视图
      allCourses.value = []
      Object.values(userSchedule.value.schedule).forEach((courses: any) => {
        allCourses.value.push(...courses)
      })
    }
  } catch (error) {
    ElMessage.error('加载用户课表失败')
  } finally {
    scheduleLoading.value = false
  }
}

// 编辑课程
const editCourse = (course: any) => {
  editingCourse.value = { ...course }
  editingCourse.value.userId = selectedUser.value?.id
  showCourseDialog.value = true
}

// 重置课程表单
const resetCourseForm = () => {
  editingCourse.value = {
    id: null,
    name: '',
    teacher: '',
    location: '',
    dayOfWeek: null,
    startTime: '',
    endTime: '',
    startWeek: 1,
    endWeek: 18,
    semester: selectedSemester.value,
    color: '#409eff',
    userId: selectedUser.value?.id
  }
}

// 保存课程
const saveCourse = async () => {
  try {
    isSavingCourse.value = true
    // 这里需要调用保存课程的API
    ElMessage.success(editingCourse.value.id ? '课程更新成功' : '课程添加成功')
    showCourseDialog.value = false
    loadSchedules() // 重新加载课表
  } catch (error) {
    ElMessage.error('保存课程失败')
  } finally {
    isSavingCourse.value = false
  }
}

// 切换用户状态
const toggleUserStatus = async (user: any) => {
  try {
    const newStatus = user.status === 1 ? 0 : 1
    const response = await adminApi.updateUserStatus(user.id, { status: newStatus })
    if (response.code === 200) {
      user.status = newStatus
      ElMessage.success('用户状态更新成功')
    }
  } catch (error) {
    ElMessage.error('更新用户状态失败')
  }
}

// 删除用户
const deleteUser = async (user: any) => {
  try {
    await ElMessageBox.confirm('确定要删除该用户吗？此操作不可恢复！', '警告', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const response = await adminApi.deleteUser(user.id)
    if (response.code === 200) {
      ElMessage.success('用户删除成功')
      loadUsers()
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除用户失败')
    }
  }
}

// 切换帖子状态
const togglePostTop = async (post: any) => {
  try {
    const newStatus = post.status === 2 ? 1 : 2
    const response = await adminApi.updatePostStatus(post.id, { status: newStatus })
    if (response.code === 200) {
      post.status = newStatus
      ElMessage.success('帖子状态更新成功')
    }
  } catch (error) {
    ElMessage.error('更新帖子状态失败')
  }
}

// 删除帖子
const deletePost = async (post: any) => {
  try {
    await ElMessageBox.confirm('确定要删除该帖子吗？此操作不可恢复！', '警告', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const response = await adminApi.deletePost(post.id)
    if (response.code === 200) {
      ElMessage.success('帖子删除成功')
      loadPosts()
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除帖子失败')
    }
  }
}

// 恢复帖子
const restorePost = async (post: any) => {
  try {
    await ElMessageBox.confirm('确定要恢复该帖子吗？', '确认', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'info'
    })
    
    const response = await adminApi.updatePostStatus(post.id, { status: 1 })
    if (response.code === 200) {
      ElMessage.success('帖子恢复成功')
      loadPosts()
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('恢复帖子失败')
    }
  }
}

// 永久删除帖子
const permanentDeletePost = async (post: any) => {
  try {
    await ElMessageBox.confirm('确定要永久删除该帖子吗？此操作不可恢复！', '危险操作', {
      confirmButtonText: '永久删除',
      cancelButtonText: '取消',
      type: 'error',
      confirmButtonClass: 'el-button--danger'
    })
    
    const response = await adminApi.permanentDeletePost(post.id)
    if (response.code === 200) {
      ElMessage.success('帖子永久删除成功')
      loadPosts()
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('永久删除帖子失败')
    }
  }
}

// 删除评论
const deleteComment = async (comment: any) => {
  try {
    await ElMessageBox.confirm('确定要删除该评论吗？此操作不可恢复！', '警告', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const response = await adminApi.deleteComment(comment.id)
    if (response.code === 200) {
      ElMessage.success('评论删除成功')
      loadComments()
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除评论失败')
    }
  }
}

// 删除课程
const deleteCourse = async (course: any) => {
  try {
    await ElMessageBox.confirm('确定要删除该课程吗？此操作不可恢复！', '警告', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const response = await adminApi.deleteCourse(course.id)
    if (response.code === 200) {
      ElMessage.success('课程删除成功')
      loadSchedules() // 重新加载课表
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除课程失败')
    }
  }
}

// 显示创建分类对话框
const showCreateCategoryDialog = () => {
  isEditingCategory.value = false
  categoryForm.value = {
    id: null,
    name: '',
    description: '',
    icon: '',
    sort: 1,
    status: 1
  }
  categoryDialogVisible.value = true
}

// 编辑分类
const editCategory = (category: any) => {
  isEditingCategory.value = true
  categoryForm.value = {
    id: category.id,
    name: category.name,
    description: category.description,
    icon: category.icon,
    sort: category.sort,
    status: category.status
  }
  categoryDialogVisible.value = true
}

// 保存分类
const saveCategoryForm = async () => {
  try {
    isSavingCategory.value = true
    let response
    if (isEditingCategory.value && categoryForm.value.id) {
      response = await adminApi.updateCategory(categoryForm.value.id, categoryForm.value)
    } else {
      response = await adminApi.createCategory(categoryForm.value)
    }
    if (response.code === 200) {
      ElMessage.success('分类保存成功')
      categoryDialogVisible.value = false
      loadCategories()
    }
  } catch (error) {
    ElMessage.error('保存分类失败')
  } finally {
    isSavingCategory.value = false
  }
}

// 删除分类
const deleteCategory = async (category: any) => {
  try {
    await ElMessageBox.confirm('确定要删除该分类吗？此操作不可恢复！', '警告', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const response = await adminApi.deleteCategory(category.id)
    if (response.code === 200) {
      ElMessage.success('分类删除成功')
      loadCategories()
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除分类失败')
    }
  }
}

// 删除资源
const deleteResource = async (resource: any) => {
  try {
    await ElMessageBox.confirm('确定要删除该资源吗？此操作不可恢复！', '警告', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const response = await adminApi.deleteResource(resource.id)
    if (response.code === 200) {
      ElMessage.success('资源删除成功')
      loadResources()
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除资源失败')
    }
  }
}

// 获取角色类型
const getRoleType = (role: number) => {
  switch (role) {
    case 0: return ''
    case 1: return 'warning'
    case 2: return 'danger'
    default: return ''
  }
}

// 获取角色文本
const getRoleText = (role: number) => {
  switch (role) {
    case 0: return '普通用户'
    case 1: return '版主'
    case 2: return '管理员'
    default: return '未知'
  }
}

// 获取帖子状态类型
const getPostStatusType = (status: number) => {
  switch (status) {
    case 1: return ''
    case 2: return 'warning'
    case 0: return 'danger'
    default: return ''
  }
}

// 获取帖子状态文本
const getPostStatusText = (status: number) => {
  switch (status) {
    case 1: return '正常'
    case 2: return '置顶'
    case 0: return '已删除'
    default: return '未知'
  }
}

// 格式化文件大小
const formatFileSize = (size: number) => {
  if (size < 1024) return size + ' B'
  if (size < 1024 * 1024) return (size / 1024).toFixed(1) + ' KB'
  if (size < 1024 * 1024 * 1024) return (size / (1024 * 1024)).toFixed(1) + ' MB'
  return (size / (1024 * 1024 * 1024)).toFixed(1) + ' GB'
}

// 退出登录
const logout = () => {
  userStore.logout()
  router.push('/login')
}

// 页面加载时初始化
onMounted(() => {
  loadStats()
})

// 新增方法实现

// 加载服务器状态
const loadServerStatus = async () => {
  try {
    const response = await adminApi.getSystemStatus()
    if (response.code === 200) {
      systemStatus.value = response.data.application
    }
  } catch (error) {
    ElMessage.error('加载系统状态失败')
  }
}

// 加载日志
const loadLogs = async () => {
  try {
    // 模拟日志数据，实际应该调用API
    logs.value = [
      {
        id: 1,
        timestamp: '2024-01-20 10:30:15',
        level: 'INFO',
        logger: 'com.unilife.controller.UserController',
        message: '用户登录成功：user_id=123'
      },
      {
        id: 2,
        timestamp: '2024-01-20 10:28:32',
        level: 'WARN',
        logger: 'com.unilife.service.PostService',
        message: '帖子审核超时：post_id=456'
      },
      {
        id: 3,
        timestamp: '2024-01-20 10:25:11',
        level: 'ERROR',
        logger: 'com.unilife.config.DatabaseConfig',
        message: '数据库连接池满载，请求被拒绝'
      }
    ]
    logTotal.value = 100
  } catch (error) {
    ElMessage.error('加载日志失败')
  }
}

// 加载网站设置
const loadWebsiteSettings = async () => {
  try {
    // 模拟加载设置，实际应该调用API
    console.log('加载网站设置')
  } catch (error) {
    ElMessage.error('加载设置失败')
  }
}

// 加载公告列表
const loadAnnouncements = async () => {
  try {
    // 模拟公告数据，实际应该调用API
    announcements.value = [
      {
        id: 1,
        title: '系统维护通知',
        type: 'maintenance',
        status: 1,
        startTime: '2024-01-20 00:00:00',
        endTime: '2024-01-21 00:00:00',
        createdAt: '2024-01-19 10:00:00'
      }
    ]
  } catch (error) {
    ElMessage.error('加载公告失败')
  }
}

// 加载通知
const loadNotifications = async () => {
  try {
    // 模拟通知数据
    notifications.value = [
      {
        id: 1,
        title: '系统提醒',
        message: '有新的用户注册审核',
        time: '5分钟前'
      }
    ]
    unreadNotifications.value = notifications.value.length
  } catch (error) {
    ElMessage.error('加载通知失败')
  }
}

// 加载统计数据
const loadStatistics = async () => {
  try {
    // 实际应该加载图表数据
    console.log('加载统计图表')
  } catch (error) {
    ElMessage.error('加载统计数据失败')
  }
}

// 进度条颜色判断
const getProgressColor = (percentage: number) => {
  if (percentage < 50) return '#67c23a'
  if (percentage < 80) return '#e6a23c'
  return '#f56c6c'
}

// 日志级别颜色
const getLogLevelType = (level: string) => {
  switch (level) {
    case 'ERROR': return 'danger'
    case 'WARN': return 'warning'
    case 'INFO': return 'success'
    case 'DEBUG': return 'info'
    default: return ''
  }
}

// 查看日志详情
const viewLogDetail = (log: any) => {
  ElMessageBox.alert(log.message, '日志详情', {
    confirmButtonText: '关闭'
  })
}

// 公告相关方法
const showCreateAnnouncementDialog = () => {
  editingAnnouncement.value = {
    id: null,
    title: '',
    type: 'system',
    content: '',
    startTime: '',
    endTime: '',
    isTop: false,
    status: 0
  }
  showAnnouncementDialog.value = true
}

const editAnnouncement = (announcement: any) => {
  editingAnnouncement.value = { ...announcement }
  showAnnouncementDialog.value = true
}

const saveAnnouncementAsDraft = async () => {
  try {
    editingAnnouncement.value.status = 0
    // 调用API保存草稿
    ElMessage.success('公告草稿保存成功')
    showAnnouncementDialog.value = false
    loadAnnouncements()
  } catch (error) {
    ElMessage.error('保存失败')
  }
}

const publishAnnouncement = async () => {
  try {
    editingAnnouncement.value.status = 1
    // 调用API发布公告
    ElMessage.success('公告发布成功')
    showAnnouncementDialog.value = false
    loadAnnouncements()
  } catch (error) {
    ElMessage.error('发布失败')
  }
}

const toggleAnnouncementStatus = async (announcement: any) => {
  try {
    const newStatus = announcement.status === 1 ? 0 : 1
    // 调用API更新状态
    announcement.status = newStatus
    ElMessage.success(`公告${newStatus === 1 ? '发布' : '下线'}成功`)
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

const deleteAnnouncement = async (announcement: any) => {
  try {
    await ElMessageBox.confirm('确定要删除该公告吗？', '警告', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    // 调用API删除
    ElMessage.success('公告删除成功')
    loadAnnouncements()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const getAnnouncementTypeColor = (type: string) => {
  switch (type) {
    case 'system': return 'primary'
    case 'feature': return 'success'
    case 'maintenance': return 'warning'
    case 'urgent': return 'danger'
    default: return ''
  }
}

const getAnnouncementTypeText = (type: string) => {
  switch (type) {
    case 'system': return '系统通知'
    case 'feature': return '功能更新'
    case 'maintenance': return '维护通知'
    case 'urgent': return '紧急通知'
    default: return '未知'
  }
}

// 设置保存方法
const saveWebsiteSettings = async () => {
  try {
    // 调用API保存网站设置
    ElMessage.success('网站设置保存成功')
  } catch (error) {
    ElMessage.error('保存失败')
  }
}

const saveEmailSettings = async () => {
  try {
    // 调用API保存邮件设置
    ElMessage.success('邮件设置保存成功')
  } catch (error) {
    ElMessage.error('保存失败')
  }
}

const saveSecuritySettings = async () => {
  try {
    // 调用API保存安全设置
    ElMessage.success('安全设置保存成功')
  } catch (error) {
    ElMessage.error('保存失败')
  }
}

const testEmail = async () => {
  try {
    // 调用API测试邮件
    ElMessage.success('测试邮件发送成功')
  } catch (error) {
    ElMessage.error('测试邮件发送失败')
  }
}

// 通知相关方法
const markAsRead = async (notificationId: number) => {
  try {
    // 调用API标记已读
    const index = notifications.value.findIndex(n => n.id === notificationId)
    if (index !== -1) {
      notifications.value.splice(index, 1)
      unreadNotifications.value = notifications.value.length
    }
    ElMessage.success('标记已读')
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

// 课表管理相关工具函数
const getDayOfWeekName = (dayOfWeek: number) => {
  const days = ['', '周一', '周二', '周三', '周四', '周五', '周六', '周日']
  return days[dayOfWeek] || '未知'
}

const searchScheduleUsers = async (query: string) => {
  if (query) {
    await loadUsers()
  }
}

const getCoursesForTimeSlot = (dayOfWeek: number, timeSlot: any) => {
  if (!userSchedule.value?.schedule?.[dayOfWeek]) {
    return []
  }
  
  return userSchedule.value.schedule[dayOfWeek].filter((course: any) => {
    // 简单的时间匹配逻辑，实际应该更精确
    const courseStartTime = course.startTime
    const courseEndTime = course.endTime
    const slotStartTime = timeSlot.time.split('-')[0]
    const slotEndTime = timeSlot.time.split('-')[1]
    
    // 这里应该有更精确的时间重叠判断
    return courseStartTime <= slotEndTime && courseEndTime >= slotStartTime
  })
}


</script>

<style scoped>
.admin-dashboard {
  min-height: 100vh;
  background-color: #f5f5f5;
}

.admin-header {
  background: #fff;
  height: 60px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 1000;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 20px;
}

.admin-title {
  margin: 0;
  font-size: 20px;
  color: #333;
}

.system-status {
  display: flex;
  align-items: center;
  gap: 12px;
}

.online-users {
  font-size: 12px;
  color: #666;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 16px;
}

.notification-badge {
  margin-right: 8px;
}

.admin-user {
  color: #666;
  font-size: 14px;
}

.admin-content {
  display: flex;
  margin-top: 60px;
  min-height: calc(100vh - 60px);
}

.admin-sidebar {
  width: 250px;
  background: #fff;
  border-right: 1px solid #e6e6e6;
}

.admin-menu {
  border-right: none;
}

.admin-main {
  flex: 1;
  padding: 20px;
  overflow-x: auto;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 20px;
  margin-bottom: 30px;
}

.stat-card {
  background: #fff;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  display: flex;
  align-items: center;
}

.stat-icon {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 15px;
  color: #fff;
  font-size: 20px;
}

.stat-icon.user-icon {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.stat-icon.post-icon {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.stat-icon.comment-icon {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.stat-icon.resource-icon {
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
}

.stat-info {
  flex: 1;
}

.stat-number {
  font-size: 24px;
  font-weight: bold;
  color: #333;
  margin-bottom: 5px;
}

.stat-label {
  color: #666;
  font-size: 14px;
  margin-bottom: 5px;
}

.stat-trend {
  font-size: 12px;
  font-weight: 500;
}

.stat-trend.positive {
  color: #67c23a;
}

.stat-trend.negative {
  color: #f56c6c;
}

.quick-actions {
  margin-bottom: 30px;
}

.quick-actions h3 {
  margin-bottom: 15px;
  color: #333;
  font-size: 16px;
}

.action-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 15px;
}

.action-card {
  background: #fff;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.action-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.action-card .el-icon {
  font-size: 24px;
  color: #409eff;
}

.action-card span {
  font-size: 14px;
  color: #333;
}

.recent-activities {
  background: #fff;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.recent-activities h3 {
  margin-bottom: 15px;
  color: #333;
  font-size: 16px;
  border-bottom: 1px solid #eee;
  padding-bottom: 10px;
}

.activity-list {
  max-height: 300px;
  overflow-y: auto;
}

.activity-item {
  display: flex;
  align-items: center;
  padding: 10px 0;
  border-bottom: 1px solid #f0f0f0;
}

.activity-item:last-child {
  border-bottom: none;
}

.activity-icon {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 12px;
  color: #fff;
  font-size: 14px;
}

.activity-icon.user {
  background: #409eff;
}

.activity-icon.post {
  background: #67c23a;
}

.activity-icon.comment {
  background: #e6a23c;
}

.activity-content {
  flex: 1;
}

.activity-text {
  font-size: 14px;
  color: #333;
  margin-bottom: 2px;
}

.activity-time {
  font-size: 12px;
  color: #999;
}

.monitor-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 20px;
}

.monitor-card {
  background: #fff;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.monitor-card h3 {
  margin-bottom: 15px;
  color: #333;
  font-size: 16px;
  border-bottom: 1px solid #eee;
  padding-bottom: 8px;
}

.monitor-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.monitor-item:last-child {
  margin-bottom: 0;
}

.monitor-value {
  font-weight: 500;
  color: #333;
}

.logs-toolbar {
  display: flex;
  gap: 12px;
  margin-bottom: 20px;
  align-items: center;
  flex-wrap: wrap;
}

.settings-content .el-tabs {
  background: #fff;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.form-tip {
  font-size: 12px;
  color: #999;
  margin-top: 4px;
}

.chart-container {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(400px, 1fr));
  gap: 20px;
}

.chart-card {
  background: #fff;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.chart-card h3 {
  margin-bottom: 15px;
  color: #333;
  font-size: 16px;
  text-align: center;
}

.chart {
  height: 300px;
  background: #f9f9f9;
  border-radius: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #999;
}

.notification-list {
  max-height: 400px;
  overflow-y: auto;
}

.notification-item {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  padding: 12px 0;
  border-bottom: 1px solid #f0f0f0;
}

.notification-item:last-child {
  border-bottom: none;
}

.notification-content {
  flex: 1;
  margin-right: 12px;
}

.notification-title {
  font-weight: 500;
  color: #333;
  margin-bottom: 4px;
}

.notification-message {
  font-size: 14px;
  color: #666;
  margin-bottom: 4px;
}

.notification-time {
  font-size: 12px;
  color: #999;
}

.table-toolbar {
  display: flex;
  gap: 12px;
  margin-bottom: 20px;
  align-items: center;
  flex-wrap: wrap;
}

h2 {
  margin-bottom: 20px;
  color: #333;
  border-bottom: 2px solid #409eff;
  padding-bottom: 10px;
}

.el-pagination {
  margin-top: 20px;
  justify-content: center;
}

.users-content,
.posts-content,
.comments-content,
.categories-content,
.resources-content,
.monitor-content,
.logs-content,
.settings-content,
.announcements-content,
.statistics-content,
.schedules-content {
  background: #fff;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  margin-bottom: 20px;
}

/* 课表管理样式 */
.schedule-toolbar {
  display: flex;
  gap: 20px;
  margin-bottom: 20px;
  align-items: center;
  flex-wrap: wrap;
}

.user-selector,
.semester-selector,
.schedule-actions {
  display: flex;
  align-items: center;
  gap: 8px;
}

.schedule-header {
  margin-bottom: 20px;
}

.schedule-header h3 {
  margin-bottom: 10px;
  color: #333;
}

.user-info {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.course-list-view {
  margin-bottom: 30px;
}

.course-list-view h4 {
  margin-bottom: 15px;
  color: #333;
  font-size: 16px;
  border-bottom: 1px solid #eee;
  padding-bottom: 8px;
}

.schedule-table h4 {
  margin-bottom: 15px;
  color: #333;
  font-size: 16px;
  border-bottom: 1px solid #eee;
  padding-bottom: 8px;
}

.week-schedule {
  width: 100%;
  border-collapse: collapse;
  border: 1px solid #e6e6e6;
  background: #fff;
  font-size: 12px;
}

.week-schedule th,
.week-schedule td {
  border: 1px solid #e6e6e6;
  text-align: center;
  vertical-align: top;
  min-height: 60px;
}

.week-schedule th {
  background: #f5f7fa;
  color: #606266;
  font-weight: 500;
  padding: 12px 8px;
}

.time-slot {
  background: #fafafa;
  color: #606266;
  font-size: 11px;
  line-height: 1.4;
  padding: 8px 4px;
  width: 80px;
}

.course-cell {
  width: calc((100% - 80px) / 7);
  min-height: 80px;
  padding: 4px;
  position: relative;
}

.course-item {
  border-radius: 4px;
  padding: 6px;
  margin-bottom: 2px;
  cursor: pointer;
  transition: all 0.2s ease;
  font-size: 11px;
  line-height: 1.3;
}

.course-item:hover {
  transform: scale(1.05);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.2);
}

.course-name {
  font-weight: bold;
  margin-bottom: 2px;
}

.course-teacher,
.course-location,
.course-weeks {
  font-size: 10px;
  opacity: 0.9;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .admin-sidebar {
    width: 200px;
  }
  
  .stats-grid {
    grid-template-columns: 1fr;
  }
  
  .action-cards {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .monitor-grid {
    grid-template-columns: 1fr;
  }
  
  .chart-container {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 480px) {
  .admin-content {
    flex-direction: column;
  }
  
  .admin-sidebar {
    width: 100%;
    height: auto;
  }
  
  .action-cards {
    grid-template-columns: 1fr;
  }
  
  .table-toolbar {
    flex-direction: column;
    align-items: stretch;
  }
}
</style> 