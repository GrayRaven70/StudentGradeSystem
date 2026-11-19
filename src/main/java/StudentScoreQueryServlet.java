package org.example;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@WebServlet("/student_score_query")
public class StudentScoreQueryServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession(false);
        String studentNo = (session == null) ? null : (String) session.getAttribute("currentUser");
        String role = (session == null) ? null : (String) session.getAttribute("role");

        JSONObject json = new JSONObject();
        if (studentNo == null || !"学生".equals(role)) {
            json.put("result_code", 1);
            json.put("result_msg", "未登录或非学生身份");
        } else {
            String keyword = request.getParameter("keyword");
            List<Map<String, String>> list = new StudentDao().queryMyScores(studentNo, keyword);
            JSONArray arr = new JSONArray();
            for (Map<String, String> m : list) {
                JSONObject o = new JSONObject();
                o.put("course_name", m.get("course_name"));
                o.put("course_no", m.get("course_no"));
                o.put("course_seq_no", m.get("course_seq_no"));
                o.put("score", m.get("score"));
                o.put("pass", m.get("pass"));
                arr.put(o);
            }
            json.put("result_code", 0);
            json.put("aaData", arr);
        }
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().print(json);
    }
}