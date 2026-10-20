package org.example;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/changePassword")
public class ChangePasswordServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String userNo = request.getParameter("userNo");
        String oldPwd = request.getParameter("oldPassword");
        String newPwd = request.getParameter("newPassword");
        String loginType = request.getParameter("loginType");

        UserDao dao = new UserDao();
        boolean success = dao.updatePassword(userNo, oldPwd, newPwd);

        request.setAttribute("msg", success ? "密码修改成功，请重新登录！" : "修改失败：原密码错误或账号不存在！");
        request.setAttribute("msgType", success ? "success" : "danger");

        // 修改完后退回对应的登录页
        String jsp = "teacher".equals(loginType) ? "/teacher_login.jsp" : "/student_login.jsp";
        request.getRequestDispatcher(jsp).forward(request, response);
    }
}