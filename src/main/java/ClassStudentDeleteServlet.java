package org.example;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONObject;

import java.io.IOException;

@WebServlet("/class_student_delete")
public class ClassStudentDeleteServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        String classId = request.getParameter("class_id");
        String studentId = request.getParameter("student_id");

        boolean success = new ClassDao().removeStudentFromClass(classId, studentId);

        JSONObject json = new JSONObject();
        json.put("result_code", success ? 0 : 1);
        json.put("result_msg", success ? "移出成功" : "移出失败");

        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().print(json);
    }
}