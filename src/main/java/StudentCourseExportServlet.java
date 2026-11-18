package org.example;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

@WebServlet("/student_course_export")
public class StudentCourseExportServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        String studentId = request.getParameter("student_id");
        String studentNo = request.getParameter("student_no");
        String fileName = (studentNo == null || studentNo.trim().isEmpty()) ? studentId : studentNo;

        List<Map<String, String>> list = new ClassDao().queryCoursesOfStudent(studentId);

        response.setContentType("text/csv;charset=UTF-8");
        response.setHeader("Content-Disposition",
                "attachment; filename=\"student_courses_" + fileName + ".csv\"");

        PrintWriter out = response.getWriter();
        out.write('\ufeff');
        out.println("课程名称,课程号,课序号,成绩,是否及格");
        for (Map<String, String> m : list) {
            String score = "null".equals(m.get("score")) ? "未录入" : m.get("score");
            out.println(m.get("course_name") + ","
                    + m.get("course_no") + ","
                    + m.get("course_seq_no") + ","
                    + score + ","
                    + m.get("pass"));
        }
        out.flush();
        out.close();
    }
}