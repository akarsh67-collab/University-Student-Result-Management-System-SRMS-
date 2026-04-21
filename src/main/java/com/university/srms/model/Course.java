package com.university.srms.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

// ============================================================
// Course
// ============================================================
public class Course {
    private int courseId;
    private String courseCode;
    private String courseName;
    private int programId;
    private int semester;
    private int credits;
    private String courseType;
    private int maxMarks;
    private int passMarks;

    public Course() {}

    public int getCourseId()                    { return courseId; }
    public void setCourseId(int c)              { this.courseId = c; }
    public String getCourseCode()               { return courseCode; }
    public void setCourseCode(String c)         { this.courseCode = c; }
    public String getCourseName()               { return courseName; }
    public void setCourseName(String c)         { this.courseName = c; }
    public int getProgramId()                   { return programId; }
    public void setProgramId(int p)             { this.programId = p; }
    public int getSemester()                    { return semester; }
    public void setSemester(int s)              { this.semester = s; }
    public int getCredits()                     { return credits; }
    public void setCredits(int c)               { this.credits = c; }
    public String getCourseType()               { return courseType; }
    public void setCourseType(String t)         { this.courseType = t; }
    public int getMaxMarks()                    { return maxMarks; }
    public void setMaxMarks(int m)              { this.maxMarks = m; }
    public int getPassMarks()                   { return passMarks; }
    public void setPassMarks(int p)             { this.passMarks = p; }
}

// ============================================================
// SemesterResult
// ============================================================
class SemesterResult {
    private int resultId;
    private int studentId;
    private String studentName;
    private String enrollmentNo;
    private String programName;
    private String deptName;
    private int sessionId;
    private String sessionName;
    private int semester;
    private BigDecimal totalMarks;
    private BigDecimal maxMarks;
    private BigDecimal percentage;
    private BigDecimal sgpa;
    private BigDecimal cgpa;
    private String gradeLetter;
    private String resultStatus;
    private int backSubjects;
    private boolean published;
    private LocalDateTime publishedAt;

    public SemesterResult() {}

    public int getResultId()                        { return resultId; }
    public void setResultId(int r)                  { this.resultId = r; }
    public int getStudentId()                       { return studentId; }
    public void setStudentId(int s)                 { this.studentId = s; }
    public String getStudentName()                  { return studentName; }
    public void setStudentName(String s)            { this.studentName = s; }
    public String getEnrollmentNo()                 { return enrollmentNo; }
    public void setEnrollmentNo(String e)           { this.enrollmentNo = e; }
    public String getProgramName()                  { return programName; }
    public void setProgramName(String p)            { this.programName = p; }
    public String getDeptName()                     { return deptName; }
    public void setDeptName(String d)               { this.deptName = d; }
    public int getSessionId()                       { return sessionId; }
    public void setSessionId(int s)                 { this.sessionId = s; }
    public String getSessionName()                  { return sessionName; }
    public void setSessionName(String s)            { this.sessionName = s; }
    public int getSemester()                        { return semester; }
    public void setSemester(int s)                  { this.semester = s; }
    public BigDecimal getTotalMarks()               { return totalMarks; }
    public void setTotalMarks(BigDecimal t)         { this.totalMarks = t; }
    public BigDecimal getMaxMarks()                 { return maxMarks; }
    public void setMaxMarks(BigDecimal m)           { this.maxMarks = m; }
    public BigDecimal getPercentage()               { return percentage; }
    public void setPercentage(BigDecimal p)         { this.percentage = p; }
    public BigDecimal getSgpa()                     { return sgpa; }
    public void setSgpa(BigDecimal s)               { this.sgpa = s; }
    public BigDecimal getCgpa()                     { return cgpa; }
    public void setCgpa(BigDecimal c)               { this.cgpa = c; }
    public String getGradeLetter()                  { return gradeLetter; }
    public void setGradeLetter(String g)            { this.gradeLetter = g; }
    public String getResultStatus()                 { return resultStatus; }
    public void setResultStatus(String r)           { this.resultStatus = r; }
    public int getBackSubjects()                    { return backSubjects; }
    public void setBackSubjects(int b)              { this.backSubjects = b; }
    public boolean isPublished()                    { return published; }
    public void setPublished(boolean p)             { this.published = p; }
    public LocalDateTime getPublishedAt()           { return publishedAt; }
    public void setPublishedAt(LocalDateTime p)     { this.publishedAt = p; }
}

// ============================================================
// ReportCard (composite object — student + marks + result)
// ============================================================
class ReportCard {
    private Student student;
    private int semester;
    private String sessionName;
    private List<SubjectMark> subjectMarks;
    private SemesterResult semesterResult;

    public ReportCard() {}

    public Student getStudent()                         { return student; }
    public void setStudent(Student s)                   { this.student = s; }
    public int getSemester()                            { return semester; }
    public void setSemester(int s)                      { this.semester = s; }
    public String getSessionName()                      { return sessionName; }
    public void setSessionName(String s)                { this.sessionName = s; }
    public List<SubjectMark> getSubjectMarks()          { return subjectMarks; }
    public void setSubjectMarks(List<SubjectMark> m)    { this.subjectMarks = m; }
    public SemesterResult getSemesterResult()           { return semesterResult; }
    public void setSemesterResult(SemesterResult r)     { this.semesterResult = r; }
}

// ============================================================
// SubjectMark (per-course aggregate for report card)
// ============================================================
class SubjectMark {
    private String courseCode;
    private String courseName;
    private int credits;
    private BigDecimal totalObtained;
    private int maxMarks;
    private BigDecimal percentage;
    private String gradeLetter;
    private BigDecimal gradePoints;
    private String result; // Pass / Fail

    public SubjectMark() {}

    public String getCourseCode()                   { return courseCode; }
    public void setCourseCode(String c)             { this.courseCode = c; }
    public String getCourseName()                   { return courseName; }
    public void setCourseName(String c)             { this.courseName = c; }
    public int getCredits()                         { return credits; }
    public void setCredits(int c)                   { this.credits = c; }
    public BigDecimal getTotalObtained()            { return totalObtained; }
    public void setTotalObtained(BigDecimal t)      { this.totalObtained = t; }
    public int getMaxMarks()                        { return maxMarks; }
    public void setMaxMarks(int m)                  { this.maxMarks = m; }
    public BigDecimal getPercentage()               { return percentage; }
    public void setPercentage(BigDecimal p)         { this.percentage = p; }
    public String getGradeLetter()                  { return gradeLetter; }
    public void setGradeLetter(String g)            { this.gradeLetter = g; }
    public BigDecimal getGradePoints()              { return gradePoints; }
    public void setGradePoints(BigDecimal g)        { this.gradePoints = g; }
    public String getResult()                       { return result; }
    public void setResult(String r)                 { this.result = r; }
}
