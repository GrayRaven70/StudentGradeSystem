import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/user_add")
public class add extends HttpServlet {
    public void service(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        request.setCharacterEncoding("UTF-8");

        // 1. 获取前端传过来的 5 个参数（id 是自增的，不需要获取）
        String userNo = request.getParameter("user_no");
        String realName = request.getParameter("real_name");
        String userGroup = request.getParameter("user_group");
        String department = request.getParameter("department");
        String password = request.getParameter("password");

        System.out.println("[add] 收到工号/学号：" + userNo);
        System.out.println("[add] 收到姓名：" + realName);
        System.out.println("[add] 收到用户组：" + userGroup);
        System.out.println("[add] 收到院系：" + department);
        System.out.println("[add] 收到密码：" + password);

        // 2. 调用 DAO 把数据存入数据库
        UserDao dao = new UserDao();
        dao.add(userNo, realName, userGroup, department, password);

        // 3. 添加成功后，跳转回列表页（必须加 getContextPath 防止 404）
        response.sendRedirect(request.getContextPath() + "/user_list.html");
    }
}