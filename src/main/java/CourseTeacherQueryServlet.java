package org.example;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@WebServlet("/course_teacher_query")
public class CourseTeacherQueryServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String courseId = request.getParameter("course_id");

        CourseDao dao = new CourseDao();
        List<Map<String, String>> list = dao.queryTeachersByCourseId(courseId);

        JSONArray array = new JSONArray();
        for(Map<String, String> map : list) {
            JSONObject obj = new JSONObject();
            obj.put("relation_id", map.get("relation_id"));
            obj.put("real_name", map.get("real_name"));
            obj.put("user_no", map.get("user_no"));
            array.put(obj);
        }

        JSONObject json = new JSONObject();
        json.put("result_code", 0);
        json.put("aaData", array);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().print(json);
    }
}