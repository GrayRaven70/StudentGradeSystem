package org.example;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONObject;

import java.io.IOException;

@WebServlet("/class_modify")
public class ClassModifyServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        String id = request.getParameter("id");
        String classNo = request.getParameter("class_no");
        String className = request.getParameter("class_name");
        String teacherNo = request.getParameter("head_teacher_no");

        if (id == null || id.trim().isEmpty()) {
            JSONObject json = new JSONObject();
            json.put("result_code", 1);
            json.put("result_msg", "缺少班级ID");
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().print(json);
            return;
        }

        String err = new ClassDao().updateClass(id, classNo, className, teacherNo);

        JSONObject json = new JSONObject();
        json.put("result_code", err == null ? 0 : 1);
        json.put("result_msg", err == null ? "修改成功" : err);

        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().print(json);
    }
}