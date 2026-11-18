package org.example;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@WebServlet("/student_course_query")
public class StudentCourseQueryServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        String studentId = request.getParameter("student_id");
        List<Map<String, String>> list = new ClassDao().queryCoursesOfStudent(studentId);

        JSONArray array = new JSONArray();
        for (Map<String, String> m : list) {
            JSONObject obj = new JSONObject();
            obj.put("course_name", m.get("course_name"));
            obj.put("course_no", m.get("course_no"));
            obj.put("course_seq_no", m.get("course_seq_no"));
            obj.put("score", m.get("score"));   // 可能是 "null" 字符串
            obj.put("pass", m.get("pass"));     // 及格 / 不及格 / 未录入
            array.put(obj);
        }

        JSONObject json = new JSONObject();
        json.put("result_code", 0);
        json.put("aaData", array);

        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().print(json);
    }
}