package org.example;

import java.sql.*;
import java.util.*;

public class ClassDao {

    private static final String URL =
            "jdbc:mysql://localhost:3366/sgs?user=root&password=123&useUnicode=true&characterEncoding=UTF-8";

    static {
        try { Class.forName("com.mysql.cj.jdbc.Driver"); }
        catch (ClassNotFoundException e) { e.printStackTrace(); }
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    // ========== 1. 查询班级列表（支持按 编号/名称/班主任工号/班主任姓名 模糊搜索）==========
    public List<Clazz> queryClasses(String keyword) {
        List<Clazz> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT c.id, c.class_no, c.class_name, c.head_teacher_id, " +
                        "u.user_no AS head_no, u.real_name AS head_name " +
                        "FROM class c LEFT JOIN sys_user u ON c.head_teacher_id = u.id WHERE 1=1 ");
        boolean hasKw = keyword != null && !keyword.trim().isEmpty();
        if (hasKw) {
            sql.append("AND (c.class_no LIKE ? OR c.class_name LIKE ? " +
                    "OR u.user_no LIKE ? OR u.real_name LIKE ?) ");
        }
        sql.append("ORDER BY c.class_no");
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            if (hasKw) {
                String like = "%" + keyword.trim() + "%";
                ps.setString(1, like);
                ps.setString(2, like);
                ps.setString(3, like);
                ps.setString(4, like);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    // ========== 2. 按 ID 查单条 ==========
    public Clazz queryById(String id) {
        String sql = "SELECT c.id, c.class_no, c.class_name, c.head_teacher_id, " +
                "u.user_no AS head_no, u.real_name AS head_name " +
                "FROM class c LEFT JOIN sys_user u ON c.head_teacher_id = u.id WHERE c.id=?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    // ========== 3. 添加班级 ==========
    // 返回 null 表示成功，否则返回错误信息
    public String addClass(String classNo, String className, String teacherNo) {
        return addClassInner(classNo == null ? null : classNo.trim(),
                className == null ? null : className.trim(),
                teacherNo == null ? null : teacherNo.trim());
    }

    // 内部实现（清晰版）
    private String addClassInner(String classNo, String className, String teacherNo) {
        Integer teacherId = null;
        if (teacherNo != null && !teacherNo.isEmpty()) {
            Map<String, Object> r = resolveTeacher(teacherNo);
            if (r.get("error") != null) return (String) r.get("error");
            teacherId = (Integer) r.get("id");
        }
        // 唯一性校验
        if (existsClassNo(classNo, null)) return "班级编号已存在";
        String sql = "INSERT INTO class(class_no, class_name, head_teacher_id) VALUES(?,?,?)";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, classNo);
            ps.setString(2, className);
            if (teacherId == null) ps.setNull(3, Types.INTEGER);
            else ps.setInt(3, teacherId);
            ps.executeUpdate();
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
            return "数据库错误：" + e.getMessage();
        }
    }

    // ========== 4. 修改班级 ==========
    public String updateClass(String id, String classNo, String className, String teacherNo) {
        if (classNo == null || classNo.trim().isEmpty()) return "班级编号不能为空";
        if (className == null || className.trim().isEmpty()) return "班级名称不能为空";
        Integer teacherId = null;
        if (teacherNo != null && !teacherNo.trim().isEmpty()) {
            Map<String, Object> r = resolveTeacher(teacherNo.trim());
            if (r.get("error") != null) return (String) r.get("error");
            teacherId = (Integer) r.get("id");
        }
        if (existsClassNo(classNo.trim(), id)) return "班级编号已存在";
        String sql = "UPDATE class SET class_no=?, class_name=?, head_teacher_id=? WHERE id=?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, classNo.trim());
            ps.setString(2, className.trim());
            if (teacherId == null) ps.setNull(3, Types.INTEGER);
            else ps.setInt(3, teacherId);
            ps.setString(4, id);
            return ps.executeUpdate() > 0 ? null : "未找到该班级";
        } catch (SQLException e) {
            e.printStackTrace();
            return "数据库错误：" + e.getMessage();
        }
    }

    // ========== 5. 删除班级（先把学生 class_id 置 NULL）==========
    public boolean deleteClass(String id) {
        Connection conn = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false);
            try (PreparedStatement ps1 = conn.prepareStatement("UPDATE sys_user SET class_id=NULL WHERE class_id=?")) {
                ps1.setString(1, id); ps1.executeUpdate();
            }
            try (PreparedStatement ps2 = conn.prepareStatement("DELETE FROM class WHERE id=?")) {
                ps2.setString(1, id); ps2.executeUpdate();
            }
            conn.commit();
            return true;
        } catch (SQLException e) {
            if (conn != null) try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            e.printStackTrace();
            return false;
        } finally {
            if (conn != null) try { conn.setAutoCommit(true); conn.close(); } catch (SQLException ex) { ex.printStackTrace(); }
        }
    }

    // ========== 6. 查询某班级的学生列表（支持按学号/姓名模糊搜索）==========
    public List<Map<String, String>> queryStudentsOfClass(String classId, String keyword) {
        List<Map<String, String>> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT id, user_no, real_name FROM sys_user " +
                        "WHERE user_group='学生' AND class_id=? ");
        boolean hasKw = keyword != null && !keyword.trim().isEmpty();
        if (hasKw) sql.append("AND (user_no LIKE ? OR real_name LIKE ?) ");
        sql.append("ORDER BY user_no");
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            ps.setString(1, classId);
            if (hasKw) {
                String like = "%" + keyword.trim() + "%";
                ps.setString(2, like);
                ps.setString(3, like);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, String> m = new HashMap<>();
                    m.put("id", rs.getString("id"));
                    m.put("user_no", rs.getString("user_no"));
                    m.put("real_name", rs.getString("real_name"));
                    list.add(m);
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    // ========== 7. 往班级添加学生（按学号）==========
    public String addStudentToClass(String classId, String studentNo) {
        if (studentNo == null || studentNo.trim().isEmpty()) return "学号不能为空";
        String checkUser = "SELECT id, user_group, class_id FROM sys_user WHERE user_no=?";
        try (Connection conn = getConnection()) {
            int studentId;
            Integer curClassId;
            try (PreparedStatement ps = conn.prepareStatement(checkUser)) {
                ps.setString(1, studentNo.trim());
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) return "找不到该学号的学生";
                    if (!"学生".equals(rs.getString("user_group"))) return "该账号不是学生身份";
                    studentId = rs.getInt("id");
                    curClassId = (Integer) rs.getObject("class_id");
                }
            }
            if (curClassId != null) {
                if (String.valueOf(curClassId).equals(classId)) return "该学生已在本班";
                // 是否允许从别的班"抢"过来？这里选择：不允许，提示先移出原班
                return "该学生已属于其他班级，请先从原班移出";
            }
            try (PreparedStatement ps = conn.prepareStatement("UPDATE sys_user SET class_id=? WHERE id=?")) {
                ps.setString(1, classId);
                ps.setInt(2, studentId);
                ps.executeUpdate();
            }
            return "OK";
        } catch (SQLException e) {
            e.printStackTrace();
            return "数据库错误：" + e.getMessage();
        }
    }

    // ========== 8. 从班级移除学生 ==========
    public boolean removeStudentFromClass(String classId, String studentId) {
        String sql = "UPDATE sys_user SET class_id=NULL WHERE id=? AND class_id=?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, studentId);
            ps.setString(2, classId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    // ========== 9. 查某学生的所有选课与成绩 ==========
    public List<Map<String, String>> queryCoursesOfStudent(String studentId) {
        List<Map<String, String>> list = new ArrayList<>();
        String sql = "SELECT cs.id AS relation_id, c.course_name, c.course_no, c.course_seq_no, cs.score " +
                "FROM course_student cs JOIN course c ON cs.course_id = c.id " +
                "WHERE cs.student_id=? ORDER BY c.course_no, c.course_seq_no";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, String> m = new HashMap<>();
                    m.put("relation_id", rs.getString("relation_id"));
                    m.put("course_name", rs.getString("course_name"));
                    m.put("course_no", rs.getString("course_no"));
                    m.put("course_seq_no", rs.getString("course_seq_no"));
                    String score = rs.getString("score");
                    m.put("score", score == null ? "null" : score);
                    if (score == null) {
                        m.put("pass", "未录入");
                    } else {
                        m.put("pass", Double.parseDouble(score) >= 60 ? "及格" : "不及格");
                    }
                    list.add(m);
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    // ========== 10. 教师查询自己当班主任的班级 ==========
    public List<Clazz> queryClassesByHeadTeacherNo(String teacherNo) {
        List<Clazz> list = new ArrayList<>();
        String sql = "SELECT c.id, c.class_no, c.class_name, c.head_teacher_id, " +
                "u.user_no AS head_no, u.real_name AS head_name " +
                "FROM class c JOIN sys_user u ON c.head_teacher_id = u.id " +
                "WHERE u.user_no=? ORDER BY c.class_no";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, teacherNo);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    // ========== 工具方法 ==========
    private Clazz mapRow(ResultSet rs) throws SQLException {
        Clazz c = new Clazz();
        c.setId(rs.getInt("id"));
        c.setClassNo(rs.getString("class_no"));
        c.setClassName(rs.getString("class_name"));
        Object ht = rs.getObject("head_teacher_id");
        c.setHeadTeacherId(ht == null ? null : ((Number) ht).intValue());
        c.setHeadTeacherNo(rs.getString("head_no"));
        c.setHeadTeacherName(rs.getString("head_name"));
        return c;
    }

    // 判断某教师是否是某班的班主任
    public boolean isHeadTeacherOfClass(String classId, String teacherNo) {
        String sql = "SELECT 1 FROM class c JOIN sys_user u ON c.head_teacher_id = u.id " +
                "WHERE c.id=? AND u.user_no=?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, classId);
            ps.setString(2, teacherNo);
            try (ResultSet rs = ps.executeQuery()) { return rs.next(); }
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    private boolean existsClassNo(String classNo, String excludeId) {
        String sql = excludeId == null
                ? "SELECT id FROM class WHERE class_no=?"
                : "SELECT id FROM class WHERE class_no=? AND id<>?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, classNo);
            if (excludeId != null) ps.setString(2, excludeId);
            try (ResultSet rs = ps.executeQuery()) { return rs.next(); }
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    private Map<String, Object> resolveTeacher(String teacherNo) {
        Map<String, Object> r = new HashMap<>();
        String sql = "SELECT id, user_group FROM sys_user WHERE user_no=?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, teacherNo);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) { r.put("error", "找不到该工号的教师"); return r; }
                if (!"教师".equals(rs.getString("user_group"))) { r.put("error", "该账号不是教师身份"); return r; }
                r.put("id", rs.getInt("id"));
            }
        } catch (SQLException e) { r.put("error", "数据库错误：" + e.getMessage()); }
        return r;
    }
}