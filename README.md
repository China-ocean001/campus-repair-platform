# 🏫 校园智能报修平台

> Campus Intelligent Repair Platform — 全栈校园报修服务平台

<div align="center">

[![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.2.5-brightgreen?style=flat&logo=springboot)](https://spring.io/projects/spring-boot)
[![Vue](https://img.shields.io/badge/Vue-3.4-4FC08D?style=flat&logo=vuedotjs)](https://vuejs.org/)
[![TypeScript](https://img.shields.io/badge/TypeScript-5.4-3178C6?style=flat&logo=typescript)](https://www.typescriptlang.org/)
[![Element Plus](https://img.shields.io/badge/Element_Plus-2.7-409EFF?style=flat&logo=element)](https://element-plus.org/)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-4479A1?style=flat&logo=mysql)](https://www.mysql.com/)
[![Redis](https://img.shields.io/badge/Redis-7.x-DC382D?style=flat&logo=redis)](https://redis.io/)
[![ECharts](https://img.shields.io/badge/ECharts-5.5-AA344D?style=flat)](https://echarts.apache.org/)
[![Pinia](https://img.shields.io/badge/Pinia-2.1-FFD859?style=flat)](https://pinia.vuejs.org/)
[![Vite](https://img.shields.io/badge/Vite-5.2-646CFF?style=flat&logo=vite)](https://vitejs.dev/)
[![License](https://img.shields.io/badge/License-MIT-yellow?style=flat)](LICENSE)

</div>

---

## 📑 目录

- [📖 项目简介](#-项目简介)
- [🏗️ 系统架构](#️-系统架构)
- [✨ 功能特性](#-功能特性)
- [🔄 业务流程](#-业务流程)
- [🛠 技术栈](#-技术栈)
- [📁 项目结构](#-项目结构)
- [🗄️ 数据库设计](#️-数据库设计)
- [📡 API 接口](#-api-接口)
- [🚀 快速开始](#-快速开始)
- [📊 项目统计](#-项目统计)
- [🐛 常见问题](#-常见问题)

---

## 📖 项目简介

面向高校的**校园智能报修服务平台**，实现从报修提交到服务评价的全流程数字化管理。系统采用前后端分离架构，支持三种角色协作，通过 WebSocket 实现实时通知，使用状态机管理工单流转。

### 适用场景

- 高校校园设施报修（宿舍、教室、实验室等）
- 中小学后勤维修管理
- 企业园区设备报修

---

## 🏗️ 系统架构

```
┌─────────────────────────────────────────────────────────────┐
│                        前端 (Vue 3)                         │
│  ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌───────────────┐  │
│  │ 学生模块  │ │ 维修工   │ │ 管理员   │ │  公共模块     │  │
│  │ 报修/评价 │ │ 接单/处理│ │ 派单/统计│ │ 登录/布局     │  │
│  └──────────┘ └──────────┘ └──────────┘ └───────────────┘  │
│                         │ HTTP + WebSocket                   │
├─────────────────────────────────────────────────────────────┤
│                     后端 (Spring Boot 3)                     │
│  ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌───────────────┐  │
│  │Controller│ │ Service  │ │ Security │ │  WebSocket    │  │
│  │ REST API │ │ 业务逻辑 │ │ JWT鉴权  │ │  实时推送     │  │
│  └──────────┘ └──────────┘ └──────────┘ └───────────────┘  │
│  ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌───────────────┐  │
│  │  Mapper  │ │  Entity  │ │StateMach │ │  Exception    │  │
│  │ 数据访问 │ │  实体    │ │ 状态机   │ │  全局异常     │  │
│  └──────────┘ └──────────┘ └──────────┘ └───────────────┘  │
├─────────────────────────────────────────────────────────────┤
│                    数据层                                   │
│  ┌──────────┐ ┌──────────┐                                 │
│  │  MySQL   │ │  Redis   │                                 │
│  │ 主数据库 │ │ 缓存/会话│                                 │
│  └──────────┘ └──────────┘                                 │
└─────────────────────────────────────────────────────────────┘
```

---

## ✨ 功能特性

### 👨‍🎓 学生端
| 功能 | 描述 |
|------|------|
| 提交报修单 | 选择报修类型、描述故障、上传图片 |
| 我的订单 | 查看报修历史及实时处理进度 |
| 服务评价 | 对已完成的服务进行星级评价 |
| 消息通知 | 接收工单状态变更实时推送 |

### 👷 维修工端
| 功能 | 描述 |
|------|------|
| 工单池 | 查看待接单的所有报修请求 |
| 我的任务 | 已接单/处理中的工单管理 |
| 状态更新 | 接单 → 处理中 → 完成 |
| 完工确认 | 上传维修后的照片，填写处理说明 |

### 👑 管理员端
| 功能 | 描述 |
|------|------|
| 数据看板 | ECharts 可视化：报修趋势、完成率、满意度 |
| 工单分配 | 手动派单或系统自动分配 |
| 用户管理 | 学生/维修工账号管理 |
| 公告发布 | 系统通知公告编辑与发布 |
| 评价管理 | 查看所有服务评价明细 |

---

## 🔄 业务流程

```
学生提交报修 ──→ 管理员审核派单 ──→ 维修工接单
                                       │
                                       ↓
          ←──── 学生评价 ←────── 维修工完成
          
状态流转：PENDING → ASSIGNED → ACCEPTED → PROCESSING → COMPLETED → EVALUATED
          待处理     已分配      已接单      处理中      已完成      已评价
```

---

## 🛠 技术栈

| 层级 | 技术 | 版本 | 说明 |
|------|------|------|------|
| **后端框架** | Spring Boot | 3.2.5 | Java 17+, 自动配置 |
| **ORM** | MyBatis-Plus | 3.5.x | 简化 CRUD, 分页插件 |
| **安全** | Spring Security + JWT | - | 无状态认证, 角色权限 |
| **实时通信** | WebSocket | - | STOMP 协议推送通知 |
| **数据库** | MySQL | 8.0+ | 主数据存储 |
| **缓存** | Redis | 7.x | 会话缓存, 消息队列 |
| **前端框架** | Vue 3 | 3.4 | Composition API |
| **类型系统** | TypeScript | 5.4 | 类型安全 |
| **UI 组件** | Element Plus | 2.7 | 企业级组件库 |
| **图表** | ECharts | 5.5 | 数据可视化 |
| **状态管理** | Pinia | 2.1 | Vue 3 官方推荐 |
| **HTTP 客户端** | Axios | 1.6 | 请求拦截/封装 |
| **构建工具** | Vite | 5.2 | 极速 HMR |

---

## 📁 项目结构

```
campus-repair-platform/
│
├── backend/                          # 🔧 后端 Spring Boot
│   ├── pom.xml                       # Maven 依赖配置
│   └── src/main/java/com/campus/repair/
│       ├── RepairApplication.java    # 🚀 启动类
│       ├── controller/               # 🌐 REST 控制器
│       │   ├── AuthController.java           # 登录认证
│       │   ├── RepairOrderController.java    # 报修单 CRUD
│       │   ├── UserController.java           # 用户管理
│       │   ├── NoticeController.java         # 公告管理
│       │   └── StatisticsController.java     # 统计数据
│       ├── service/                  # 💼 业务逻辑
│       │   ├── RepairOrderService.java
│       │   ├── UserService.java
│       │   ├── NoticeService.java
│       │   ├── StatisticsService.java
│       │   └── impl/                 # 实现类
│       ├── mapper/                   # 🗄️ 数据访问 (MyBatis-Plus)
│       │   ├── RepairOrderMapper.java
│       │   ├── UserMapper.java
│       │   ├── NoticeMapper.java
│       │   ├── EvaluationMapper.java
│       │   └── OrderLogMapper.java
│       ├── entity/                   # 📦 数据库实体
│       │   ├── User.java
│       │   ├── RepairOrder.java
│       │   ├── Notice.java
│       │   ├── Evaluation.java
│       │   └── OrderLog.java
│       ├── dto/                      # 📥 请求参数对象
│       │   ├── LoginDTO.java
│       │   ├── RepairOrderCreateDTO.java
│       │   ├── AssignOrderDTO.java
│       │   ├── EvaluationDTO.java
│       │   ├── NoticeDTO.java
│       │   └── CreateUserDTO.java
│       ├── vo/                       # 📤 响应视图对象
│       │   ├── LoginVO.java
│       │   ├── RepairOrderVO.java
│       │   ├── UserVO.java
│       │   ├── NoticeVO.java
│       │   └── StatisticsVO.java
│       ├── security/                 # 🔐 JWT 安全
│       │   ├── JwtTokenProvider.java
│       │   ├── JwtAuthenticationFilter.java
│       │   └── UserDetailsServiceImpl.java
│       ├── websocket/                # 📡 实时推送
│       │   └── NotifyWebSocketServer.java
│       ├── statemachine/             # 🔄 状态机
│       │   ├── OrderStatus.java
│       │   └── RepairOrderStateMachine.java
│       ├── common/                   # 🧰 公共组件
│       │   ├── Result.java           # 统一响应封装
│       │   ├── PageResult.java       # 分页结果
│       │   ├── BusinessException.java
│       │   └── GlobalExceptionHandler.java
│       └── config/                   # ⚙️ 配置
│           ├── SecurityConfig.java
│           ├── WebSocketConfig.java
│           ├── MybatisPlusConfig.java
│           └── MybatisPlusMetaHandler.java
│
├── frontend/                         # 🎨 前端 Vue 3
│   ├── package.json
│   ├── vite.config.ts
│   ├── tsconfig.json
│   ├── index.html
│   └── src/
│       ├── App.vue                   # 根组件
│       ├── main.ts                   # 入口文件
│       ├── router/index.ts           # 路由配置
│       ├── store/auth.ts             # Pinia 认证状态
│       ├── api/index.ts              # Axios API 封装
│       ├── utils/http.ts             # HTTP 工具
│       ├── styles/main.css           # 全局样式
│       └── views/                    # 📄 页面组件
│           ├── common/
│           │   ├── LoginView.vue     # 登录页
│           │   └── LayoutView.vue    # 布局框架
│           ├── admin/
│           │   ├── DashboardView.vue      # 数据看板
│           │   ├── OrderManageView.vue    # 工单管理
│           │   ├── UserManageView.vue     # 用户管理
│           │   └── NoticeManageView.vue   # 公告管理
│           ├── student/
│           │   ├── SubmitOrderView.vue    # 提交报修
│           │   └── MyOrdersView.vue       # 我的订单
│           └── worker/
│               └── WorkerOrdersView.vue   # 工单处理
│
├── sql/
│   └── init.sql                      # 🗄️ 数据库初始化
│
└── docs/
    └── 课程设计报告.md                # 📄 设计文档
```

---

## 🗄️ 数据库设计

### E-R 关系

```
┌──────────┐     ┌──────────────┐     ┌──────────────┐
│   User   │────→│ RepairOrder  │←────│  OrderLog    │
│  用户表  │     │   报修单     │     │  操作日志    │
└──────────┘     └──────┬───────┘     └──────────────┘
       │                │
       │                ↓
       │         ┌──────────────┐
       └────────→│ Evaluation   │
                 │   服务评价   │
                 └──────────────┘
                         
┌──────────┐
│  Notice  │ (独立公告表)
│  公告表  │
└──────────┘
```

### 核心表结构

**user 用户表**

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键, 自增 |
| username | VARCHAR(50) | 用户名, 唯一 |
| password | VARCHAR(255) | BCrypt 加密 |
| real_name | VARCHAR(50) | 真实姓名 |
| phone | VARCHAR(20) | 手机号 |
| role | ENUM('STUDENT','WORKER','ADMIN') | 角色 |
| avatar | VARCHAR(255) | 头像 URL |
| status | TINYINT | 状态 (1:正常, 0:禁用) |
| create_time | DATETIME | 创建时间 |

**repair_order 报修单表**

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键 |
| student_id | BIGINT | 报修学生 |
| worker_id | BIGINT | 维修工 (可空) |
| type | VARCHAR(50) | 报修类型 |
| description | TEXT | 故障描述 |
| image_url | VARCHAR(500) | 照片 URL |
| status | VARCHAR(20) | 状态 (状态机管理) |
| location | VARCHAR(200) | 报修位置 |
| create_time | DATETIME | 创建时间 |
| complete_time | DATETIME | 完成时间 |

---

## 📡 API 接口

### 认证模块 (`/api/auth`)

| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| POST | `/api/auth/login` | 用户登录 | 否 |
| POST | `/api/auth/register` | 学生注册 | 否 |

### 报修模块 (`/api/orders`)

| 方法 | 路径 | 说明 | 角色 |
|------|------|------|------|
| POST | `/api/orders` | 创建报修单 | 学生 |
| GET | `/api/orders` | 分页查询 | 全部 |
| GET | `/api/orders/{id}` | 详情查询 | 全部 |
| PUT | `/api/orders/{id}/status` | 更新状态 | 维修工 |
| PUT | `/api/orders/{id}/assign` | 分配维修工 | 管理员 |
| POST | `/api/orders/{id}/evaluate` | 评价 | 学生 |

### 用户模块 (`/api/users`)

| 方法 | 路径 | 说明 | 角色 |
|------|------|------|------|
| GET | `/api/users` | 分页查询 | 管理员 |
| POST | `/api/users` | 创建用户 | 管理员 |
| PUT | `/api/users/{id}` | 更新用户 | 管理员 |
| DELETE | `/api/users/{id}` | 删除用户 | 管理员 |

### 统计模块 (`/api/statistics`)

| 方法 | 路径 | 说明 | 角色 |
|------|------|------|------|
| GET | `/api/statistics/dashboard` | 看板数据 | 管理员 |
| GET | `/api/statistics/trend` | 趋势分析 | 管理员 |

### 统一响应格式

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {},
  "timestamp": 1718318400000
}
```

---

## 🚀 快速开始

### 📋 环境要求

| 软件 | 版本 | 说明 |
|------|------|------|
| JDK | 17+ | 后端运行环境 |
| MySQL | 8.0+ | 主数据库 |
| Redis | 7.x | 缓存 (可选) |
| Node.js | 18+ | 前端构建 |
| Maven | 3.8+ | 后端构建 |
| npm/pnpm | 9+ | 前端包管理 |

### ⚙️ 环境变量配置

```yaml
# backend/src/main/resources/application.yml
server:
  port: 8080

spring:
  datasource:
    url: jdbc:mysql://localhost:3306/campus_repair
    username: root
    password: your_password       # ⚠️ 修改为你的密码
  data:
    redis:
      host: localhost
      port: 6379

app:
  jwt:
    secret: your-jwt-secret-key   # ⚠️ 生产环境请更换
    expiration: 86400000          # 24小时
```

### 🔧 后端启动

```bash
cd backend

# 1. 创建数据库
mysql -u root -p -e "CREATE DATABASE IF NOT EXISTS campus_repair DEFAULT CHARSET utf8mb4"

# 2. 导入表结构和初始数据
mysql -u root -p campus_repair < ../sql/init.sql

# 3. 编译启动
mvn clean package -DskipTests
java -jar target/repair-1.0.0.jar

# 或开发模式
mvn spring-boot:run
```

后端启动后访问: http://localhost:8080

### 🎨 前端启动

```bash
cd frontend

# 安装依赖
npm install

# 开发模式启动 (HMR热更新)
npm run dev

# 生产构建
npm run build

# 预览生产构建
npm run preview
```

前端启动后访问: http://localhost:5173

### 🔑 默认测试账号

| 角色 | 用户名 | 密码 |
|------|--------|------|
| 管理员 | `admin` | `123456` |
| 维修工 | `worker1` | `123456` |
| 学生 | `student1` | `123456` |

---

## 📊 项目统计

| 指标 | 数量 |
|------|------|
| 后端 Java 类 | 35+ |
| 前端 Vue 组件 | 13 个页面 |
| 数据库表 | 7 张 |
| REST API 端点 | 20+ |
| WebSocket 端点 | 2 |
| 状态机状态 | 6 种 |

---

## 🐛 常见问题

<details>
<summary><b>Q: 启动报错 "Access denied for user"</b></summary>

请检查 `application.yml` 中的数据库用户名和密码是否正确。
</details>

<details>
<summary><b>Q: 前端请求后端接口跨域</b></summary>

后端已配置 CORS，允许 `localhost:5173`。如需修改，查看 `WebConfig.java`。
</details>

<details>
<summary><b>Q: WebSocket 连接不上</b></summary>

确认防火墙未拦截 8080 端口，且前端 WebSocket 连接地址与后端一致。
</details>

<details>
<summary><b>Q: Redis 连接失败</b></summary>

如果未安装 Redis，可在 `application.yml` 中注释 Redis 配置。项目核心功能不依赖 Redis。
</details>

---

## 📝 开发计划

- [ ] Docker 容器化部署
- [ ] 移动端小程序适配
- [ ] AI 智能派单推荐
- [ ] 报修图片 AI 识别分类
- [ ] 导出 Excel 报表

---

## ☕ 支持作者

如果这个项目对你有帮助，欢迎请我喝杯咖啡~

<div align="center">

| <img src="sponsor/alipay.jpg" width="260" alt="支付宝"> | <img src="sponsor/wechat.jpg" width="260" alt="微信支付"> |
|:---:|:---:|
| **支付宝** | **微信支付** |

</div>

---

## 📄 License

MIT License — 仅供学习交流使用

---

<div align="center">

**⭐ 如果这个项目对你有帮助，请给一个 Star！**

</div>
