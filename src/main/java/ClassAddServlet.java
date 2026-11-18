package org.example;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONObject;

import java.io.IOException;

@WebServlet("/class_add")
public class ClassAddServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        String classNo = request.getParameter("class_no");
        String className = request.getParameter("class_name");
        String teacherNo = request.getParameter("head_teacher_no");

        // 返回 null 表示成功，否则是错误信息
        String err = new ClassDao().addClass(classNo, className, teacherNo);

        JSONObject json = new JSONObject();
        json.put("result_code", err == null ? 0 : 1);
        json.put("result_msg", err == null ? "添加成功" : err);

        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().print(json);
    }
}