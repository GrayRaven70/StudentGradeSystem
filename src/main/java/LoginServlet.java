package org.example;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        String username = request.getParameter("username");
        String password = request.getParameter("password");

        // 固定管理员账号密码验证
        if ("admin14".equals(username) && "123".equals(password)) {
            // 验证成功，把用户名存入 Session（作为已登录的凭证）
            HttpSession session = request.getSession();
            session.setAttribute("currentUser", username);
            session.setAttribute("role", "admin");

            // 跳转到列表页
            response.sendRedirect(request.getContextPath() + "/user_list.html");
        } else {
            // 验证失败，带着错误信息退回登录页
            request.setAttribute("error", "账号或密码错误！");
            request.getRequestDispatcher("/admin_login.jsp").forward(request, response);
        }
    }
}