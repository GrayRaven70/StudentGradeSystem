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

@WebServlet("/teacher_class_query")
public class TeacherClassQueryServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        // 从 Session 拿当前登录教师工号（StudentLoginServlet 登录成功时存入 currentUser）
        HttpSession session = request.getSession(false);
        String teacherNo = (session == null) ? null : (String) session.getAttribute("currentUser");

        JSONObject json = new JSONObject();

        if (teacherNo == null) {
            json.put("result_code", 1);
            json.put("result_msg", "未登录");
        } else {
            List<Clazz> list = new ClassDao().queryClassesByHeadTeacherNo(teacherNo);
            JSONArray array = new JSONArray();
            for (Clazz c : list) {
                JSONObject obj = new JSONObject();
                obj.put("id", c.getId());
                obj.put("class_no", c.getClassNo());
                obj.put("class_name", c.getClassName());
                obj.put("head_teacher_no", c.getHeadTeacherNo() == null ? "" : c.getHeadTeacherNo());
                obj.put("head_teacher_name", c.getHeadTeacherName() == null ? "" : c.getHeadTeacherName());
                array.put(obj);
            }
            json.put("result_code", 0);
            json.put("aaData", array);
        }

        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().print(json);
    }
}