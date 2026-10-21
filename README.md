# 学生成绩管理系统 (Student Grade System)

这是一个基于 Java Web 开发的**学生成绩管理系统**。项目采用标准的 MVC 分层架构（Servlet + DAO + JSP/HTML），实现了从用户权限、课程管理到成绩录入与导出的完整教务闭环流程。

## 🛠️ 技术栈
- **后端**：Java 17, Servlet (Jakarta EE), JDBC
- **前端**：HTML5, JSP, jQuery, Ajax, Bootstrap 5, DataTables (实现无刷新分页与搜索)
- **数据库**：MySQL 8.0+
- **服务器**：Apache Tomcat 10.1.x
- **架构模式**：DAO (数据访问对象) 模式, Filter 权限拦截

## ✨ 核心功能
- 🔐 **安全与权限**：基于 Session + Filter 拦截器，未登录无法访问后台；支持管理员、教师、学生多角色。
- 👥 **用户信息管理**：管理员可对学生、教师、管理员账号进行增删改查。
- 📚 **课程信息管理**：支持课程的增删改查，支持一门课程关联多位任课教师（多对多关系）。
- 📝 **学生成绩管理**：
  - **管理模式**：为课程添加/移除学生，录入与修改成绩（支持成绩为 `NULL`）。
  - **查看模式**：只读查看学生成绩，支持一键**导出成绩单 (CSV / Excel 格式)**。
- 🔍 **前端交互**：全站基于 Ajax 异步交互，表格自带全局搜索、分页、排序功能。

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
2. 在 `sgs` 库中执行以下完整的 SQL 脚本，建立 4 张核心表并插入初始数据：

```sql
-- 1. 用户表 (包含管理员、教师、学生)
CREATE TABLE `sys_user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_no` varchar(50) NOT NULL COMMENT '工号/学号',
  `real_name` varchar(50) NOT NULL COMMENT '姓名',
  `user_group` enum('教师','学生','管理员') NOT NULL DEFAULT '学生' COMMENT '用户组',
  `department` varchar(100) DEFAULT NULL COMMENT '院系',
  `password` varchar(100) NOT NULL COMMENT '密码',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_no` (`user_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 2. 课程表
CREATE TABLE `course` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `course_no` varchar(50) NOT NULL COMMENT '课程号',
  `course_seq_no` varchar(50) NOT NULL COMMENT '课序号',
  `course_name` varchar(100) NOT NULL COMMENT '课程名',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 3. 课程-教师 关联表
CREATE TABLE `course_teacher` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `course_id` int(11) NOT NULL,
  `teacher_id` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 4. 课程-学生-成绩 关联表
CREATE TABLE `course_student` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `course_id` int(11) NOT NULL,
  `student_id` int(11) NOT NULL,
  `score` decimal(5,2) DEFAULT NULL COMMENT '成绩，允许为空',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ========== 插入初始测试数据 ==========
-- 插入一个管理员 (admin14 / 123)
INSERT INTO `sys_user` (`user_no`, `real_name`, `user_group`, `password`) VALUES ('admin14', '超级管理员', '管理员', '123');
-- 插入一个教师 (T001 / 123)
INSERT INTO `sys_user` (`user_no`, `real_name`, `user_group`, `department`, `password`) VALUES ('T001', '张老师', '教师', '计算机学院', '123');
-- 插入一个学生 (S001 / 123)
INSERT INTO `sys_user` (`user_no`, `real_name`, `user_group`, `department`, `password`) VALUES ('S001', '李同学', '学生', '软件工程', '123');
```

### 3. 修改数据库连接信息
打开项目中的 `src/main/java/org/example/UserDao.java` 和 `CourseDao.java` 文件，找到 `URL` 变量，修改为你本地的账号密码：

```java
// 请将 password=你的密码 替换为真实的密码，默认端口通常是 3306，请按需修改
private static final String URL =
        "jdbc:mysql://127.0.0.1:3306/sgs?user=root&password=你的密码&useUnicode=true&characterEncoding=UTF-8";
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
| **管理员** | `admin14` | `123` | 登录后可管理用户、管理课程、配置成绩 |
| **教师** | `T001` | `123` | 登录后可修改密码（暂未开发主页面）|
| **学生** | `S001` | `123` | 登录后可修改密码（暂未开发主页面）|

---

## 📂 项目结构

```text
StudentGradeSystem/
├── src/main/java/org/example/
│   ├── User.java / Course.java      # 实体类
│   ├── UserDao.java / CourseDao.java # 数据库操作封装 (DAO模式)
│   ├── LoginServlet / LogoutServlet  # 统一登录与注销
│   ├── AuthFilter.java               # Filter 权限拦截器
│   ├── User*Servlet.java             # 用户增删改查接口
│   ├── Course*Servlet.java           # 课程及教师关联接口
│   └── CourseStudent*Servlet.java    # 成绩管理及导出接口
├── web/
│   ├── index.jsp                     # 系统角色选择入口
│   ├── admin_login.jsp               # 管理员登录
│   ├── teacher_login.jsp             # 教师登录
│   ├── student_login.jsp             # 学生登录
│   ├── admin_dashboard.html          # 管理员控制台首页
│   ├── user_list.html                # 用户管理
│   ├── course_list.html              # 课程管理列表
│   ├── course_modify.html            # 课程与教师管理
│   └── course_student_modify.html    # 学生成绩管理/查看/导出
└── pom.xml                           # Maven 依赖配置
```

```
