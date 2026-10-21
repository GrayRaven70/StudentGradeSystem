package org.example;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import org.json.JSONObject;
import java.io.IOException;

@WebServlet("/course_student_update_score")
public class CourseStudentUpdateScoreServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String relationId = request.getParameter("relation_id");
        String score = request.getParameter("score");

        boolean success = new CourseDao().updateScore(relationId, score);

        JSONObject json = new JSONObject();
        json.put("result_code", success ? 0 : 1);
        json.put("result_msg", success ? "成绩更新成功" : "更新失败");
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().print(json);
    }
}