package org.example;

public class Clazz {
    private int id;
    private String classNo;
    private String className;
    private Integer headTeacherId;   // 可能为 NULL
    private String headTeacherNo;    // JOIN 出来，展示用
    private String headTeacherName;  // JOIN 出来，展示用

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getClassNo() { return classNo; }
    public void setClassNo(String classNo) { this.classNo = classNo; }
    public String getClassName() { return className; }
    public void setClassName(String className) { this.className = className; }
    public Integer getHeadTeacherId() { return headTeacherId; }
    public void setHeadTeacherId(Integer headTeacherId) { this.headTeacherId = headTeacherId; }
    public String getHeadTeacherNo() { return headTeacherNo; }
    public void setHeadTeacherNo(String headTeacherNo) { this.headTeacherNo = headTeacherNo; }
    public String getHeadTeacherName() { return headTeacherName; }
    public void setHeadTeacherName(String headTeacherName) { this.headTeacherName = headTeacherName; }
}