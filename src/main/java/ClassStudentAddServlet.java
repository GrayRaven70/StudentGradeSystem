package org.example;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONObject;

import java.io.IOException;

@WebServlet("/class_student_add")
public class ClassStudentAddServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        String classId = request.getParameter("class_id");
        String studentNo = request.getParameter("student_no");

        String resultMsg = new ClassDao().addStudentToClass(classId, studentNo);

        JSONObject json = new JSONObject();
        json.put("result_code", "OK".equals(resultMsg) ? 0 : 1);
        json.put("result_msg", "OK".equals(resultMsg) ? "添加成功" : resultMsg);

        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().print(json);
    }
}