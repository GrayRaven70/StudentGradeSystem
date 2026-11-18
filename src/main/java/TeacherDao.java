package org.example;

import java.sql.*;
import java.util.*;

public class TeacherDao {

    private static final String URL =
            "jdbc:mysql://localhost:3366/sgs?user=root&password=123&useUnicode=true&characterEncoding=UTF-8";

    static {
        try { Class.forName("com.mysql.cj.jdbc.Driver"); }
        catch (ClassNotFoundException e) { e.printStackTrace(); }
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    // ========== 1. 查教师账户（工号+姓名）==========
    public Map<String, String> queryTeacherByNo(String teacherNo) {
        String sql = "SELECT user_no, real_name FROM sys_user WHERE user_no=? AND user_group='教师'";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, teacherNo);
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

    // ========== 2. 我任教的课程列表（含平均分，支持模糊搜索）==========
    public List<Map<String, String>> queryCoursesByTeacherNo(String teacherNo, String keyword) {
        List<Map<String, String>> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT c.id, c.course_no, c.course_seq_no, c.course_name, " +
                        "AVG(cs.score) AS avg_score " +
                        "FROM course c " +
                        "JOIN course_teacher ct ON c.id = ct.course_id " +
                        "JOIN sys_user u ON ct.teacher_id = u.id " +
                        "LEFT JOIN course_student cs ON c.id = cs.course_id " +
                        "WHERE u.user_no = ? ");
        boolean hasKw = keyword != null && !keyword.trim().isEmpty();
        if (hasKw) {
            sql.append("AND (c.course_no LIKE ? OR c.course_seq_no LIKE ? OR c.course_name LIKE ?) ");
        }
        sql.append("GROUP BY c.id, c.course_no, c.course_seq_no, c.course_name ");
        sql.append("ORDER BY c.course_no, c.course_seq_no");
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            ps.setString(1, teacherNo);
            if (hasKw) {
                String like = "%" + keyword.trim() + "%";
                ps.setString(2, like);
                ps.setString(3, like);
                ps.setString(4, like);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, String> m = new HashMap<>();
                    m.put("id", rs.getString("id"));
                    m.put("course_no", rs.getString("course_no"));
                    m.put("course_seq_no", rs.getString("course_seq_no"));
                    m.put("course_name", rs.getString("course_name"));
                    String avg = rs.getString("avg_score");
                    m.put("avg_score", avg == null ? "暂无成绩" : String.format("%.2f", Double.parseDouble(avg)));
                    list.add(m);
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    // ========== 3. 校验课程是否属于该教师 ==========
    public boolean courseBelongsToTeacher(String courseId, String teacherNo) {
        String sql = "SELECT 1 FROM course_teacher ct " +
                "JOIN sys_user u ON ct.teacher_id = u.id " +
                "WHERE ct.course_id=? AND u.user_no=?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, courseId);
            ps.setString(2, teacherNo);
            try (ResultSet rs = ps.executeQuery()) { return rs.next(); }
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    // ========== 4. 某课程下学生+成绩（支持模糊搜索）==========
    // 返回 null 表示课程不属于该教师（越权）
    public List<Map<String, String>> queryStudentsByCourseAndTeacher(
            String courseId, String teacherNo, String keyword) {
        if (!courseBelongsToTeacher(courseId, teacherNo)) return null;

        List<Map<String, String>> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT cs.id AS relation_id, u.user_no, u.real_name, cs.score " +
                        "FROM course_student cs " +
                        "JOIN sys_user u ON cs.student_id = u.id " +
                        "WHERE cs.course_id=? ");
        boolean hasKw = keyword != null && !keyword.trim().isEmpty();
        if (hasKw) sql.append("AND (u.user_no LIKE ? OR u.real_name LIKE ?) ");
        sql.append("ORDER BY u.user_no");
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            ps.setString(1, courseId);
            if (hasKw) {
                String like = "%" + keyword.trim() + "%";
                ps.setString(2, like);
                ps.setString(3, like);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, String> m = new HashMap<>();
                    m.put("relation_id", rs.getString("relation_id"));
                    m.put("user_no", rs.getString("user_no"));
                    m.put("real_name", rs.getString("real_name"));
                    String score = rs.getString("score");
                    m.put("score", score == null ? "null" : score);
                    list.add(m);
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    // ========== 5. 教师改成绩（校验课程归属 + 0~100）==========
    // 返回 null 成功，否则错误信息
    public String updateScoreByTeacher(String relationId, String teacherNo, String score) {
        // 先查这条 relation 属于哪门课，再校验是否该教师的课
        String checkSql = "SELECT cs.course_id FROM course_student cs WHERE cs.id=?";
        try (Connection conn = getConnection()) {
            String courseId;
            try (PreparedStatement ps = conn.prepareStatement(checkSql)) {
                ps.setString(1, relationId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) return "找不到该选课记录";
                    courseId = rs.getString("course_id");
                }
            }
            if (!courseBelongsToTeacher(courseId, teacherNo)) return "无权修改该课程成绩";

            // 校验成绩
            boolean clear = (score == null || score.trim().isEmpty());
            if (!clear) {
                try {
                    double d = Double.parseDouble(score.trim());
                    if (d < 0 || d > 100) return "成绩必须在 0~100 之间";
                } catch (NumberFormatException e) {
                    return "成绩必须是数字";
                }
            }

            String sql = "UPDATE course_student SET score=? WHERE id=?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                if (clear) ps.setNull(1, Types.DECIMAL);
                else ps.setBigDecimal(1, new java.math.BigDecimal(score.trim()));
                ps.setString(2, relationId);
                return ps.executeUpdate() > 0 ? null : "更新失败";
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "数据库错误：" + e.getMessage();
        }
    }
}