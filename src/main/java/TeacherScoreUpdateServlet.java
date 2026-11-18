package org.example;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.json.JSONObject;

import java.io.IOException;

@WebServlet("/teacher_score_update")
public class TeacherScoreUpdateServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession(false);
        String teacherNo = (session == null) ? null : (String) session.getAttribute("currentUser");

        JSONObject json = new JSONObject();
        if (teacherNo == null) {
            json.put("result_code", 1);
            json.put("result_msg", "未登录");
        } else {
            String relationId = request.getParameter("relation_id");
            String score = request.getParameter("score");
            String err = new TeacherDao().updateScoreByTeacher(relationId, teacherNo, score);
            json.put("result_code", err == null ? 0 : 1);
            json.put("result_msg", err == null ? "修改成功" : err);
        }
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().print(json);
    }
}