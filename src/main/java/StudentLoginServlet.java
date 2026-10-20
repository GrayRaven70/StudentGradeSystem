package org.example;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.Map;

@WebServlet("/studentLogin")
public class StudentLoginServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String userNo = request.getParameter("userNo");
        String password = request.getParameter("password");
        String loginType = request.getParameter("loginType"); // 知道是从哪个页面来的

        UserDao dao = new UserDao();
        Map<String, String> user = dao.login(userNo, password);

        if (user != null) {
            // 登录成功，存入 Session
            HttpSession session = request.getSession();
            session.setAttribute("currentUser", user.get("user_no"));
            session.setAttribute("role", user.get("user_group"));
            session.setAttribute("realName", user.get("real_name"));

            // 目前没有其他页面，先弹个提示
            response.setContentType("text/html;charset=UTF-8");
            response.getWriter().println("<script>alert('登录成功！欢迎 " + user.get("real_name") + "');window.location.href='index.jsp';</script>");
        } else {
            // 登录失败
            request.setAttribute("error", "账号或密码错误！");
            String jsp = "teacher".equals(loginType) ? "/teacher_login.jsp" : "/student_login.jsp";
            request.getRequestDispatcher(jsp).forward(request, response);
        }
    }
}