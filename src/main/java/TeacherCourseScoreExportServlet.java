package org.example;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

@WebServlet("/teacher_course_score_export")
public class TeacherCourseScoreExportServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession(false);
        String teacherNo = (session == null) ? null : (String) session.getAttribute("currentUser");
        if (teacherNo == null) {
            response.sendRedirect(request.getContextPath() + "/index.jsp");
            return;
        }

        String courseId = request.getParameter("course_id");
        List<Map<String, String>> list =
                new TeacherDao().queryStudentsByCourseAndTeacher(courseId, teacherNo, null);
        if (list == null) {
            response.setContentType("text/html;charset=UTF-8");
            response.getWriter().print("无权访问该课程");
            return;
        }

        response.setContentType("text/csv;charset=UTF-8");
        response.setHeader("Content-Disposition",
                "attachment; filename=\"course_scores_" + courseId + ".csv\"");
        PrintWriter out = response.getWriter();
        out.write('\ufeff');
        out.println("学号,姓名,成绩");
        for (Map<String, String> m : list) {
            String score = "null".equals(m.get("score")) ? "未录入" : m.get("score");
            out.println(m.get("user_no") + "," + m.get("real_name") + "," + score);
        }
        out.flush();
        out.close();
    }
}