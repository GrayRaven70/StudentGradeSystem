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

@WebServlet("/class_student_export")
public class ClassStudentExportServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        String classId = request.getParameter("class_id");
        List<Map<String, String>> list = new ClassDao().queryStudentsOfClass(classId, null);

        response.setContentType("text/csv;charset=UTF-8");
        response.setHeader("Content-Disposition",
                "attachment; filename=\"class_students_" + classId + ".csv\"");

        PrintWriter out = response.getWriter();
        out.write('\ufeff'); // BOM，防 Excel 中文乱码
        out.println("学号,姓名");
        for (Map<String, String> m : list) {
            out.println(m.get("user_no") + "," + m.get("real_name"));
        }
        out.flush();
        out.close();
    }
}