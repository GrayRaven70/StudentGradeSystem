package org.example;

import java.util.Map;
import java.util.HashMap;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CourseDao {

    // 复用你之前的连接信息
    private static final String URL =
            "jdbc:mysql://localhost:3366/sgs?user=root&password=123&useUnicode=true&characterEncoding=UTF-8";

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

    // ========== 1. 查询所有课程（包含拼接后的教师名字）==========
    public List<Course> queryAllCourses() {
        List<Course> list = new ArrayList<>();
        // 核心逻辑：左连接教师表，用 GROUP_CONCAT 把多个老师名字用逗号拼接起来
        String sql = "SELECT c.id, c.course_no, c.course_seq_no, c.course_name, " +
                "GROUP_CONCAT(u.real_name SEPARATOR ', ') AS teachers " +
                "FROM course c " +
                "LEFT JOIN course_teacher ct ON c.id = ct.course_id " +
                "LEFT JOIN sys_user u ON ct.teacher_id = u.id " +
                "GROUP BY c.id " +
                "ORDER BY c.course_no, c.course_seq_no";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Course c = new Course();
                c.setId(rs.getInt("id"));
                c.setCourseNo(rs.getString("course_no"));
                c.setCourseSeqNo(rs.getString("course_seq_no"));
                c.setCourseName(rs.getString("course_name"));
                // 如果没有老师，GROUP_CONCAT 会返回 null
                String teachers = rs.getString("teachers");
                c.setTeachers(teachers == null ? "暂无教师" : teachers);
                list.add(c);
            }
        } catch (SQLException e) {
            System.out.println("[CourseDao] 查询失败：" + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    // ========== 2. 添加课程 ==========
    public boolean addCourse(String courseNo, String courseSeqNo, String courseName) {
        String sql = "insert into course(course_no, course_seq_no, course_name) values(?,?,?)";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, courseNo);
            ps.setString(2, courseSeqNo);
            ps.setString(3, courseName);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ========== 3. 修改课程基本信息 ==========
    public boolean updateCourse(String id, String courseNo, String courseSeqNo, String courseName) {
        String sql = "update course set course_no=?, course_seq_no=?, course_name=? where id=?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, courseNo);
            ps.setString(2, courseSeqNo);
            ps.setString(3, courseName);
            ps.setString(4, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ========== 4. 删除课程（级联删除关联的教师和学生） ==========
    public boolean deleteCourse(String id) {
        String deleteTeacherSql = "delete from course_teacher where course_id=?";
        String deleteStudentSql = "delete from course_student where course_id=?";
        String deleteCourseSql = "delete from course where id=?";

        Connection conn = null;
        try {
            conn = getConnection();
            // 开启事务（保证三个表要么一起删，要么都不删）
            conn.setAutoCommit(false);

            try (PreparedStatement ps1 = conn.prepareStatement(deleteTeacherSql)) {
                ps1.setString(1, id);
                ps1.executeUpdate();
            }
            try (PreparedStatement ps2 = conn.prepareStatement(deleteStudentSql)) {
                ps2.setString(1, id);
                ps2.executeUpdate();
            }
            try (PreparedStatement ps3 = conn.prepareStatement(deleteCourseSql)) {
                ps3.setString(1, id);
                ps3.executeUpdate();
            }

            conn.commit(); // 提交事务
            return true;
        } catch (SQLException e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            e.printStackTrace();
            return false;
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); conn.close(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
        }
    }

    // ========== 5. 查询某门课的所有任课教师 ==========
    public List<Map<String, String>> queryTeachersByCourseId(String courseId) {
        List<Map<String, String>> list = new ArrayList<>();
        // ct.id 是关联表的主键，删除时需要用到
        String sql = "SELECT ct.id as relation_id, u.real_name, u.user_no " +
                "FROM course_teacher ct " +
                "JOIN sys_user u ON ct.teacher_id = u.id " +
                "WHERE ct.course_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, courseId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, String> map = new HashMap<>();
                    map.put("relation_id", rs.getString("relation_id"));
                    map.put("real_name", rs.getString("real_name"));
                    map.put("user_no", rs.getString("user_no"));
                    list.add(map);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // ========== 6. 给课程添加教师 ==========
    public String addTeacherToCourse(String courseId, String teacherNo) {
        // 1. 先去 sys_user 表查这个工号是不是老师
        String checkUserSql = "SELECT id, user_group FROM sys_user WHERE user_no = ?";
        // 2. 检查是否重复添加
        String checkExistSql = "SELECT id FROM course_teacher WHERE course_id = ? AND teacher_id = ?";
        // 3. 插入关联表
        String insertSql = "INSERT INTO course_teacher(course_id, teacher_id) VALUES(?, ?)";

        try (Connection conn = getConnection()) {
            int teacherId = -1;
            // 查工号
            try (PreparedStatement ps = conn.prepareStatement(checkUserSql)) {
                ps.setString(1, teacherNo);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        if (!"教师".equals(rs.getString("user_group"))) {
                            return "该账号存在，但不是教师身份！";
                        }
                        teacherId = rs.getInt("id");
                    } else {
                        return "找不到该工号的教师！";
                    }
                }
            }
            // 查是否重复
            try (PreparedStatement ps = conn.prepareStatement(checkExistSql)) {
                ps.setString(1, courseId);
                ps.setInt(2, teacherId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return "该教师已经在本课程中了，请勿重复添加！";
                    }
                }
            }
            // 插入
            try (PreparedStatement ps = conn.prepareStatement(insertSql)) {
                ps.setString(1, courseId);
                ps.setInt(2, teacherId);
                ps.executeUpdate();
            }
            return "OK"; // 成功返回 OK
        } catch (SQLException e) {
            e.printStackTrace();
            return "数据库错误：" + e.getMessage();
        }
    }

    // ========== 7. 移除课程教师 ==========
    public boolean removeTeacher(String relationId) {
        String sql = "DELETE FROM course_teacher WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, relationId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ========== 8. 查询某门课的所有学生及成绩 ==========
    public List<Map<String, String>> queryStudentsByCourseId(String courseId) {
        List<Map<String, String>> list = new ArrayList<>();
        // 关联查询 sys_user 拿学号姓名，如果成绩是 NULL 就保持 NULL
        String sql = "SELECT cs.id as relation_id, u.user_no, u.real_name, cs.score " +
                "FROM course_student cs " +
                "JOIN sys_user u ON cs.student_id = u.id " +
                "WHERE cs.course_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, courseId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, String> map = new HashMap<>();
                    map.put("relation_id", rs.getString("relation_id"));
                    map.put("user_no", rs.getString("user_no"));
                    map.put("real_name", rs.getString("real_name"));
                    // getString 在数据库值为 NULL 时会返回 null
                    map.put("score", rs.getString("score") == null ? "null" : rs.getString("score"));
                    list.add(map);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // ========== 9. 给课程添加学生 ==========
    public String addStudentToCourse(String courseId, String studentNo) {
        String checkUserSql = "SELECT id, user_group FROM sys_user WHERE user_no = ?";
        String checkExistSql = "SELECT id FROM course_student WHERE course_id = ? AND student_id = ?";
        String insertSql = "INSERT INTO course_student(course_id, student_id, score) VALUES(?, ?, NULL)";

        try (Connection conn = getConnection()) {
            int studentId = -1;
            try (PreparedStatement ps = conn.prepareStatement(checkUserSql)) {
                ps.setString(1, studentNo);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        if (!"学生".equals(rs.getString("user_group"))) {
                            return "该账号存在，但不是学生身份！";
                        }
                        studentId = rs.getInt("id");
                    } else {
                        return "找不到该学号的学生！";
                    }
                }
            }
            try (PreparedStatement ps = conn.prepareStatement(checkExistSql)) {
                ps.setString(1, courseId);
                ps.setInt(2, studentId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return "该学生已经在本课程中，请勿重复添加！";
                }
            }
            try (PreparedStatement ps = conn.prepareStatement(insertSql)) {
                ps.setString(1, courseId);
                ps.setInt(2, studentId);
                ps.executeUpdate();
            }
            return "OK";
        } catch (SQLException e) {
            e.printStackTrace();
            return "数据库错误：" + e.getMessage();
        }
    }

    // ========== 10. 移除学生 ==========
    public boolean removeStudent(String relationId) {
        String sql = "DELETE FROM course_student WHERE id = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, relationId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ========== 11. 修改成绩 ==========
    public boolean updateScore(String relationId, String score) {
        String sql = "UPDATE course_student SET score = ? WHERE id = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            // 如果传进来的是空字符串，说明要把成绩置为 NULL
            if (score == null || score.trim().isEmpty()) {
                ps.setNull(1, java.sql.Types.DECIMAL);
            } else {
                ps.setBigDecimal(1, new java.math.BigDecimal(score));
            }
            ps.setString(2, relationId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ========== 2. 按 ID 查询单门课程（供修改页面回显）==========
    public Course queryCourseById(String id) {
        String sql = "SELECT * FROM course WHERE id=?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Course c = new Course();
                    c.setId(rs.getInt("id"));
                    c.setCourseNo(rs.getString("course_no"));
                    c.setCourseSeqNo(rs.getString("course_seq_no"));
                    c.setCourseName(rs.getString("course_name"));
                    return c;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}