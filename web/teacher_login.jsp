<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
  <meta charset="UTF-8">
  <title>教师登录</title>
  <style>
    body { display: flex; justify-content: center; align-items: center; height: 100vh; background-color: #f4f7f6; font-family: sans-serif; margin: 0; }
    .login-box { background: white; padding: 30px 40px; border-radius: 8px; box-shadow: 0 4px 10px rgba(0,0,0,0.1); width: 300px; text-align: center; }
    .input-group { margin-bottom: 15px; text-align: left; }
    .input-group input { width: 100%; padding: 8px; box-sizing: border-box; border: 1px solid #ccc; border-radius: 4px; }
    .btn-blue { width: 100%; padding: 10px; background-color: #007bff; color: white; border: none; border-radius: 4px; cursor: pointer; margin-bottom: 10px; }
    .btn-blue:hover { background-color: #0069d9; }
    .link-btn { font-size: 14px; color: #007bff; cursor: pointer; text-decoration: underline; }
    .msg { font-size: 14px; margin-bottom: 10px; }
  </style>
</head>
<body>
<div class="login-box">
  <h2>教师登录</h2>

  <!-- 显示错误或成功信息 -->
  <% String msg = (String) request.getAttribute("msg"); String error = (String) request.getAttribute("error"); %>
  <% if (error != null) { %><p class="msg" style="color: red;"><%= error %></p><% } %>
  <% if (msg != null) { %><p class="msg" style="color: <%= "success".equals(request.getAttribute("msgType")) ? "green" : "red" %>;"><%= msg %></p><% } %>

  <!-- 登录表单 -->
  <div id="loginForm">
    <form action="studentLogin" method="post">
      <input type="hidden" name="loginType" value="teacher">
      <div class="input-group">
        <label>工号</label>
        <input type="text" name="userNo" required>
      </div>
      <div class="input-group">
        <label>密码</label>
        <input type="password" name="password" required>
      </div>
      <button type="submit" class="btn-blue">登 录</button>
    </form>
    <span class="link-btn" onclick="toggleForm()">修改密码？</span>
  </div>

  <!-- 修改密码表单 (默认隐藏) -->
  <div id="pwdForm" style="display: none;">
    <form action="changePassword" method="post">
      <input type="hidden" name="loginType" value="teacher">
      <div class="input-group">
        <label>工号</label>
        <input type="text" name="userNo" required>
      </div>
      <div class="input-group">
        <label>原密码</label>
        <input type="password" name="oldPassword" required>
      </div>
      <div class="input-group">
        <label>新密码</label>
        <input type="password" name="newPassword" required>
      </div>
      <button type="submit" class="btn-blue">确认修改</button>
    </form>
    <span class="link-btn" onclick="toggleForm()">返回登录</span>
  </div>

</div>

<script>
  function toggleForm() {
    var login = document.getElementById("loginForm");
    var pwd = document.getElementById("pwdForm");
    if (login.style.display === "none") {
      login.style.display = "block";
      pwd.style.display = "none";
    } else {
      login.style.display = "none";
      pwd.style.display = "block";
    }
  }
</script>
</body>
</html>