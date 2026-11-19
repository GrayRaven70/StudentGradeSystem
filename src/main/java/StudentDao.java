package org.example;

import java.sql.*;
import java.util.*;

public class StudentDao {

    private static final String URL =
            "jdbc:mysql://localhost:3366/sgs?user=root&password=123&useUnicode=true&characterEncoding=UTF-8";

    static {
        try { Class.forName("com.mysql.cj.jdbc.Driver"); }
        catch (ClassNotFoundException e) { e.printStackTrace(); }
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    // ========== 1. 查学生账户（学号+姓名）==========
    public Map<String, String> queryStudentByNo(String studentNo) {
        String sql = "SELECT user_no, real_name FROM sys_user WHERE user_no=? AND user_group='学生'";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, studentNo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Map<String, String> m = new HashMap<>();
                    m.put("user_no", rs.getString("user_no"));
                    m.put("real_name", rs.getString("real_name"));
                    return m;
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    // ========== 2. 我的成绩（课程名/号/序号/成绩/是否及格，支持模糊搜索）==========
    // 返回每行：course_name, course_no, course_seq_no, score, pass
    public List<Map<String, String>> queryMyScores(String studentNo, String keyword) {
        List<Map<String, String>> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT c.course_name, c.course_no, c.course_seq_no, cs.score " +
                        "FROM course_student cs " +
                        "JOIN sys_user u ON cs.student_id = u.id " +
                        "JOIN course c ON cs.course_id = c.id " +
                        "WHERE u.user_no = ? ");
        boolean hasKw = keyword != null && !keyword.trim().isEmpty();
        if (hasKw) {
            sql.append("AND (c.course_name LIKE ? OR c.course_no LIKE ? OR c.course_seq_no LIKE ?) ");
        }
        sql.append("ORDER BY c.course_no, c.course_seq_no");
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            ps.setString(1, studentNo);
            if (hasKw) {
                String like = "%" + keyword.trim() + "%";
                ps.setString(2, like);
                ps.setString(3, like);
                ps.setString(4, like);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(scoreRow(rs));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    // ========== 3. 我的课程（课程名/号/序号/授课教师姓名/成绩/是否及格）==========
    // 教师姓名用 GROUP_CONCAT 拼接；注意 GROUP BY 要包含所有非聚合列
    public List<Map<String, String>> queryMyCourses(String studentNo, String keyword) {
        List<Map<String, String>> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT c.course_name, c.course_no, c.course_seq_no, " +
                        "GROUP_CONCAT(t.real_name SEPARATOR ', ') AS teachers, cs.score " +
                        "FROM course_student cs " +
                        "JOIN sys_user u ON cs.student_id = u.id " +
                        "JOIN course c ON cs.course_id = c.id " +
                        "LEFT JOIN course_teacher ct ON c.id = ct.course_id " +
                        "LEFT JOIN sys_user t ON ct.teacher_id = t.id " +
                        "WHERE u.user_no = ? ");
        boolean hasKw = keyword != null && !keyword.trim().isEmpty();
        if (hasKw) {
            // 教师姓名是聚合列，HAVING 过滤
            sql.append("GROUP BY c.id, c.course_name, c.course_no, c.course_seq_no, cs.score ");
            sql.append("HAVING c.course_name LIKE ? OR c.course_no LIKE ? OR c.course_seq_no LIKE ? " +
                    "OR teachers LIKE ? ");
        } else {
            sql.append("GROUP BY c.id, c.course_name, c.course_no, c.course_seq_no, cs.score ");
        }
        sql.append("ORDER BY c.course_no, c.course_seq_no");
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            ps.setString(1, studentNo);
            if (hasKw) {
                String like = "%" + keyword.trim() + "%";
                ps.setString(2, like);
                ps.setString(3, like);
                ps.setString(4, like);
                ps.setString(5, like);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, String> m = scoreRow(rs);
                    String teachers = rs.getString("teachers");
                    m.put("teachers", teachers == null ? "暂无教师" : teachers);
                    list.add(m);
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    // 公共行映射：course_name, course_no, course_seq_no, score, pass
    private Map<String, String> scoreRow(ResultSet rs) throws SQLException {
        Map<String, String> m = new HashMap<>();
        m.put("course_name", rs.getString("course_name"));
        m.put("course_no", rs.getString("course_no"));
        m.put("course_seq_no", rs.getString("course_seq_no"));
        String score = rs.getString("score");
        m.put("score", score == null ? "null" : score);
        if (score == null) m.put("pass", "未录入");
        else m.put("pass", Double.parseDouble(score) >= 60 ? "及格" : "不及格");
        return m;
    }
}