package org.example;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@WebServlet("/class_student_query")
public class ClassStudentQueryServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        String classId = request.getParameter("class_id");
        String keyword = request.getParameter("keyword");

        List<Map<String, String>> list = new ClassDao().queryStudentsOfClass(classId, keyword);

        JSONArray array = new JSONArray();
        for (Map<String, String> m : list) {
            JSONObject obj = new JSONObject();
            obj.put("id", m.get("id"));
            obj.put("user_no", m.get("user_no"));
            obj.put("real_name", m.get("real_name"));
            array.put(obj);
        }

        JSONObject json = new JSONObject();
        json.put("result_code", 0);
        json.put("aaData", array);

        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().print(json);
    }
}