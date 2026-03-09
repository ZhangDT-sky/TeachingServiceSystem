# Teaching Service System

## 项目简介

Teaching Service System（教学服务管理系统）是一个基于 Spring Boot 3.5.3 开发的现代化教学管理平台，旨在为高校提供全面、高效、智能化的教学管理解决方案。系统涵盖了从学生管理、教师管理到课程管理、成绩管理、选课管理等教学全流程的核心功能，采用了微服务架构设计理念，具有良好的扩展性和可维护性。

### 项目特点

- **现代化技术栈**：采用 Spring Boot 3.5.3、Java 17 等最新技术，确保系统性能和安全性
- **完整的教学管理流程**：覆盖从学生入学到毕业的全流程管理
- **智能化功能**：提供选课推荐、成绩分析、教学质量评估等智能功能
- **高并发支持**：通过 Redis 缓存、RabbitMQ 异步处理等技术支持高并发访问
- **数据安全**：采用 JWT 身份认证、权限控制等多重安全机制
- **易用性**：提供清晰的 API 文档和友好的用户界面

### 应用场景

- 高校教学管理部门
- 学院教学办公室
- 教师课程管理
- 学生选课与成绩查询

## 技术栈

### 后端技术

| 技术                | 版本          | 用途                                  |
|---------------------|---------------|---------------------------------------|
| Spring Boot         | 3.5.3         | 项目框架，简化 Spring 应用开发        |
| Spring Web          | 3.5.3         | Web 开发，提供 RESTful API 支持       |
| Spring Data Redis   | 3.5.3         | Redis 缓存，提高系统性能              |
| MyBatis Plus        | 3.5.12        | ORM 框架，简化数据库操作              |
| MySQL               | 8.0+          | 关系型数据库，存储系统数据            |
| RabbitMQ            | 3.8+          | 消息队列，处理异步任务                |
| JWT                 | 0.11.5        | 身份认证，实现无状态登录              |
| Lombok              | 1.18.30       | 简化代码，减少样板代码                |
| Fastjson2           | 2.0.42        | JSON 处理，实现对象与 JSON 互转       |
| Apache POI          | 5.2.5         | Excel 处理，实现数据导入导出          |
| Springdoc OpenAPI   | 2.2.0         | API 文档，自动生成接口文档            |
| Spring Validation   | 3.5.3         | 数据校验，确保输入数据的合法性        |

### 开发环境

- **Java 17** - 编程语言，提供更好的性能和安全性
- **Maven** - 项目构建工具，管理依赖和构建流程
- **IntelliJ IDEA** - 推荐的 IDE 开发工具
- **Git** - 版本控制系统

## 功能模块

### 1. 用户管理模块

#### 管理员管理
- 管理员账户的增删改查
- 权限分配与管理
- 系统日志查看

#### 学生管理
- 学生信息的录入、修改、删除
- 学生学籍管理
- 学生成绩查询权限控制

#### 教师管理
- 教师信息的录入、修改、删除
- 教师授课资格管理
- 教师业绩统计

### 2. 教学资源管理模块

#### 学院管理
- 学院信息的增删改查
- 学院负责人管理

#### 专业管理
- 专业信息的增删改查
- 专业培养方案管理

#### 班级管理
- 班级信息的增删改查
- 班级学生管理

#### 课程管理
- 课程信息的增删改查
- 课程大纲管理
- 课程学分设置

### 3. 课程开设与选课模块

#### 课程开设管理
- 学期课程开设计划
- 教师授课安排
- 教室分配

#### 选课管理
- 学生选课申请
- 选课冲突检测
- 选课结果公布
- 退课管理

### 4. 成绩管理模块

#### 成绩录入
- 教师成绩录入
- 成绩修改权限控制
- 成绩提交确认

#### 成绩查询
- 学生成绩查询
- 教师成绩查询
- 班级成绩统计

#### 成绩统计分析
- 课程成绩分布
- 学生成绩排名
- 教学质量评估依据

### 5. 排课与课表模块

#### 班级排课
- 排课规则设置
- 排课冲突检测
- 课表生成

#### 课表查询
- 学生个人课表
- 教师个人课表
- 教室使用课表

### 6. 考试管理模块

#### 考试安排
- 考试时间安排
- 考场分配
- 监考教师安排

#### 考试成绩管理
- 考试成绩录入
- 考试成绩查询
- 考试成绩统计

### 7. 系统功能模块

#### 身份认证
- JWT 令牌生成与验证
- 登录权限控制
- 密码加密存储

#### 缓存优化
- Redis 缓存热点数据
- 缓存失效策略
- 缓存一致性维护

#### 异步处理
- RabbitMQ 消息队列
- 异步任务处理
- 批量数据导出

#### 数据导入导出
- Excel 数据导入
- Excel 数据导出
- 数据备份与恢复

## 快速开始

### 环境要求

- JDK 17+
- MySQL 8.0+
- Redis 6.0+
- RabbitMQ 3.8+

### 项目构建

```bash
# 克隆项目
git clone <repository-url>

# 进入项目目录
cd TeachingServiceSystem

# 构建项目
mvn clean install
```

### 配置文件

修改 `src/main/resources/application.yml` 配置文件：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/teaching_system?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai
    username: root
    password: your_password
  redis:
    host: localhost
    port: 6379
    password: your_password
  rabbitmq:
    host: localhost
    port: 5672
    username: guest
    password: guest
```

### 启动项目

```bash
# 启动项目
mvn spring-boot:run
```

### API 文档

启动项目后，访问以下地址查看 API 文档：

- Swagger UI: http://localhost:8080/swagger-ui.html
- OpenAPI JSON: http://localhost:8080/v3/api-docs

## 项目结构

```
└── com.example.ZhangDT
    ├── TeachingServiceSystemApplication.java # 项目启动类
    ├── bean                                # 实体类
    │   ├── Admin.java                      # 管理员实体
    │   ├── Student.java                    # 学生实体
    │   ├── Teacher.java                    # 教师实体
    │   ├── College.java                    # 学院实体
    │   ├── Major.java                      # 专业实体
    │   ├── Class.java                      # 班级实体
    │   ├── Course.java                     # 课程实体
    │   ├── CourseOffer.java                # 课程开设实体
    │   ├── CourseSelect.java               # 选课实体
    │   ├── Grade.java                      # 成绩实体
    │   ├── ClassSchedule.java              # 课表实体
    │   ├── dto                             # 数据传输对象
    │   └── exam                            # 考试相关实体
    ├── config                              # 配置类
    │   ├── MyMetaObjectHandler.java        # MyBatis Plus 自动填充配置
    │   ├── RabbitConfig.java               # RabbitMQ 配置
    │   └── RedisScriptConfig.java          # Redis 脚本配置
    ├── consumer                            # 消息队列消费者
    │   └── StudentCourseConsumer.java      # 学生课程相关消费者
    ├── controller                          # 控制器
    │   ├── AdminController.java            # 管理员控制器
    │   ├── AuthController.java             # 认证控制器
    │   ├── StudentController.java          # 学生控制器
    │   ├── TeacherController.java          # 教师控制器
    │   ├── CollegeController.java          # 学院控制器
    │   ├── MajorController.java            # 专业控制器
    │   ├── ClassController.java            # 班级控制器
    │   ├── CourseController.java           # 课程控制器
    │   ├── CourseOfferingController.java   # 课程开设控制器
    │   ├── CourseSelectController.java     # 选课控制器
    │   ├── GradeController.java            # 成绩控制器
    │   ├── ClassScheduleController.java    # 课表控制器
    │   ├── ExportController.java           # 导出控制器
    │   └── exam                            # 考试相关控制器
    ├── core                                # 核心类
    │   └── ResponseMessage.java            # 统一响应格式
    ├── mapper                              # Mapper 接口
    │   ├── AdminMapper.java                # 管理员 Mapper
    │   ├── StudentMapper.java              # 学生 Mapper
    │   ├── TeacherMapper.java              # 教师 Mapper
    │   └── ...                             # 其他 Mapper 接口
    ├── service                             # 业务逻辑
    │   ├── AdminService.java               # 管理员服务接口
    │   ├── StudentService.java             # 学生服务接口
    │   ├── TeacherService.java             # 教师服务接口
    │   └── impl                            # 服务实现类
    └── util                                # 工具类
        └── JwtTokenUtil.java               # JWT 工具类
```

### 目录说明

- **bean/**: 包含系统所有实体类，映射数据库表结构
- **config/**: 系统配置类，包括第三方组件配置
- **consumer/**: 消息队列消费者，处理异步任务
- **controller/**: 控制器层，处理 HTTP 请求
- **core/**: 核心类，包括统一响应格式等
- **mapper/**: 数据访问层，与数据库交互
- **service/**: 业务逻辑层，实现系统核心业务
- **util/**: 工具类，提供通用功能支持