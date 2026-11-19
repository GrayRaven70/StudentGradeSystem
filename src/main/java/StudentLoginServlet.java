package org.example;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

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
        String loginType = request.getParameter("loginType"); // "teacher" 或 "student"

        UserDao dao = new UserDao();
        Map<String, String> user = dao.login(userNo, password);

        if (user != null) {
            // ===== 登录成功：写入 Session =====
            HttpSession session = request.getSession();
            session.setAttribute("currentUser", user.get("user_no"));
            session.setAttribute("role", user.get("user_group"));
            session.setAttribute("realName", user.get("real_name"));

            // ===== 按角色跳转 =====
            String role = user.get("user_group");
            String target;
            if ("教师".equals(role)) {
                target = "/teacher_dashboard.html";   // 教师进教师主页面
            } else if ("学生".equals(role)) {
                target = "/student_dashboard.html";                 // 学生进学生主页面
            } else {
                target = "/index.jsp";                // 其他情况兜底
            }
            response.sendRedirect(request.getContextPath() + target);

        } else {
            // ===== 登录失败：带着错误信息退回对应登录页 =====
            request.setAttribute("error", "账号或密码错误！");
            String jsp = "teacher".equals(loginType) ? "/teacher_login.jsp" : "/student_login.jsp";
            request.getRequestDispatcher(jsp).forward(request, response);
        }
    }
}