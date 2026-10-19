import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserDao {

    // 数据库连接信息（保持你原来的端口和账号密码）
    private static final String URL =
            "jdbc:mysql://localhost:端口号/sgs?user=用户名&password=密码&useUnicode=true&characterEncoding=UTF-8";

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    // ========== 1. 添加用户 ==========
    public void add(String userNo, String realName, String userGroup, String department, String password) {
        String sql = "insert into sys_user(user_no, real_name, user_group, department, password) values(?,?,?,?,?)";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, userNo);
            ps.setString(2, realName);
            ps.setString(3, userGroup);
            ps.setString(4, department);
            ps.setString(5, password);
            ps.executeUpdate();
            System.out.println("[UserDao] 添加成功：" + userNo);
        } catch (SQLException e) {
            System.out.println("[UserDao] 添加失败：" + e.getMessage());
            e.printStackTrace();
        }
    }

    // ========== 2. 删除用户 ==========
    public void delete(String id) {
        String sql = "delete from sys_user where id=?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            ps.executeUpdate();
            System.out.println("[UserDao] 删除成功，id=" + id);
        } catch (SQLException e) {
            System.out.println("[UserDao] 删除失败：" + e.getMessage());
            e.printStackTrace();
        }
    }

    // ========== 3. 修改用户 ==========
    // 注意：一般修改信息时，如果不改密码，前端传过来的 password 可能是空或者原密码。这里为了简单，全部覆盖。
    public void update(String id, String userNo, String realName, String userGroup, String department, String password) {
        String sql = "update sys_user set user_no=?, real_name=?, user_group=?, department=?, password=? where id=?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, userNo);
            ps.setString(2, realName);
            ps.setString(3, userGroup);
            ps.setString(4, department);
            ps.setString(5, password);
            ps.setString(6, id);
            ps.executeUpdate();
            System.out.println("[UserDao] 修改成功，id=" + id);
        } catch (SQLException e) {
            System.out.println("[UserDao] 修改失败：" + e.getMessage());
            e.printStackTrace();
        }
    }

    // ========== 4. 查询所有用户 ==========
    public List<Map<String, String>> queryAll() {
        List<Map<String, String>> list = new ArrayList<>();
        // 注意：查询时千万不要把 password 查出来返回给前端（安全原因），除非有特定业务需求
        String sql = "select id, user_no, real_name, user_group, department from sys_user order by id desc";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(rowToMap(rs));
            }
        } catch (SQLException e) {
            System.out.println("[UserDao] 查询失败：" + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    // ========== 5. 按 id 查询单个用户（用于修改前回显） ==========
    public List<Map<String, String>> queryById(String id) {
        List<Map<String, String>> list = new ArrayList<>();
        // 这里把 password 查出来是可以的，为了修改的时候回显到输入框
        String sql = "select id, user_no, real_name, user_group, department, password from sys_user where id=?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(rowToMapWithPassword(rs));
                }
            }
        } catch (SQLException e) {
            System.out.println("[UserDao] 查询失败：" + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    // 工具方法：把结果集转为 Map（不含密码）
    private Map<String, String> rowToMap(ResultSet rs) throws SQLException {
        Map<String, String> map = new HashMap<>();
        map.put("id", rs.getString("id"));
        map.put("user_no", rs.getString("user_no"));
        map.put("real_name", rs.getString("real_name"));
        map.put("user_group", rs.getString("user_group"));
        map.put("department", rs.getString("department"));
        return map;
    }

    // 工具方法：把结果集转为 Map（包含密码，仅限修改页使用）
    private Map<String, String> rowToMapWithPassword(ResultSet rs) throws SQLException {
        Map<String, String> map = rowToMap(rs); // 先调用上面的，把基础信息放进去
        map.put("password", rs.getString("password")); // 再加上密码
        return map;
    }
}