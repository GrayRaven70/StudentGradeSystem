<%--
  Created by IntelliJ IDEA.
  User: 32770
  Date: 2026/10/19
  Time: 18:25
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%
    // 获取当前项目根路径（例如 /CRUD_zuoye2_war_exploded）
    String ctx = request.getContextPath();
    // 重定向到目标 HTML 页面
    response.sendRedirect(ctx + "/user_list.html");
%>
