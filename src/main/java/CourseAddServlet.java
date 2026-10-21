package org.example;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import org.json.JSONObject;
import java.io.IOException;

@WebServlet("/course_add")
public class CourseAddServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String courseNo = request.getParameter("course_no");
        String seqNo = request.getParameter("course_seq_no");
        String courseName = request.getParameter("course_name");

        CourseDao dao = new CourseDao();
        boolean success = dao.addCourse(courseNo, seqNo, courseName);

        JSONObject json = new JSONObject();
        json.put("result_code", success ? 0 : 1);
        json.put("result_msg", success ? "添加成功" : "添加失败");

        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().print(json);
    }
}