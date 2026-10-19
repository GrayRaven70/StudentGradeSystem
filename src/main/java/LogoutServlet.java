package org.example;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // 销毁 Session
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        // 退回登录页
        response.sendRedirect(request.getContextPath() + "/admin_login.jsp");
    }
}