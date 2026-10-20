package org.example;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

// ★ 1. 修改 URL 映射路径
@WebServlet("/user_delete")
public class delete extends HttpServlet {

    public void service(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        request.setCharacterEncoding("UTF-8");

        // 删除只需要 id 即可
        String id = request.getParameter("id");
        System.out.println("[delete] 准备删除 id：" + id);

        JSONObject resultJson = new JSONObject();

        try {
            if (id != null && id.trim().length() > 0) {
                // ★ 2. 调用新写的 UserDao
                UserDao dao = new UserDao();
                dao.delete(id);

                resultJson.put("result_code", 0);
                resultJson.put("result_msg", "ok");
            } else {
                resultJson.put("result_code", 1);
                resultJson.put("result_msg", "ID不能为空");
            }
        } catch (Exception e) {
            e.printStackTrace();
            try {
                resultJson.put("result_code", 1);
                resultJson.put("result_msg", "删除失败：" + e.getMessage());
            } catch (JSONException ex) {
                ex.printStackTrace();
            }
        }

        response.setContentType("application/json; charset=UTF-8");
        response.getWriter().print(resultJson);
    }
}