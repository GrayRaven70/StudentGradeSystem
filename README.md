# 学生成绩管理系统 (Student Grade System)

这是一个基于 Java Web 的简易学生/用户信息管理系统，实现了用户信息的增删改查（CRUD）功能。

## 🛠️ 技术栈
- **后端**：Java 17, Servlet (Jakarta EE), JDBC
- **前端**：HTML5, jQuery, Bootstrap 5, DataTables (带分页/搜索的表格插件)
- **数据库**：MySQL 8.0+
- **服务器**：Apache Tomcat 10.1.42


### 1. 环境准备
请确保你的电脑上已经安装并配置好以下环境：
- **JDK 17** 或更高版本
- **Apache Tomcat 10.1.x**（注意：本项目使用 `jakarta.servlet.*`，不能用 Tomcat 9 及以下版本）
- **MySQL 8.0+**
- **IntelliJ IDEA** (推荐 2023/2024 版本)

### 2. 数据库配置
1. 打开 Navicat 或 MySQL 命令行工具。
2. 新建一个数据库，命名为 `sgs`（字符集建议选 `utf8mb4`）。
3. 在 `sgs` 数据库下执行以下 SQL 语句建表：

CREATE TABLE `sys_user` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_no` varchar(50) NOT NULL COMMENT '工号/学号',
  `real_name` varchar(50) NOT NULL COMMENT '姓名',
  `user_group` enum('教师','学生','管理员') NOT NULL DEFAULT '学生' COMMENT '用户组',
  `department` varchar(100) DEFAULT NULL COMMENT '院系',
  `password` varchar(100) NOT NULL COMMENT '密码',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_no` (`user_no`) COMMENT '工号学号唯一'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户信息表';


### 3. 修改数据库连接信息
打开项目中的 `src/main/java/UserDao.java` 文件，找到以下代码：

private static final String URL =
        "jdbc:mysql://localhost:3366/sgs?user=root&password=123&useUnicode=true&characterEncoding=UTF-8";

> ⚠️ **注意**：请根据你本机的实际情况修改。默认端口是 `3306`，如果你的 MySQL 端口是默认的，请把 `3366` 改成 `3306`；同时把 `user=root` 和 `password=123` 改为你本地数据库的账号密码。

### 4. 在 IDEA 中运行
1. 使用 IntelliJ IDEA 打开本项目文件夹。
2. 等待 Maven 自动下载依赖。
3. 配置 Tomcat：
   - 点击右上角 `Edit Configurations` -> `+` -> `Tomcat Server` -> `Local`。
   - 在 `Deployment` 标签页，点击 `+` -> `Artifact`，选择 `StudentGradeSystem:war exploded`。
   - 将 `Application context` 设置为 `/StudentGradeSystem`。
4. 点击运行按钮启动服务器。
5. 在浏览器中访问：`http://localhost:8080/StudentGradeSystem/user_list.html`

## 📂 项目结构

StudentGradeSystem/
├── src/main/java/
│   ├── User.java        # 实体类
│   ├── UserDao.java     # 数据库操作封装
│   ├── add.java         # 处理添加请求
│   ├── delete.java      # 处理删除请求
│   ├── modify.java      # 处理修改请求
│   └── query.java       # 处理查询请求
├── web/
│   ├── index.jsp   
│   ├── user_list.html   # 用户列表（前端页面）
│   ├── user_add.html    # 添加用户（前端页面）
│   └── user_modify.html # 修改用户（前端页面）
└── pom.xml              # Maven 依赖配置
