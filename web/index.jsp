<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%
    // 获取项目根路径，比如 /StudentGradeSystem
    String ctx = request.getContextPath();
%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <title>学生成绩管理系统 - 登录入口</title>
    <style>
        /* 简单的 CSS 样式，让页面居中且好看 */
        body {
            display: flex;
            justify-content: center;
            align-items: center;
            height: 100vh;
            background-color: #f4f7f6;
            font-family: "Microsoft YaHei", sans-serif;
            margin: 0;
        }
        .login-container {
            text-align: center;
            background: white;
            padding: 40px 50px;
            border-radius: 10px;
            box-shadow: 0 4px 15px rgba(0,0,0,0.1);
        }
        h1 {
            color: #333;
            margin-bottom: 30px;
            font-size: 24px;
        }
        .btn-group {
            display: flex;
            flex-direction: column;
            gap: 15px;
        }
        .btn {
            text-decoration: none;
            color: white;
            padding: 12px 40px;
            border-radius: 5px;
            font-size: 16px;
            transition: background-color 0.3s;
            width: 150px;
            display: inline-block;
        }
        /* 三种角色的不同颜色 */
        .btn-admin { background-color: #dc3545; }   /* 红色 */
        .btn-admin:hover { background-color: #c82333; }

        .btn-teacher { background-color: #007bff; } /* 蓝色 */
        .btn-teacher:hover { background-color: #0069d9; }

        .btn-student { background-color: #28a745; } /* 绿色 */
        .btn-student:hover { background-color: #218838; }
    </style>
</head>
<body>

<div class="login-container">
    <h1>学生成绩管理系统</h1>

    <div class="btn-group">
        <!-- 使用 ctx 拼接路径，点击跳转到对应的 HTML 页面 -->
        <a href="<%=ctx%>/admin_login.html" class="btn btn-admin">管理员登录</a>
        <a href="<%=ctx%>/teacher_login.html" class="btn btn-teacher">教师登录</a>
        <a href="<%=ctx%>/student_login.html" class="btn btn-student">学生登录</a>
    </div>
</div>

</body>
</html>