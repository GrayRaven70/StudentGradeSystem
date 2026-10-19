import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

// ★ 1. 修改映射路径
@WebServlet("/user_modify")
public class modify extends HttpServlet {

    public void service(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        request.setCharacterEncoding("UTF-8");

        // ★ 2. 接收所有 sys_user 表需要的参数
        String id = request.getParameter("id");
        String userNo = request.getParameter("user_no");
        String realName = request.getParameter("real_name");
        String userGroup = request.getParameter("user_group");
        String department = request.getParameter("department");
        String password = request.getParameter("password");

        System.out.println("[modify] 收到 id：" + id);
        System.out.println("[modify] 收到 工号/学号：" + userNo);
        System.out.println("[modify] 收到 姓名：" + realName);

        JSONObject resultJson = new JSONObject();

        try {
            if (id != null && id.trim().length() > 0) {
                // ★ 3. 调用新的 UserDao
                UserDao dao = new UserDao();
                dao.update(id, userNo, realName, userGroup, department, password);

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
                resultJson.put("result_msg", "修改失败：" + e.getMessage());
            } catch (JSONException ex) {
                ex.printStackTrace();
            }
        }

        response.setContentType("application/json; charset=UTF-8");
        response.getWriter().print(resultJson);
        response.getWriter().flush();
    }
}