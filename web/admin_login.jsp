<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <title>管理员登录</title>
    <style>
        body { display: flex; justify-content: center; align-items: center; height: 100vh; background-color: #f4f7f6; font-family: sans-serif; margin: 0; }
        .login-box { background: white; padding: 30px 40px; border-radius: 8px; box-shadow: 0 4px 10px rgba(0,0,0,0.1); width: 300px; text-align: center; }
        .input-group { margin-bottom: 15px; text-align: left; }
        .input-group input { width: 100%; padding: 8px; box-sizing: border-box; border: 1px solid #ccc; border-radius: 4px; }
        .btn-login { width: 100%; padding: 10px; background-color: #dc3545; color: white; border: none; border-radius: 4px; cursor: pointer; }
    </style>
</head>
<body>
<div class="login-box">
    <h2>管理员登录</h2>

    <!-- 显示错误信息 -->
    <% String error = (String) request.getAttribute("error"); %>
    <% if (error != null) { %>
    <p style="color: red; font-size: 14px;"><%= error %></p>
    <% } %>

    <!-- 提交给 LoginServlet -->
    <form action="login" method="post">
        <div class="input-group">
            <label>账号</label>
            <input type="text" name="username" required>
        </div>
        <div class="input-group">
            <label>密码</label>
            <input type="password" name="password" required>
        </div>
        <button type="submit" class="btn-login">登 录</button>
    </form>
</div>
</body>
</html>