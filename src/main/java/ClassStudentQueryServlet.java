package org.example;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
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

        HttpSession session = request.getSession(false);
        String role = (session == null) ? null : (String) session.getAttribute("role");
        String currentUser = (session == null) ? null : (String) session.getAttribute("currentUser");

        String classId = request.getParameter("class_id");
        String keyword = request.getParameter("keyword");

        JSONObject json = new JSONObject();

        // 权限：管理员全部放行；教师必须是该班班主任
        boolean allowed = "管理员".equals(role)
                || ("教师".equals(role) && currentUser != null
                && new ClassDao().isHeadTeacherOfClass(classId, currentUser));
        if (!allowed) {
            json.put("result_code", 1);
            json.put("result_msg", "无权查看该班级");
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().print(json);
            return;
        }

        List<Map<String, String>> list = new ClassDao().queryStudentsOfClass(classId, keyword);
        JSONArray arr = new JSONArray();
        for (Map<String, String> m : list) {
            JSONObject o = new JSONObject();
            o.put("id", m.get("id"));
            o.put("user_no", m.get("user_no"));
            o.put("real_name", m.get("real_name"));
            arr.put(o);
        }
        json.put("result_code", 0);
        json.put("aaData", arr);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().print(json);
    }
}