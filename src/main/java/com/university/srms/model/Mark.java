package com.university.srms.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

// ============================================================
// Mark — one exam type entry per student per course
// ============================================================
public class Mark {

    private int markId;
    private int studentId;
    private String studentName;
    private String enrollmentNo;
    private int courseId;
    private String courseCode;
    private String courseName;
    private int sessionId;
    private String sessionName;
    private int examTypeId;
    private String examTypeName;
    private BigDecimal marksObtained;
    private int maxMarks;
    private boolean isAbsent;
    private int enteredBy;
    private LocalDateTime enteredAt;
    private String remarks;

    public Mark() {}

    // ---- Getters & Setters ----
    public int getMarkId()                        { return markId; }
    public void setMarkId(int m)                  { this.markId = m; }
    public int getStudentId()                     { return studentId; }
    public void setStudentId(int s)               { this.studentId = s; }
    public String getStudentName()                { return studentName; }
    public void setStudentName(String s)          { this.studentName = s; }
    public String getEnrollmentNo()               { return enrollmentNo; }
    public void setEnrollmentNo(String e)         { this.enrollmentNo = e; }
    public int getCourseId()                      { return courseId; }
    public void setCourseId(int c)                { this.courseId = c; }
    public String getCourseCode()                 { return courseCode; }
    public void setCourseCode(String c)           { this.courseCode = c; }
    public String getCourseName()                 { return courseName; }
    public void setCourseName(String c)           { this.courseName = c; }
    public int getSessionId()                     { return sessionId; }
    public void setSessionId(int s)               { this.sessionId = s; }
    public String getSessionName()                { return sessionName; }
    public void setSessionName(String s)          { this.sessionName = s; }
    public int getExamTypeId()                    { return examTypeId; }
    public void setExamTypeId(int e)              { this.examTypeId = e; }
    public String getExamTypeName()               { return examTypeName; }
    public void setExamTypeName(String e)         { this.examTypeName = e; }
    public BigDecimal getMarksObtained()          { return marksObtained; }
    public void setMarksObtained(BigDecimal m)    { this.marksObtained = m; }
    public int getMaxMarks()                      { return maxMarks; }
    public void setMaxMarks(int m)                { this.maxMarks = m; }
    public boolean isAbsent()                     { return isAbsent; }
    public void setAbsent(boolean a)              { this.isAbsent = a; }
    public int getEnteredBy()                     { return enteredBy; }
    public void setEnteredBy(int e)               { this.enteredBy = e; }
    public LocalDateTime getEnteredAt()           { return enteredAt; }
    public void setEnteredAt(LocalDateTime e)     { this.enteredAt = e; }
    public String getRemarks()                    { return remarks; }
    public void setRemarks(String r)              { this.remarks = r; }
}
