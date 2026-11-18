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

@WebServlet("/teacher_course_student_query")
public class TeacherCourseStudentQueryServlet extends HttpServlet {
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
            String courseId = request.getParameter("course_id");
            String keyword = request.getParameter("keyword");
            List<Map<String, String>> list =
                    new TeacherDao().queryStudentsByCourseAndTeacher(courseId, teacherNo, keyword);
            if (list == null) {
                json.put("result_code", 1);
                json.put("result_msg", "无权访问该课程");
            } else {
                JSONArray arr = new JSONArray();
                for (Map<String, String> m : list) {
                    JSONObject o = new JSONObject();
                    o.put("relation_id", m.get("relation_id"));
                    o.put("user_no", m.get("user_no"));
                    o.put("real_name", m.get("real_name"));
                    o.put("score", m.get("score"));
                    arr.put(o);
                }
                json.put("result_code", 0);
                json.put("aaData", arr);
            }
        }
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().print(json);
    }
}