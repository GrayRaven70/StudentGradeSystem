package org.example;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import org.json.JSONObject;
import java.io.IOException;

@WebServlet("/course_delete")
public class CourseDeleteServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String id = request.getParameter("id");

        CourseDao dao = new CourseDao();
        boolean success = dao.deleteCourse(id);

        JSONObject json = new JSONObject();
        json.put("result_code", success ? 0 : 1);
        json.put("result_msg", success ? "删除成功" : "删除失败");

        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().print(json);
    }
}