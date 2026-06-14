# 🏫 校园智能报修平台

> Campus Intelligent Repair Platform — 全栈校园报修服务平台

[![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.2.5-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Vue](https://img.shields.io/badge/Vue-3.4-4FC08D.svg)](https://vuejs.org/)
[![TypeScript](https://img.shields.io/badge/TypeScript-5.4-blue.svg)](https://www.typescriptlang.org/)
[![Element Plus](https://img.shields.io/badge/Element_Plus-2.7-409EFF.svg)](https://element-plus.org/)
[![License](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)

## 📖 项目简介

面向高校的智能报修服务平台，支持**学生报修 → 管理员派单 → 维修工处理 → 评价反馈**的完整业务流程。

## ✨ 功能特性

### 三角色体系
- 👨‍🎓 **学生**：提交报修单、查看我的订单、评价服务
- 👷 **维修工**：接单/处理/完成报修、查看待处理工单
- 👑 **管理员**：用户管理、工单分配、公告发布、数据统计看板

### 核心功能
- 🔐 JWT 认证 + Spring Security 权限控制
- 📡 WebSocket 实时通知推送
- 🔄 报修单状态机（待处理 → 已接单 → 处理中 → 已完成 → 已评价）
- 📊 ECharts 数据可视化统计看板
- 📢 公告通知管理
- ⭐ 服务评价体系

## 🛠 技术栈

| 层级 | 技术 |
|------|------|
| **后端框架** | Spring Boot 3.2.5 |
| **ORM** | MyBatis-Plus 3.5 |
| **安全** | Spring Security + JWT |
| **实时通信** | WebSocket |
| **数据库** | MySQL 8.0 + Redis |
| **前端框架** | Vue 3 + TypeScript |
| **UI 组件** | Element Plus 2.7 |
| **图表** | ECharts 5.5 |
| **状态管理** | Pinia 2 |
| **构建工具** | Vite 5 |

## 📁 项目结构

```
campus-repair-platform/
├── backend/                    # 后端 Spring Boot
│   ├── src/main/java/com/campus/repair/
│   │   ├── controller/         # 控制器层
│   │   ├── service/            # 业务逻辑层
│   │   ├── mapper/             # 数据访问层
│   │   ├── entity/             # 实体类
│   │   ├── dto/                # 数据传输对象
│   │   ├── vo/                 # 视图对象
│   │   ├── security/           # JWT 安全配置
│   │   ├── websocket/          # WebSocket 通知
│   │   ├── statemachine/       # 状态机
│   │   └── config/             # 配置类
│   └── src/main/resources/
│       └── application.yml     # 应用配置
├── frontend/                   # 前端 Vue 3
│   └── src/
│       ├── views/              # 页面视图
│       │   ├── admin/          # 管理员页面
│       │   ├── student/        # 学生页面
│       │   ├── worker/         # 维修工页面
│       │   └── common/         # 公共页面（登录/布局）
│       ├── router/             # 路由配置
│       ├── store/              # Pinia 状态管理
│       ├── api/                # API 封装
│       └── utils/              # 工具函数
├── sql/
│   └── init.sql                # 数据库初始化脚本
└── docs/
    └── 课程设计报告.md          # 课程设计文档
```

## 🚀 快速开始

### 环境要求
- JDK 17+
- MySQL 8.0+
- Redis (可选)
- Node.js 18+

### 后端启动
```bash
cd backend

# 1. 创建数据库
mysql -u root -p -e "CREATE DATABASE campus_repair"

# 2. 导入初始化脚本
mysql -u root -p campus_repair < ../sql/init.sql

# 3. 修改 application.yml 中的数据库密码

# 4. 启动后端
./mvnw spring-boot:run
```

### 前端启动
```bash
cd frontend

# 安装依赖
npm install

# 启动开发服务器
npm run dev
```

访问 http://localhost:5173

## 📊 项目统计

- 后端 Java 文件：30+
- 前端 Vue 组件：13 个页面
- 数据库表：7 张
- REST API：20+ 个端点

## 📄 License

MIT License — 仅供学习交流使用
