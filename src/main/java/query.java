package org.example;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Map;

// ★ 1. 修改映射路径
@WebServlet("/user_query")
public class query extends HttpServlet {

    public void service(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        request.setCharacterEncoding("UTF-8");

        String id = request.getParameter("id");
        System.out.println("[query] 收到 id：" + id);

        // ★ 2. 调用新的 UserDao
        UserDao dao = new UserDao();
        List<Map<String, String>> list;

        // 判断 id 是否有值且不为 "undefined"（防止前端传空或未定义）
        if (id != null && id.trim().length() > 0 && !"undefined".equals(id)) {
            list = dao.queryById(id); // 按 id 查单条（用于修改页回显）
        } else {
            list = dao.queryAll();    // 查全部（用于列表页）
        }

        // 组装返回的 JSON 数据
        JSONObject resultJson = new JSONObject();
        try {
            resultJson.put("aaData", list);
            resultJson.put("result_code", 0);
            resultJson.put("result_msg", "ok");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // 输出给前端
        response.setContentType("application/json; charset=UTF-8");
        response.getWriter().print(resultJson);
        response.getWriter().flush();
    }
}