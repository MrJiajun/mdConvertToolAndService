# SmartConvert 部署文档

## 目录

- [环境要求](#环境要求)
- [开发环境部署](#开发环境部署)
- [Docker 部署](#docker-部署)
- [生产环境配置](#生产环境配置)
- [API 文档](#api-文档)
- [常见问题](#常见问题)

---

## 环境要求

### 开发环境

| 组件 | 版本要求 |
|------|----------|
| JDK | 17+ |
| Node.js | 20.19.0+ 或 22.12.0+ |
| Maven | 3.9+ |
| npm | 随 Node.js 安装 |

### 生产环境

| 组件 | 版本要求 |
|------|----------|
| Docker | 20.10+ |
| Docker Compose | 2.0+ |

---

## 开发环境部署

### 后端服务

```bash
# 进入后端目录
cd smartconvert/backend/smartconvert-backend

# 编译项目
mvn clean compile

# 运行测试
mvn test

# 启动开发服务器
mvn spring-boot:run
```

后端服务将在 `http://localhost:8080` 启动。

**可用端点：**
- `GET /api/health` - 健康检查
- `POST /api/convert` - 文档转换
- `GET /api/download/{fileId}/{format}` - 下载转换后的文件

### 前端服务

```bash
# 进入前端目录
cd smartconvert/frontend/smartconvert-frontend

# 安装依赖
npm install

# 启动开发服务器
npm run dev
```

前端服务将在 `http://localhost:3000` 启动，API 请求会自动代理到后端服务。

**构建生产版本：**

```bash
# 类型检查
npm run type-check

# 构建生产版本
npm run build

# 预览构建结果
npm run preview
```

---

## Docker 部署

### 一键部署（推荐）

```bash
# 进入项目根目录
cd smartconvert

# 构建并启动所有服务
docker-compose up -d --build
```

### 服务访问地址

| 服务 | 地址 |
|------|------|
| 前端应用 | http://localhost |
| 后端 API | http://localhost:8080/api |
| Swagger 文档 | http://localhost:8080/swagger-ui.html |
| OpenAPI JSON | http://localhost:8080/v3/api-docs |

### 常用 Docker 命令

```bash
# 查看服务状态
docker-compose ps

# 查看日志
docker-compose logs -f

# 查看特定服务日志
docker-compose logs -f backend
docker-compose logs -f frontend

# 停止服务
docker-compose down

# 停止并删除数据卷
docker-compose down -v

# 重新构建特定服务
docker-compose up -d --build backend
docker-compose up -d --build frontend
```

### 单独构建镜像

**后端镜像：**

```bash
cd smartconvert/backend/smartconvert-backend
docker build -t smartconvert-backend:latest .
```

**前端镜像：**

```bash
cd smartconvert/frontend/smartconvert-frontend
docker build -t smartconvert-frontend:latest .
```

---

## 生产环境配置

### 环境变量

#### 后端服务

| 变量名 | 默认值 | 说明 |
|--------|--------|------|
| `SPRING_PROFILES_ACTIVE` | `prod` | Spring 配置文件 |
| `JAVA_OPTS` | `-Xmx512m -Xms256m` | JVM 参数 |
| `APP_UPLOAD_DIR` | `/tmp/smartconvert/uploads` | 上传文件目录 |
| `APP_CLEANUP_INTERVAL` | `1800000` | 清理间隔（毫秒，默认30分钟） |
| `APP_CLEANUP_MAX_AGE` | `3600000` | 文件最大保留时间（毫秒，默认1小时） |

#### 前端服务 (Nginx)

前端通过 Nginx 配置文件管理，主要配置：
- Gzip 压缩
- API 反向代理
- 静态资源缓存
- SPA 路由支持

### 端口配置

修改 `docker-compose.yml` 中的端口映射：

```yaml
services:
  backend:
    ports:
      - "8080:8080"  # 修改为 desired_port:8080
  
  frontend:
    ports:
      - "80:80"      # 修改为 desired_port:80
```

### 文件存储

生产环境建议将上传目录挂载到持久化存储：

```yaml
volumes:
  - ./data/uploads:/tmp/smartconvert/uploads
```

### HTTPS 配置

在 `nginx.conf` 中添加 SSL 配置：

```nginx
server {
    listen 443 ssl;
    ssl_certificate /etc/nginx/ssl/cert.pem;
    ssl_certificate_key /etc/nginx/ssl/key.pem;
    # ... 其他配置
}

# HTTP 重定向到 HTTPS
server {
    listen 80;
    return 301 https://$host$request_uri;
}
```

---

## API 文档

### Swagger UI

启动后端服务后访问：`http://localhost:8080/swagger-ui.html`

### API 端点

#### 健康检查

```http
GET /api/health
```

**响应：**
```
OK
```

#### 文档转换

```http
POST /api/convert
Content-Type: multipart/form-data
```

**参数：**

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| file | File | 是 | 要转换的文件 |
| sourceFormat | String | 是 | 源格式：docx, pdf, txt, md |
| targetFormat | String | 是 | 目标格式：docx, pdf, txt, md |

**成功响应：**
```json
{
  "success": true,
  "message": "Conversion successful",
  "downloadUrl": "/api/download/uuid/format",
  "fileName": "converted.md",
  "fileSize": 1024
}
```

**错误响应：**
```json
{
  "success": false,
  "message": "Unsupported source format: xyz"
}
```

#### 下载文件

```http
GET /api/download/{fileId}/{format}
```

**参数：**

| 参数名 | 类型 | 说明 |
|--------|------|------|
| fileId | String | 转换返回的文件 ID |
| format | String | 文件格式 |

**响应：** 文件流下载

---

## 支持的转换格式

| 源格式 | 目标格式 | 说明 |
|--------|----------|------|
| docx | md | Word 转 Markdown，保留标题、列表、表格 |
| docx | pdf | Word 转 PDF |
| docx | txt | Word 转纯文本 |
| pdf | md | PDF 转 Markdown，提取文本内容 |
| pdf | docx | PDF 转 Word |
| pdf | txt | PDF 转纯文本 |
| txt | md | 纯文本转 Markdown |
| txt | docx | 纯文本转 Word |
| txt | pdf | 纯文本转 PDF |
| md | docx | Markdown 转 Word |
| md | pdf | Markdown 转 PDF |
| md | txt | Markdown 转纯文本 |

---

## 常见问题

### 1. 文件上传失败

**问题：** 上传文件时报错 "File size exceeds the maximum limit"

**解决方案：** 
- 默认限制为 10MB
- 修改 `application.yml` 中的配置：
```yaml
spring:
  servlet:
    multipart:
      max-file-size: 50MB
      max-request-size: 100MB
```

### 2. Docker 构建失败

**问题：** Maven 依赖下载超时

**解决方案：**
- 配置 Maven 镜像源
- 或使用预构建的 JAR 文件

### 3. 前端无法连接后端

**问题：** 前端请求 API 报错 CORS 或连接失败

**解决方案：**
- 开发环境：确保后端在 8080 端口运行
- Docker 环境：检查 `nginx.conf` 中的代理配置
- 检查网络连通性：`docker network ls`

### 4. 转换后的文件下载失败

**问题：** 文件已过期或不存在

**解决方案：**
- 默认文件保留 1 小时
- 修改 `APP_CLEANUP_MAX_AGE` 增加保留时间

### 5. 中文显示乱码

**问题：** PDF 中中文显示为方框

**解决方案：**
- Docker 镜像已安装 `ttf-dejavu` 字体
- 如需其他字体，在 Dockerfile 中添加：
```dockerfile
RUN apk add --no-cache fontconfig ttf-dejavu
```

---

## 监控与日志

### 查看日志

```bash
# 实时查看所有日志
docker-compose logs -f

# 查看后端日志
docker-compose logs -f backend

# 查看最近 100 行日志
docker-compose logs --tail=100 backend
```

### 健康检查

```bash
# 检查后端健康状态
curl http://localhost:8080/api/health

# 检查容器状态
docker-compose ps
```

---

## 目录结构

```
smartconvert/
├── backend/
│   └── smartconvert-backend/
│       ├── src/
│       │   ├── main/
│       │   │   ├── java/com/smartconvert/
│       │   │   │   ├── config/
│       │   │   │   ├── controller/
│       │   │   │   ├── dto/
│       │   │   │   ├── exception/
│       │   │   │   └── service/
│       │   │   └── resources/
│       │   └── test/
│       ├── Dockerfile
│       └── pom.xml
├── frontend/
│   └── smartconvert-frontend/
│       ├── src/
│       │   ├── components/
│       │   ├── router/
│       │   ├── stores/
│       │   └── views/
│       ├── Dockerfile
│       ├── nginx.conf
│       └── package.json
└── docker-compose.yml
```

---

## 技术支持

如遇问题，请检查：
1. 服务是否正常启动
2. 端口是否被占用
3. 日志中的错误信息
4. 环境变量配置是否正确
