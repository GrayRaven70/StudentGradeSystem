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

@WebServlet("/teacher_course_export")
public class TeacherCourseExportServlet extends HttpServlet {
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

        String keyword = request.getParameter("keyword");
        List<Map<String, String>> list = new TeacherDao().queryCoursesByTeacherNo(teacherNo, keyword);

        response.setContentType("text/csv;charset=UTF-8");
        response.setHeader("Content-Disposition",
                "attachment; filename=\"teacher_courses_" + teacherNo + ".csv\"");
        PrintWriter out = response.getWriter();
        out.write('\ufeff');
        out.println("课程号,课序号,课程名,平均分");
        for (Map<String, String> m : list) {
            out.println(m.get("course_no") + "," + m.get("course_seq_no") + ","
                    + m.get("course_name") + "," + m.get("avg_score"));
        }
        out.flush();
        out.close();
    }
}