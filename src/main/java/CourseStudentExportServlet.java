package org.example;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

@WebServlet("/course_student_export")
public class CourseStudentExportServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String courseId = request.getParameter("course_id");

        List<Map<String, String>> list = new CourseDao().queryStudentsByCourseId(courseId);

        // 1. 设置响应头，告诉浏览器这是一个下载文件，文件名叫 成绩单.csv
        response.setContentType("text/csv;charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=\"score_export_" + courseId + ".csv\"");

        PrintWriter out = response.getWriter();

        // 2. 写入 BOM 头（解决 Excel 打开 CSV 时中文乱码的问题）
        out.write('\ufeff');

        // 3. 写入表头
        out.println("学号,姓名,成绩");

        // 4. 循环写入数据
        for (Map<String, String> map : list) {
            String score = map.get("score");
            if ("null".equals(score)) score = "未录入";
            out.println(map.get("user_no") + "," + map.get("real_name") + "," + score);
        }

        out.flush();
        out.close();
    }
}