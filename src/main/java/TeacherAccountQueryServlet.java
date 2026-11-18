package org.example;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Map;

@WebServlet("/teacher_account_query")
public class TeacherAccountQueryServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession(false);
        String teacherNo = (session == null) ? null : (String) session.getAttribute("currentUser");

        JSONObject json = new JSONObject();
        if (teacherNo == null) {
            json.put("result_code", 1);
            json.put("result_msg", "未登录");
        } else {
            Map<String, String> m = new TeacherDao().queryTeacherByNo(teacherNo);
            if (m == null) {
                json.put("result_code", 1);
                json.put("result_msg", "查不到教师信息");
            } else {
                JSONObject data = new JSONObject();
                data.put("user_no", m.get("user_no"));
                data.put("real_name", m.get("real_name"));
                json.put("data", data);
                json.put("result_code", 0);
            }
        }
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().print(json);
    }
}