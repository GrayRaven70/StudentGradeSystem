package org.example;
import jakarta.servlet.*; import jakarta.servlet.annotation.*; import jakarta.servlet.http.*;
import org.json.*; import java.io.IOException; import java.util.List;

@WebServlet("/class_query")
public class ClassQueryServlet extends HttpServlet {
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String id = req.getParameter("id");
        String keyword = req.getParameter("keyword");
        ClassDao dao = new ClassDao();
        JSONObject json = new JSONObject();
        if (id != null && !id.trim().isEmpty()) {
            Clazz c = dao.queryById(id);
            if (c == null) { json.put("result_code", 1); json.put("result_msg", "找不到该班级"); }
            else {
                JSONObject o = new JSONObject();
                o.put("id", c.getId()); o.put("class_no", c.getClassNo());
                o.put("class_name", c.getClassName());
                o.put("head_teacher_no", c.getHeadTeacherNo() == null ? "" : c.getHeadTeacherNo());
                o.put("head_teacher_name", c.getHeadTeacherName() == null ? "" : c.getHeadTeacherName());
                json.put("data", o); json.put("result_code", 0);
            }
        } else {
            List<Clazz> list = dao.queryClasses(keyword);
            JSONArray arr = new JSONArray();
            for (Clazz c : list) {
                JSONObject o = new JSONObject();
                o.put("id", c.getId()); o.put("class_no", c.getClassNo());
                o.put("class_name", c.getClassName());
                o.put("head_teacher_no", c.getHeadTeacherNo() == null ? "暂无" : c.getHeadTeacherNo());
                o.put("head_teacher_name", c.getHeadTeacherName() == null ? "暂无" : c.getHeadTeacherName());
                arr.put(o);
            }
            json.put("aaData", arr); json.put("result_code", 0);
        }
        resp.setContentType("application/json;charset=UTF-8");
        resp.getWriter().print(json);
    }
}