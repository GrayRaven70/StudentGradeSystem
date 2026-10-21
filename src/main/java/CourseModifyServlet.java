package org.example;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import org.json.JSONObject;
import java.io.IOException;

@WebServlet("/course_modify")
public class CourseModifyServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String id = request.getParameter("id");
        String courseNo = request.getParameter("course_no");
        String seqNo = request.getParameter("course_seq_no");
        String courseName = request.getParameter("course_name");

        CourseDao dao = new CourseDao();
        boolean success = dao.updateCourse(id, courseNo, seqNo, courseName);

        JSONObject json = new JSONObject();
        json.put("result_code", success ? 0 : 1);
        json.put("result_msg", success ? "修改成功" : "修改失败");

        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().print(json);
    }
}