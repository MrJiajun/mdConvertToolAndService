这是一份为您精心编写的《多格式文档互转工具（SmartConvert）需求文档》。文档采用了现代化的技术选型，旨在指导您构建一个界面精美、功能稳健的全栈应用。

---

# 多格式文档互转工具 (SmartConvert) 需求文档

## 1. 项目概述

### 1.1 项目简介

SmartConvert 是一款基于 Web 的文档格式转换工具，支持 Word、PDF、Text 与 Markdown 之间的双向互转。项目旨在为开发者、撰稿人和学生提供一个极简、高效且视觉精美的文档处理平台。

### 1.2 核心目标

- 实现高保真度的格式转换。

- 提供响应式、现代化且符合审美的前端交互体验。

- 构建基于 Spring Boot 的高性能后端处理引擎。

---

## 2. 技术栈选型

### 2.1 前端 (Frontend)

- **框架**: Vue 3 (Composition API) 或 React 18

- **构建工具**: Vite

- **UI 组件库**: Element Plus (Vue) 或 Ant Design (React)

- **样式库**: Tailwind CSS (用于实现自定义精美界面)

- **状态管理**: Pinia (Vue) 或 Redux Toolkit (React)

- **动画库**: Framer Motion 或 GSAP (增加转场动效)

### 2.2 后端 (Backend)

- **核心框架**: Spring Boot 3.x

- **核心库**:
  
  - **Markdown 解析**: flexmark-java
  
  - **Word 处理**: Apache POI
  
  - **PDF 处理**: itext7 或 OpenHTMLtoPDF
  
  - **Pandoc 桥接 (可选)**: 若需极致转换效果，可通过系统调用 Pandoc

- **接口文档**: Swagger / Knife4j

- **文件上传**: Spring StandardMultipartHttpServletRequest

### 2.3 部署环境

- **容器化**: Docker & Docker Compose

- **服务器**: Nginx (前端静态资源路由)

---

## 3. 功能需求

### 3.1 核心转换模块

支持以下转换路径：

1. **Word (.docx) ↔ Markdown**: 保留标题、列表、表格和加粗等基本样式。

2. **PDF (.pdf) ↔ Markdown**:
   
   - PDF 转 MD：提取文本内容，尽量保持层级（注意：复杂布局可能存在偏差）。
   
   - MD 转 PDF：支持代码高亮渲染后的美化导出。

3. **Text (.txt) ↔ Markdown**: 纯文本与 Markdown 格式的封装与去格式化。

### 3.2 用户界面 (UI) 需求

- **深色模式支持**: 提供一键切换深浅色主题。

- **拖拽上传**: 仿制 Vercel 或 Apple 风格的拖拽上传区域。

- **实时预览**: Markdown 转换前/后的实时预览窗口（左编辑右预览）。

- **进度反馈**: 带有动效的转换进度条（Processing...）。

- **批量处理**: 支持一次性上传多个文件并打包下载。

### 3.3 后端 API 功能

- POST /api/convert: 核心接口。接收文件、源格式、目标格式，返回转换后的文件流或下载链接。

- GET /api/history: 查看最近转换记录（基于 Session 或本地存储）。

- GET /api/health: 系统健康检查。

---

## 4. 界面设计建议 (UI/UX)

1. **配色方案**: 采用极简白/石墨黑，点缀色使用 Indigo (600) 或 Emerald (500)。

2. **卡片式布局**: 转换工具箱采用悬浮卡片感，增加 backdrop-blur (毛玻璃) 效果。

3. **交互反馈**: 按钮点击时的微互动（Micro-interactions），上传成功后的 Toast 提示。

---

## 5. 后端关键代码实现思路 (Java)

### 5.1 依赖配置 (Maven 示例)



```html
<!-- Word 处理 -->
<dependency>
    <groupId>org.apache.poi</groupId>
    <artifactId>poi-ooxml</artifactId>
    <version>5.2.3</version>
</dependency>
<!-- Markdown 解析 -->
<dependency>
    <groupId>com.vladsch.flexmark</groupId>
    <artifactId>flexmark-all</artifactId>
    <version>0.64.0</version>
</dependency>
<!-- PDF 处理 -->
<dependency>
    <groupId>com.itextpdf</groupId>
    <artifactId>itext7-core</artifactId>
    <version>7.2.5</version>
    <type>pom</type>
</dependency>
```

### 5.2 核心控制器逻辑简述



```java
@RestController
@RequestMapping("/api")
public class ConvertController {

    @PostMapping("/convert")
    public ResponseEntity<Resource> convertFile(
            @RequestParam("file") MultipartFile file,            @RequestParam("targetFormat") String targetFormat) {

        // 1. 获取源文件后缀
        // 2. 调用对应的 Service 逻辑 (如 WordToMdService)
        // 3. 生成临时文件
        // 4. 返回文件流下载
        return null; 
    }
}
```

---

## 6. 非功能需求

1. **性能**: 单个 10MB 以内的文档转换时间应在 3 秒内完成。

2. **安全性**:
   
   - 上传文件后缀严格校验，防止恶意脚本注入。
   
   - 转换后的临时文件定期清理（使用 @Scheduled 定时任务）。

3. **易用性**: 无需注册即可使用基础转换功能。

---

## 7. 任务分解 (Roadmap)

**Phase 1**: 环境搭建，Spring Boot 基础骨架搭建，引入文件处理类库。

**Phase 2**: 开发 Word/Text 与 MD 的互转核心算法。

**Phase 3**: 前端原型设计，完成拖拽上传组件与预览组件。

**Phase 4**: 前后端联调，处理 PDF 转换的复杂样式兼容。

**Phase 5**: UI 细节打磨，部署上线。

---

这份文档可以作为您开发的蓝图。
