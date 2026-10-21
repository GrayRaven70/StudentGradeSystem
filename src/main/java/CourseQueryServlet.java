package org.example;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

@WebServlet("/course_query")
public class CourseQueryServlet extends HttpServlet {
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String id = request.getParameter("id");

        CourseDao dao = new CourseDao();
        JSONObject resultJson = new JSONObject();

        try {
            if (id != null && !id.trim().isEmpty()) {
                // 查单条
                Course c = dao.queryCourseById(id);
                if (c != null) {
                    JSONObject obj = new JSONObject();
                    obj.put("id", c.getId());
                    obj.put("course_no", c.getCourseNo());
                    obj.put("course_seq_no", c.getCourseSeqNo());
                    obj.put("course_name", c.getCourseName());
                    resultJson.put("data", obj);
                    resultJson.put("result_code", 0);
                } else {
                    resultJson.put("result_code", 1);
                    resultJson.put("result_msg", "找不到该课程");
                }
            } else {
                // 查全部
                List<Course> list = dao.queryAllCourses();
                JSONArray array = new JSONArray();
                for (Course c : list) {
                    JSONObject obj = new JSONObject();
                    obj.put("id", c.getId());
                    obj.put("course_no", c.getCourseNo());
                    obj.put("course_seq_no", c.getCourseSeqNo());
                    obj.put("course_name", c.getCourseName());
                    obj.put("teachers", c.getTeachers());
                    array.put(obj);
                }
                resultJson.put("aaData", array);
                resultJson.put("result_code", 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
            resultJson.put("result_code", 1);
            resultJson.put("result_msg", e.getMessage());
        }

        response.setContentType("application/json; charset=UTF-8");
        response.getWriter().print(resultJson);
    }
}