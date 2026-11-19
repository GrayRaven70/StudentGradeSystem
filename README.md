# 学生成绩管理系统 (Student Grade System)

这是一个基于 Java Web 开发的**学生成绩管理系统**。项目采用标准的 MVC 分层架构（Servlet + DAO + JSP/HTML），实现了从用户权限、课程管理、成绩录入到**班级行政班管理**的完整教务闭环流程。

## 🛠️ 技术栈
- **后端**：Java 17, Servlet (Jakarta EE), JDBC
- **前端**：HTML5, JSP, jQuery, Ajax, Bootstrap 5, DataTables (实现无刷新分页与搜索)
- **数据库**：MySQL 8.0+
- **服务器**：Apache Tomcat 10.1.x
- **架构模式**：DAO (数据访问对象) 模式, Filter 权限拦截

## ✨ 核心功能
- 🔐 **安全与权限**：基于 Session + Filter 拦截器，未登录无法访问后台；严格区分管理员、教师、学生三种角色，并在接口层进行越权校验。
- 👥 **用户信息管理**：管理员可对学生、教师、管理员账号进行增删改查。
- 📚 **课程信息管理**：支持课程的增删改查，支持一门课程关联多位任课教师（多对多关系）。
- 📝 **成绩管理模式**：管理员可为课程添加/移除学生，录入与修改成绩（支持成绩为 `NULL`），并一键导出成绩单 (CSV)。
- 🏫 **班级信息管理（新增）**：管理员可管理行政班级，指定班主任，添加/移除班级学生。
- 👨‍🏫 **教师端功能（新增）**：
    - **我的账户**：查看个人信息与修改密码。
    - **我的班级**：查看自己担任班主任的班级及学生名单（只读，可导出名单）。
    - **我的课程**：查看自己任教的课程及平均分，支持按课程修改/查看学生成绩。
- 👨‍🎓 **学生端功能（新增）**：
    - **我的账户**：查看个人信息与修改密码。
    - **我的成绩**：查看所有修读课程的成绩及及格状态，支持导出。
    - **我的课程**：查看所修课程及授课教师名单，支持导出。

---

## 🚀 快速开始

### 1. 环境准备
请确保你的电脑上已安装并配置好以下环境：
- **JDK 17** 或更高版本
- **Apache Tomcat 10.1.x**（注意：本项目基于 `jakarta.servlet.*`，**不能用 Tomcat 9 及以下版本**）
- **MySQL 8.0+**
- **IntelliJ IDEA** (推荐 2023/2024 版本)

### 2. 数据库配置
1. 打开 Navicat 或 MySQL 命令行，新建一个数据库命名为 `sgs`（字符集选 `utf8mb4`）。
2. 在 `sgs` 库中执行以下完整的 SQL 脚本，建立 5 张核心表并插入初始数据：

```sql
-- 1. 用户表 (包含管理员、教师、学生)
CREATE TABLE `sys_user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_no` varchar(50) NOT NULL COMMENT '工号/学号',
  `real_name` varchar(50) NOT NULL COMMENT '姓名',
  `user_group` enum('教师','学生','管理员') NOT NULL DEFAULT '学生' COMMENT '用户组',
  `department` varchar(100) DEFAULT NULL COMMENT '院系',
  `password` varchar(100) NOT NULL COMMENT '密码',
  `class_id` int DEFAULT NULL COMMENT '所属班级，指向 class.id',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_no` (`user_no`),
  KEY `idx_class_id` (`class_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 2. 班级表 (新增)
CREATE TABLE `class` (
  `id` int NOT NULL AUTO_INCREMENT,
  `class_no` varchar(50) NOT NULL COMMENT '班级编号',
  `class_name` varchar(100) NOT NULL COMMENT '班级名称',
  `head_teacher_id` int DEFAULT NULL COMMENT '班主任，指向 sys_user.id',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_class_no` (`class_no`),
  KEY `idx_head_teacher` (`head_teacher_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 3. 课程表
CREATE TABLE `course` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `course_no` varchar(50) NOT NULL COMMENT '课程号',
  `course_seq_no` varchar(50) NOT NULL COMMENT '课序号',
  `course_name` varchar(100) NOT NULL COMMENT '课程名',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 4. 课程-教师 关联表
CREATE TABLE `course_teacher` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `course_id` int(11) NOT NULL,
  `teacher_id` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_teacher` (`teacher_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 5. 课程-学生-成绩 关联表
CREATE TABLE `course_student` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `course_id` int(11) NOT NULL,
  `student_id` int(11) NOT NULL,
  `score` decimal(5,2) DEFAULT NULL COMMENT '成绩，允许为空',
  PRIMARY KEY (`id`),
  KEY `idx_student` (`student_id`),
  KEY `idx_course` (`course_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ========== 插入初始测试数据 ==========
-- 插入一个管理员 (admin14 / 123)
INSERT INTO `sys_user` (`user_no`, `real_name`, `user_group`, `password`) VALUES ('admin14', '超级管理员', '管理员', '123');
-- 插入一个教师 (T001 / 123)
INSERT INTO `sys_user` (`user_no`, `real_name`, `user_group`, `department`, `password`) VALUES ('T001', '张老师', '教师', '计算机学院', '123');
-- 插入一个学生 (S001 / 123)
INSERT INTO `sys_user` (`user_no`, `real_name`, `user_group`, `department`, `password`) VALUES ('S001', '李同学', '学生', '软件工程', '123');

-- 初始化一个班级并分配给 S001 和 T001
INSERT INTO `class` (`class_no`, `class_name`, `head_teacher_id`) 
SELECT 'C2024001', '软件工程2024级1班', id FROM `sys_user` WHERE `user_no`='T001';
UPDATE `sys_user` SET `class_id`=(SELECT id FROM `class` WHERE `class_no`='C2024001') WHERE `user_no`='S001';
```

### 3. 修改数据库连接信息
请在项目中**统一**修改数据库连接账号密码。打开以下 DAO 文件，找到 `URL` 变量，修改为你本地的账号密码（**注意确保 UserDao、CourseDao、ClassDao、StudentDao、TeacherDao 五个文件中的数据库账号密码完全一致**）：

```java
// 默认端口通常是 3306，请按需修改端口号和 username/password
private static final String URL =
        "jdbc:mysql://localhost:3306/sgs?user=root&password=你的密码&useUnicode=true&characterEncoding=UTF-8";
```

### 4. 在 IDEA 中运行
1. 使用 IntelliJ IDEA 打开本项目，等待 Maven 依赖下载完毕。
2. 配置 Tomcat 服务器：
    - 点击右上角 `Edit Configurations` -> `+` -> `Tomcat Server` -> `Local`。
    - 在 `Deployment` 标签页，点击 `+` -> `Artifact`，选择 `StudentGradeSystem:war exploded`。
    - 将 `Application context` 设置为 `/StudentGradeSystem`。
3. 启动 Tomcat，浏览器访问：`http://localhost:8080/StudentGradeSystem/`

---

## 🔑 默认测试账号
| 角色 | 账号 (学号/工号) | 密码 | 说明 |
| :--- | :--- | :--- | :--- |
| **管理员** | `admin14` | `123` | 登录后可管理用户、课程、配置成绩及班级信息 |
| **教师** | `T001` | `123` | 登录后可查看我的账户、我的班级、我的课程及录入成绩 |
| **学生** | `S001` | `123` | 登录后可查看我的账户、我的成绩、我的课程 |

---

## 📂 项目结构

```text
StudentGradeSystem/
├── src/main/java/org/example/
│   ├── User.java / Course.java / Clazz.java  # 实体类
│   ├── UserDao.java / CourseDao.java / ClassDao.java / StudentDao.java / TeacherDao.java # 数据库操作封装
│   ├── AuthFilter.java                       # Filter 权限拦截器 (白名单机制)
│   ├── LoginServlet / LogoutServlet / StudentLoginServlet / ChangePasswordServlet # 统一登录与注销
│   ├── User*Servlet / Course*Servlet         # 管理员：用户/课程增删改查接口
│   ├── CourseStudent*Servlet                 # 管理员：成绩管理及导出接口
│   ├── Class*Servlet / ClassStudent*Servlet  # 管理员：班级管理、班级学生管理接口
│   ├── Teacher*Servlet                       # 教师端：账户、课程、改分、导出接口
│   └── Student*Servlet                       # 学生端：账户、成绩、课程查询及导出接口
├── web/
│   ├── index.jsp                             # 系统角色选择入口
│   ├── *login.jsp                            # 管理员/教师/学生登录
│   ├── admin_dashboard.html                  # 管理员控制台 (用户/课程/班级)
│   ├── teacher_dashboard.html                # 教师控制台 (账户/班级/课程)
│   ├── student_dashboard.html                # 学生控制台 (账户/成绩/课程)
│   ├── user_list.html / course_list.html     # 各模块列表管理页面
│   ├── class_list.html / class_student_list.html # 班级及班级学生列表
│   └── ...                                   # 其他功能及表单页面
└── pom.xml                                   # Maven 依赖配置
```

---

## ⚠️ 注意事项（开发者必读）
1. **Session Role 标识**：为了保证权限校验一致，管理员的 Session `role` 字段已统一写为 `"管理员"`（而非英文 `admin`），教师的 `role` 为 `"教师"`，学生的 `role` 为 `"学生"`。后端通过 `session.getAttribute("role")` 获取身份。
2. **安全边界**：
    - 学生端接口直接基于 Session 中保存的 `user_no` 查询自己的数据，不接收前端传递的 ID，从根本上杜绝越权。
    - 教师端接口（如修改成绩）强制校验该课程是否属于该教师（通过 `course_teacher` 表查询）。
    - 班级管理接口强制校验教师是否为该班班主任，增删学生仅限管理员操作。