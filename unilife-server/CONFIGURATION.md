# 项目配置说明

## 环境配置

本项目使用环境变量和本地配置文件来管理敏感信息，确保安全性。

### 1. 本地开发环境配置

创建 `src/main/resources/application-local.yml` 文件（已在.gitignore中忽略）：

```yaml
# 本地开发环境配置
aliyun:
  oss:
    endpoint: your-endpoint
    accessKeyId: your-access-key-id
    accessKeySecret: your-access-key-secret
    bucketName: your-bucket-name
    urlPrefix: https://your-bucket-name.oss-region.aliyuncs.com/
```

### 2. 环境变量配置

复制 `env.example` 为 `.env` 并填入真实配置：

```bash
cp env.example .env
```

然后编辑 `.env` 文件，填入您的真实配置信息。

### 3. 启动应用

#### 方式1：使用本地配置文件
```bash
java -jar app.jar --spring.profiles.active=local
```

#### 方式2：使用环境变量
```bash
# 设置环境变量
export ALIYUN_OSS_ENDPOINT=your-endpoint
export ALIYUN_OSS_ACCESS_KEY_ID=your-access-key-id
export ALIYUN_OSS_ACCESS_KEY_SECRET=your-access-key-secret
export ALIYUN_OSS_BUCKET_NAME=your-bucket-name
export ALIYUN_OSS_URL_PREFIX=https://your-bucket-name.oss-region.aliyuncs.com/

# 启动应用
java -jar app.jar
```

## 阿里云OSS配置

### 1. 创建OSS Bucket
1. 登录阿里云控制台
2. 进入对象存储OSS服务
3. 创建Bucket，选择合适的地域和存储类型
4. 配置访问权限（推荐私有读写）

### 2. 获取AccessKey
1. 进入阿里云控制台
2. 点击右上角头像 -> AccessKey管理
3. 创建AccessKey（建议使用RAM子账号）
4. 为RAM用户授予OSS相关权限

### 3. 配置跨域访问（CORS）
在OSS控制台设置CORS规则：
- 来源：您的前端域名
- 允许Methods：GET, POST, PUT, DELETE, HEAD
- 允许Headers：*
- 暴露Headers：ETag, x-oss-request-id

## 安全注意事项

1. **永远不要将AccessKey提交到代码仓库**
2. **使用RAM子账号，最小权限原则**
3. **定期轮换AccessKey**
4. **启用OSS访问日志监控**
5. **配置适当的Bucket策略**

## 生产环境部署

生产环境建议使用以下方式之一：

1. **容器环境变量**（Docker/Kubernetes）
2. **云服务商的密钥管理服务**
3. **专门的配置中心**（如Nacos、Apollo） 