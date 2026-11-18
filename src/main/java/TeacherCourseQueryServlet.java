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

@WebServlet("/teacher_course_query")
public class TeacherCourseQueryServlet extends HttpServlet {
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
            String keyword = request.getParameter("keyword");
            List<Map<String, String>> list = new TeacherDao().queryCoursesByTeacherNo(teacherNo, keyword);
            JSONArray arr = new JSONArray();
            for (Map<String, String> m : list) {
                JSONObject o = new JSONObject();
                o.put("id", m.get("id"));
                o.put("course_no", m.get("course_no"));
                o.put("course_seq_no", m.get("course_seq_no"));
                o.put("course_name", m.get("course_name"));
                o.put("avg_score", m.get("avg_score"));
                arr.put(o);
            }
            json.put("result_code", 0);
            json.put("aaData", arr);
        }
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().print(json);
    }
}