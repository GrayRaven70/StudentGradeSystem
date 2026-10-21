package org.example;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import org.json.JSONObject;
import java.io.IOException;

@WebServlet("/course_student_delete")
public class CourseStudentDeleteServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String relationId = request.getParameter("relation_id");
        boolean success = new CourseDao().removeStudent(relationId);

        JSONObject json = new JSONObject();
        json.put("result_code", success ? 0 : 1);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().print(json);
    }
}