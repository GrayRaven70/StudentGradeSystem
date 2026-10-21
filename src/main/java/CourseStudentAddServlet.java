package org.example;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import org.json.JSONObject;
import java.io.IOException;

@WebServlet("/course_student_add")
public class CourseStudentAddServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String courseId = request.getParameter("course_id");
        String studentNo = request.getParameter("student_no");

        String resultMsg = new CourseDao().addStudentToCourse(courseId, studentNo);

        JSONObject json = new JSONObject();
        json.put("result_code", "OK".equals(resultMsg) ? 0 : 1);
        json.put("result_msg", "OK".equals(resultMsg) ? "添加成功" : resultMsg);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().print(json);
    }
}