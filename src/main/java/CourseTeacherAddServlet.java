package org.example;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import org.json.JSONObject;
import java.io.IOException;

@WebServlet("/course_teacher_add")
public class CourseTeacherAddServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String courseId = request.getParameter("course_id");
        String teacherNo = request.getParameter("teacher_no");

        CourseDao dao = new CourseDao();
        String resultMsg = dao.addTeacherToCourse(courseId, teacherNo);

        JSONObject json = new JSONObject();
        if("OK".equals(resultMsg)) {
            json.put("result_code", 0);
            json.put("result_msg", "添加成功");
        } else {
            json.put("result_code", 1);
            json.put("result_msg", resultMsg);
        }
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().print(json);
    }
}