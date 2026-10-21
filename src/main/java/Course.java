package org.example;

public class Course {
    private int id;
    private String courseNo;
    private String courseSeqNo;
    private String courseName;
    private String teachers; // 用于展示拼接后的教师名字

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getCourseNo() { return courseNo; }
    public void setCourseNo(String courseNo) { this.courseNo = courseNo; }

    public String getCourseSeqNo() { return courseSeqNo; }
    public void setCourseSeqNo(String courseSeqNo) { this.courseSeqNo = courseSeqNo; }

    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }

    public String getTeachers() { return teachers; }
    public void setTeachers(String teachers) { this.teachers = teachers; }
}